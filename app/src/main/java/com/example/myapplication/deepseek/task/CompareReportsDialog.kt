package com.example.lotteryprediction.deepseek.task

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lotteryprediction.R
import com.example.lotteryprediction.databinding.DialogCompareReportsBinding

class CompareReportsDialog : DialogFragment() {
    private lateinit var binding: DialogCompareReportsBinding
    private var listener: OnCompareListener? = null

    interface OnCompareListener {
        fun onCompareSelected(report1Id: String, report2Id: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? OnCompareListener ?: context as? OnCompareListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCompareReportsBinding.inflate(LayoutInflater.from(context))
        
        val reports = ReportHistoryManager.getHistoryReports(requireContext())
        val reportNames = reports.map { it.first.replace("report_", "").replace(".txt", "") }
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, reportNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerReport1.adapter = adapter
        binding.spinnerReport2.adapter = adapter

        return AlertDialog.Builder(requireContext()).apply {
            setView(binding.root)
            setTitle(R.string.report_compare_title)
            setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                if (binding.spinnerReport1.selectedItemPosition == binding.spinnerReport2.selectedItemPosition) {
                    Toast.makeText(context, "Please select different reports", Toast.LENGTH_SHORT).show()
                } else {
                    val report1Id = reports[binding.spinnerReport1.selectedItemPosition].first
                    val report2Id = reports[binding.spinnerReport2.selectedItemPosition].first
                    listener?.onCompareSelected(report1Id, report2Id)
                }
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
    }

    companion object {
        const val TAG = "CompareReportsDialog"
    }
}
