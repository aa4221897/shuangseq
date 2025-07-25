package com.example.lotteryprediction.deepseek.task

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
// 移除BuildConfig导入
import com.example.lotteryprediction.R
import com.example.lotteryprediction.databinding.ActivityFeatureReportBinding
import com.example.lotteryprediction.deepseek.util.LogUtils
import java.io.File

class FeatureReportActivity : AppCompatActivity(), CompareReportsDialog.OnCompareListener {
    private lateinit var binding: ActivityFeatureReportBinding
    private val TAG = "FeatureReport"
    private val historyAdapter = ReportHistoryAdapter(::loadReport)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeatureReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadHistory()
        loadLatestReport()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareReport()
                true
            }
            R.id.action_export_pdf -> {
                exportToPdf()
                true
            }
            R.id.action_compare -> {
                showCompareDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            loadLatestReport()
        }

        binding.historyRecycler.apply {
            layoutManager = LinearLayoutManager(this@FeatureReportActivity)
            adapter = historyAdapter
        }
    }

    private fun loadHistory() {
        val history = ReportHistoryManager.getHistoryReports(this)
        historyAdapter.submitList(history)
        binding.emptyView.visibility = if (history.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun loadLatestReport() {
        binding.swipeRefresh.isRefreshing = true
        
        try {
            val report = ReportHistoryManager.getHistoryReports(this)
                .firstOrNull()?.second ?: "No report available"
            binding.reportText.text = report
            LogUtils.d(TAG, "Latest report loaded")
        } catch (e: Exception) {
            binding.reportText.text = "Error loading report: ${e.message}"
            LogUtils.e(TAG, "Failed to load report", e)
        } finally {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadReport(content: String) {
        binding.reportText.text = content
    }

    private fun shareReport() {
        try {
            val reportFile = File(filesDir, "feature_reports/latest_report.txt")
            if (!reportFile.exists()) {
                return
            }

            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                reportFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "text/plain"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share report via"))
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to share report", e)
            toast("Failed to share report")
        }
    }

    private fun exportToPdf() {
        try {
            val reportFile = File(filesDir, "feature_reports/latest_report.txt")
            if (!reportFile.exists()) {
                toast("No report available to export")
                return
            }

            val content = reportFile.readText()
            val pdfFile = ReportExporter.exportToPdf(this, content)
            
            pdfFile?.let {
                toast("Report exported to ${it.absolutePath}")
                LogUtils.d(TAG, "PDF exported to ${it.absolutePath}")
            } ?: run {
                toast("Failed to export PDF")
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to export PDF", e)
            toast("Failed to export PDF: ${e.message}")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showCompareDialog() {
        val dialog = CompareReportsDialog()
        dialog.show(supportFragmentManager, CompareReportsDialog.TAG)
    }

    private fun showComparisonResult(diffResult: String) {
        binding.reportText.text = diffResult
        toast(getString(R.string.report_comparison_completed))
    }

    override fun onCompareSelected(report1Id: String, report2Id: String) {
        val diffResult = ReportComparator.getFormattedDifferences(this, report1Id, report2Id)
        showComparisonResult(diffResult)
    }
}
