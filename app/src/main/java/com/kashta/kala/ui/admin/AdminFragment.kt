package com.kashta.kala.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.databinding.FragmentAdminBinding
import com.kashta.kala.utils.SessionManager

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdminPagerAdapter(this)
        binding.viewPagerAdmin.adapter = adapter

        TabLayoutMediator(binding.tabLayoutAdmin, binding.viewPagerAdmin) { tab, position ->
            tab.text = when (position) {
                0 -> "Products"
                1 -> "Users"
                else -> "Orders"
            }
        }.attach()

        // Load stats
        updateStatsDashboard()

        // Bind Sign Out action to the Toolbar close button
        binding.btnBack.setOnClickListener {
            sessionManager.logout()
            Toast.makeText(context, "Logged out successfully ✓", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_admin_to_login)
        }
    }

    fun updateStatsDashboard() {
        if (_binding == null) return
        binding.tvStatProductsCount.text = DataRepository.products.size.toString()
        binding.tvStatUsersCount.text    = DataRepository.usersList.size.toString()
        binding.tvStatOrdersCount.text   = DataRepository.orders.size.toString()
        binding.tvStatQuotesCount.text   = DataRepository.quotes.size.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
