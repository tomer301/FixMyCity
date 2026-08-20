package com.example.fixmycity.repository

import android.net.Uri
import com.example.fixmycity.models.HazardReport
import com.example.fixmycity.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    /*Uploading image into Firebase Storage*/

    fun uploadReportImage(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${Constants.FirebaseCollection.STORAGE_REPORTS_IMAGES}/${UUID.randomUUID()}.jpg"
        val imageRef = storage.reference.child(fileName)

        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /*Saving report object into Firestore*/

    fun saveReport(
        report: HazardReport,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = db.collection(Constants.FirebaseCollection.DB_REPORTS).document()
        val reportWithId = report.copy(id = docRef.id)

        docRef.set(reportWithId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /*Realtime Listener to reports from Firestore*/

    fun listenToReports(
        onReportsUpdated: (List<HazardReport>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(Constants.FirebaseCollection.DB_REPORTS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reports = snapshot.toObjects(HazardReport::class.java)
                    onReportsUpdated(reports)
                }
            }
    }

    /*Atomic adding/remove Upvote of user*/

    fun toggleUpvote(
        report: HazardReport,
        currentUserId: String,
        onFailure: (Exception) -> Unit
    ) {
        if (currentUserId.isBlank() || report.reporterUserId == currentUserId) return

        val docRef = db.collection(Constants.FirebaseCollection.DB_REPORTS).document(report.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            @Suppress("UNCHECKED_CAST")
            val upVotedUserIds = snapshot.get("upVotedUserIds") as? List<String> ?: emptyList()
            val currentCount = snapshot.getLong("upVotedCount") ?: 0L
            val reporterUserId = snapshot.get("reporterUserId") ?: ""

            if (reporterUserId == currentUserId) return@runTransaction

            val isAlreadyUpvoted = upVotedUserIds.contains(currentUserId)

            if (isAlreadyUpvoted) {
                val updatedUserIds = upVotedUserIds - currentUserId
                val newCount = kotlin.math.max(0, currentCount - 1L)

                transaction.update(docRef, "upVotedUserIds", updatedUserIds)
                transaction.update(docRef, "upVotedCount", newCount)
            } else {
                val updatedUserIds = upVotedUserIds + currentUserId
                val newCount = currentCount + 1L

                transaction.update(docRef, "upVotedUserIds", updatedUserIds)
                transaction.update(docRef, "upVotedCount", newCount)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

}