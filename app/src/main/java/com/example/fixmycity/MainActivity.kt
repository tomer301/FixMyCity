package com.example.fixmycity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fixmycity.adapters.ReportsAdapter
import com.example.fixmycity.auth.LoginActivity
import com.example.fixmycity.databinding.ActivityMainBinding
import com.example.fixmycity.models.HazardReport
import com.example.fixmycity.repository.ReportRepository
import com.example.fixmycity.utils.CityNeighborhoodHelper
import com.example.fixmycity.utils.SignalManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reportsAdapter: ReportsAdapter
    private val reportRepository = ReportRepository()

    private var allReportsList: List<HazardReport> = emptyList()
    private var firestoreListener: ListenerRegistration? = null

    private var selectedCityIndex: Int = 0
    private var selectedNeighborhoodIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        setupFilterDropdowns()

        setupHeaderButtons()

        binding.fabAddReport.setOnClickListener {
            val intent = Intent(this, AddReportActivity::class.java)
            startActivity(intent)
        }

        startListeningToReports()
    }

    private fun setupHeaderButtons() {
        binding.btnLogout.setOnClickListener {
            firestoreListener?.remove()
            firestoreListener = null

            reportRepository.logout()
            SignalManager.getInstance().toast("התנתקת בהצלחה")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        reportsAdapter = ReportsAdapter { clickedReport ->
            handleUpvoteToggle(clickedReport)
        }

        binding.rvReports.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reportsAdapter
        }
    }

    private fun setupFilterDropdowns() {
        binding.spFilterCity.setOnItemClickListener { _, _, position, _ ->
            selectedCityIndex = position
            selectedNeighborhoodIndex = 0

            CityNeighborhoodHelper.updateNeighborhoodDropdown(
                context = this,
                autoCompleteTextView = binding.spFilterNeighborhood,
                cityIndex = position
            )
            applyCurrentFilters()
        }

        binding.spFilterNeighborhood.setOnItemClickListener { _, _, position, _ ->
            selectedNeighborhoodIndex = position
            applyCurrentFilters()
        }
    }

    private fun startListeningToReports() {
        binding.progressBar.visibility = View.VISIBLE

        firestoreListener = reportRepository.listenToReports(
            onReportsUpdated = { reports ->
                binding.progressBar.visibility = View.GONE
                allReportsList = reports
                applyCurrentFilters()
            },
            onError = { exception ->
                binding.progressBar.visibility = View.GONE
                SignalManager.getInstance().toast("שגיאה שטעינת הדיווחים: ${exception.message}")
            }
        )
    }

    private fun applyCurrentFilters() {
        var filteredList = allReportsList

        if (selectedCityIndex > 0) {
            val cities = resources.getStringArray(R.array.cities_array)
            if (selectedCityIndex < cities.size) {
                val selectedCityName = cities[selectedCityIndex]
                filteredList = filteredList.filter { it.cityName == selectedCityName }
            }
        }

        if (selectedNeighborhoodIndex > 0 && selectedCityIndex > 0) {
            val neighborhoods = CityNeighborhoodHelper.getNeighborhoodsForCity(this, selectedCityIndex)
            if (selectedNeighborhoodIndex < neighborhoods.size) {
                val selectedNeighborhoodName = neighborhoods[selectedNeighborhoodIndex]
                filteredList = filteredList.filter { it.neighborhood == selectedNeighborhoodName }
            }
        }

        if (filteredList.isEmpty()) {
            binding.tvEmptyReports.visibility = View.VISIBLE
        } else {
            binding.tvEmptyReports.visibility = View.GONE
        }

        reportsAdapter.updateReports(filteredList)
    }

    private fun handleUpvoteToggle(report: HazardReport) {
        val currentUserId = reportRepository.getCurrentUserId()

        if (report.reporterUserId == currentUserId) {
            SignalManager.getInstance().toast("לא ניתן להצביע לדיווח של עצמך")
            return
        }

        reportRepository.toggleUpvote(
            report = report,
            currentUserId = currentUserId,
            onFailure = { exception ->
                SignalManager.getInstance().toast("שגיאה בעדכון ההצבעה: ${exception.message}")
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }
}