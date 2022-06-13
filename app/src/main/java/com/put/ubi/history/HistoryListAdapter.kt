package com.put.ubi.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.put.ubi.R
import com.put.ubi.databinding.ItemHistoryBinding
import com.put.ubi.model.PaymentSource
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class HistoryListAdapter() :
    ListAdapter<PaymentWithPrice, HistoryListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(paymentWithPrice: PaymentWithPrice) {
            binding.apply {
                itemHistoryDateValue.text = formatDate(paymentWithPrice.fund.date)
                val context = binding.root.context

                itemHistoryPriceValue.text = context.getString(R.string.money_with_currency, formatMoney(paymentWithPrice.price))
                itemHistoryAmountValue.text = paymentWithPrice.fund.value.toString()
                itemHistorySourceValue.text = when (paymentWithPrice.fund.source) {
                    PaymentSource.INDIVIDUAL -> context.getString(R.string.history_individual)
                    PaymentSource.COMPANY -> context.getString(R.string.history_company)
                    PaymentSource.COUNTRY -> context.getString(R.string.history_country)
                }
            }
        }
    }

    private fun formatMoney(payments: BigDecimal): String? {
        val decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = ' '
        }
        return DecimalFormat("#,##0.00", decimalFormatSymbols).format(payments)
    }

    private fun formatDate(date: Date) =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PaymentWithPrice>() {
            override fun areItemsTheSame(
                oldItem: PaymentWithPrice,
                newItem: PaymentWithPrice
            ): Boolean {
                return oldItem.fund.id == newItem.fund.id
            }

            override fun areContentsTheSame(
                oldItem: PaymentWithPrice,
                newItem: PaymentWithPrice
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
