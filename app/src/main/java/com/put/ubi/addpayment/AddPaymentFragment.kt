package com.put.ubi.addpayment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.put.ubi.R
import com.put.ubi.databinding.FragmentAddPaymentBinding
import com.put.ubi.util.DecimalDigitsInputFilter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddPaymentFragment : Fragment(R.layout.fragment_add_payment) {
    @Inject
    lateinit var addPaymentViewModelFactory: AddPaymentViewModelFactory
    private val viewModel: AddPaymentViewModel by viewModels {
        AddPaymentViewModel.provideFactory(addPaymentViewModelFactory, args.type)
    }
    private val args: AddPaymentFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentAddPaymentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val paymentType = args.type
            countryPaymentAmountTextInputLayout.isVisible = paymentType == AddPaymentType.Country
            employeePaymentAmountTextInputLayout.isVisible = paymentType == AddPaymentType.Personal
            employerPaymentAmountTextInputLayout.isVisible = paymentType == AddPaymentType.Personal

            val filters = arrayOf(DecimalDigitsInputFilter(12, 2))
            countryPaymentAmountEditText.filters = filters
            employeePaymentAmountEditText.filters = filters
            employerPaymentAmountEditText.filters = filters

            dateEditText.setOnClickListener {
                showDateDialog()
            }

            countryPaymentAmountEditText.doAfterTextChanged {
                it?.let {
                    viewModel.setCountryPaymentValue(
                        it.toString()
                    )
                }
            }
            employeePaymentAmountEditText.doAfterTextChanged {
                it?.let {
                    viewModel.setEmployeePaymentValue(
                        it.toString()
                    )
                }
            }
            employerPaymentAmountEditText.doAfterTextChanged {
                it?.let {
                    viewModel.setEmployerPaymentValue(
                        it.toString()
                    )
                }
            }

            addButton.setOnClickListener { viewModel.onConfirm() }
            retryButton.setOnClickListener { viewModel.loadUnitValues() }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.date.collect {
                        binding.dateEditText.setText(it?.let { formatDate(it) } ?: "")
                    }
                }
                launch {
                    viewModel.unitValue.collect {
                        binding.unitValue.text =
                            getString(R.string.add_payment_unit_value, it.toString())
                    }
                }
                launch {
                    viewModel.employeePaymentValue.collect {
                        with(binding) {
                            val currentValue =
                                employeePaymentAmountEditText.text.toString().toBigDecimalOrNull()
                            if (currentValue != it) binding.employeePaymentAmountEditText.setText(
                                it?.toPlainString() ?: ""
                            )
                        }
                    }
                }
                launch {
                    viewModel.employerPaymentValue.collect {
                        with(binding) {
                            val currentValue =
                                employerPaymentAmountEditText.text.toString().toBigDecimalOrNull()
                            if (currentValue != it) binding.employerPaymentAmountEditText.setText(
                                it?.toPlainString() ?: ""
                            )
                        }
                    }
                }
                launch {
                    viewModel.employeePaymentError.collect {
                        binding.employeePaymentAmountTextInputLayout.error = if (it) {
                            getString(R.string.add_payment_provide_correct_value)
                        } else {
                            ""
                        }
                    }
                }
                launch {
                    viewModel.employerPaymentError.collect {
                        binding.employerPaymentAmountTextInputLayout.error = if (it) {
                            getString(R.string.add_payment_provide_correct_value)
                        } else {
                            ""
                        }
                    }
                }
                launch {
                    viewModel.countryPaymentError.collect {
                        binding.countryPaymentAmountTextInputLayout.error = if (it) {
                            getString(R.string.add_payment_provide_correct_value)
                        } else {
                            ""
                        }
                    }
                }
                launch {
                    viewModel.dateError.collect {
                        binding.dateTextInputLayout.error = if (it) {
                            getString(R.string.add_payment_provide_correct_value)
                        } else {
                            ""
                        }
                    }
                }
                launch {
                    viewModel.loadingError.collect {
                        binding.error.isVisible = it
                    }
                }
                launch {
                    viewModel.loading.collect {
                        binding.loading.isVisible = it
                    }
                }
                launch {
                    viewModel.success.collect {
                        findNavController().navigate(AddPaymentFragmentDirections.actionAddPaymentFragmentToMainTabsFragment())
                    }
                }
            }
        }
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR);
        val currentMonth = calendar.get(Calendar.MONTH);
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            viewModel.setDate(year, month, dayOfMonth)
        }, currentYear, currentMonth, currentDay).apply {
            datePicker.maxDate = with(calendar) {
                timeInMillis
            }
        }.show()
    }

    private fun formatDate(date: Date) =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}

enum class AddPaymentType {
    Personal, Country
}