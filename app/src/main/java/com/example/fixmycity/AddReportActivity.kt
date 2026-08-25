package com.example.fixmycity

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fixmycity.databinding.ActivityAddReportBinding
import com.example.fixmycity.models.HazardReport
import com.example.fixmycity.repository.ReportRepository
import com.example.fixmycity.utils.CityNeighborhoodHelper
import com.example.fixmycity.utils.Constants
import com.example.fixmycity.utils.ReportValidator
import com.example.fixmycity.utils.SignalManager

class AddReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReportBinding
    private val reportRepository = ReportRepository()

    private var selectedImageUri: Uri? = null
    private var selectedCityIndex: Int = 0
    private var selectedNeighborhoodIndex: Int = 0
    private var selectedCategoryIndex: Int = 0

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivReportImage.setImageURI(it)
            binding.llImagePlaceholder.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.cvImagePicker.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        setupDropdownListeners()

        binding.btnSubmitReport.setOnClickListener {
            validateAndSubmitReport()
        }
    }

    private fun setupDropdownListeners() {
        binding.spCity.setOnItemClickListener { _, _, position, _ ->
            selectedCityIndex = position
            binding.tilCity.error = null
            CityNeighborhoodHelper.updateNeighborhoodDropdown(
                context = this,
                autoCompleteTextView = binding.spNeighborhood,
                cityIndex = position
            )

            selectedNeighborhoodIndex = 0
        }

        binding.spNeighborhood.setOnItemClickListener { _, _, position, _ ->
            selectedNeighborhoodIndex = position
            binding.tilNeighborhood.error = null
        }

        binding.spCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryIndex = position
            binding.tilCategory.error = null
        }
    }

    private fun validateAndSubmitReport() {
        val description = binding.etReportDescription.text.toString().trim()
        val address = binding.etReportAddress.text.toString().trim()
        val cityText = binding.spCity.text.toString().trim()
        val neighborhoodText = binding.spNeighborhood.text.toString().trim()
        val categoryText = binding.spCategory.text.toString().trim()

        val imageError = ReportValidator.validateImage(selectedImageUri)
        val descriptionError = ReportValidator.validateDescription(description)
        val cityError = ReportValidator.validateCitySelection(selectedCityIndex)
        val neighborhoodError = ReportValidator.validateNeighborhoodSelection(selectedNeighborhoodIndex)
        val categoryError = ReportValidator.validateCategorySelection(selectedCategoryIndex)
        val finalAddress = ReportValidator.validateAddress(address)

        binding.tilReportDescription.error = descriptionError
        binding.tilCity.error = cityError
        binding.tilNeighborhood.error = neighborhoodError
        binding.tilCategory.error = categoryError

        if (imageError != null) {
            SignalManager.getInstance().toast(imageError)
        }

        if (imageError != null || descriptionError != null || cityError != null ||
            neighborhoodError != null || categoryError != null) {
            return
        }

        binding.btnSubmitReport.isEnabled = false
        SignalManager.getInstance().toast("מעלה תמונה ושומר דיווח...")

        uploadImageAndSaveReport(description, cityText, neighborhoodText, finalAddress, categoryText)
    }

    private fun uploadImageAndSaveReport(
        description: String,
        city: String,
        neighborhood: String,
        address: String,
        category: String
    ) {
        val imageUri = selectedImageUri ?: return
        val currentUserId = reportRepository.getCurrentUserId()

        reportRepository.uploadReportImage(
            imageUri = imageUri,
            onSuccess = { downloadUrl ->

                val report = HazardReport(
                    description = description,
                    cityName = city,
                    neighborhood = neighborhood,
                    address = address,
                    category = category,
                    imageUrl = downloadUrl,
                    status = Constants.Status.STATUS_RECEIVED,
                    upVotedCount = 0,
                    reporterUserId = currentUserId,
                    timestamp = System.currentTimeMillis()
                )

                reportRepository.saveReport(
                    report = report,
                    onSuccess = {
                        SignalManager.getInstance().toast("הדיווח נשלח בהצלחה!")
                        finish()
                    },
                    onFailure = { e ->
                        handleFailure("שגיאה בשמירת הדיווח ב-Firestore: ${e.message}")
                    }
                )
            },
            onFailure = { e ->
                handleFailure("שגיאה בהעלאת התמונה ל-Storage: ${e.message}")
            }
        )
    }

    private fun handleFailure(errorMessage: String) {
        binding.btnSubmitReport.isEnabled = true
        SignalManager.getInstance().toast(errorMessage, SignalManager.ToastLength.LONG)
    }
}