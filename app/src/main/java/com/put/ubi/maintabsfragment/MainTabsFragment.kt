package com.put.ubi.maintabsfragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.put.ubi.R
import com.put.ubi.dashboard.DashboardFragment
import com.put.ubi.databinding.FragmentMainTabsBinding
import com.put.ubi.history.HistoryFragment
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class MainTabsFragment : Fragment(R.layout.fragment_main_tabs) {
    private val binding by viewBinding(FragmentMainTabsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val adapter = MainTabsAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dashboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(MainTabsFragmentDirections.actionMainTabsFragmentToSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class MainTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    val context = fragment.requireContext()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            DASHBOARD_POS -> DashboardFragment()
            HISTORY_POS -> HistoryFragment()
            else -> throw IllegalStateException("Wrong position")
        }
    }

    fun getTitle(position: Int): String {
        return when (position) {
            DASHBOARD_POS -> context.getString(R.string.main_tabs_dashboard)
            HISTORY_POS -> context.getString(R.string.main_tabs_history)
            else -> throw IllegalStateException("Wrong position")
        }
    }

    companion object {
        private const val DASHBOARD_POS = 0
        private const val HISTORY_POS = 1
    }
}
