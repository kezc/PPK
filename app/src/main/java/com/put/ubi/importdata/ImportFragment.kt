package com.put.ubi.importdata

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.put.ubi.R
import com.put.ubi.databinding.FragmentImportBinding
import com.put.ubi.model.AllUserData
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class ImportFragment : Fragment(R.layout.fragment_import) {
    private val viewModel: ImportViewModel by viewModels()
    private val binding by viewBinding(FragmentImportBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.importData.setOnClickListener {
            openFile()
        }
        binding.choose.setOnClickListener {
            viewModel.choose()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.fund.collect { fund ->
                        binding.chosenFund.text = fund ?: ""
                        binding.summaryGroup.isVisible = fund != null
                    }
                }
                launch {
                    viewModel.ownPayments.collect { payments ->
                        binding.ownPaymentsAmount.text = payments?.let { formatDecimal(it) } ?: "0"
                    }
                }
                launch {
                    viewModel.countryPayments.collect { payments ->
                        binding.countryPaymentsAmount.text = payments?.let { formatDecimal(it) } ?: "0"
                    }
                }
                launch {
                    viewModel.companyPayments.collect { payments ->
                        binding.employerPaymentsAmount.text = payments?.let { formatDecimal(it) } ?: "0"
                    }
                }
                launch {
                    viewModel.success.collect {
                        findNavController().navigate(ImportFragmentDirections.actionImportFragmentToMainTabsFragment())
                    }
                }
            }
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        if (intent.resolveActivity(requireActivity().packageManager) == null) {
            Toast.makeText(
                requireContext(),
                "Could not read",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        onOpenPDFResult.launch(intent)
    }

    private val onOpenPDFResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result?.data?.data
                val filename = uri?.getFileName(requireContext())
                if (filename?.endsWith(".ppk") == false) {
                    Toast.makeText(
                        requireContext(),
                        "Wrong file type",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }
                val json = uri?.let {
                    requireContext().contentResolver
                        .openInputStream(it)
                        ?.bufferedReader()
                        ?.readText()
                }
                val data = json?.let { Gson().fromJson(it, AllUserData::class.java) }
                if (data != null) {
                    viewModel.setLoadedFile(data)
                    Log.d("Import fragment", data.toString())
                } else {
                    Log.e("Import Fragment", "Could not load file")
                }
            } else {
                Log.e("Import Fragment", "Opening pdf results code: ${result.resultCode}")
            }
        }


    private fun Uri.getFileName(context: Context): String? {
        var result: String? = null
        if (scheme == "content") {
            context.contentResolver.query(this, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    cursor.moveToFirst()
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = cursor.getString(index)
                    }
                }
            }
        }

        if (result == null) {
            result = path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    private fun formatDecimal(payments: BigDecimal): String? {
        val decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
        }
        return DecimalFormat("#,##0.00", decimalFormatSymbols).apply { isGroupingUsed = false }
            .format(payments)
    }
}