package com.put.ubi.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.put.ubi.R
import com.put.ubi.databinding.FragmentSettingsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val exportViewModel: ExportViewModel by viewModels()
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.exportDataButton.setOnClickListener {
           exportViewModel.exportData()
       }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    exportViewModel.success.collect {
                        val uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.put.ubi.provider",
                            it
                        )
                        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "*/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                        }
                        startActivity(Intent.createChooser(sharingIntent, "Share using"))
                    }
                }
            }
        }

    }
}