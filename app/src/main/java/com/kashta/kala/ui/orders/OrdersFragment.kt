package com.kashta.kala.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupStatusFilter()
        loadOrders("All")

        binding.btnExploreCatalog.setOnClickListener {
            findNavController().navigate(R.id.navigation_catalog)
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter()
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
        binding.rvOrders.setHasFixedSize(false)
    }

    private fun setupStatusFilter() {
        val chips = listOf(
            binding.chipAll, binding.chipPending, binding.chipProcessing,
            binding.chipShipped, binding.chipDelivered
        )
        val statuses = listOf("All", "Pending", "Processing", "Shipped", "Delivered")

        chips.forEachIndexed { i, chip ->
            chip.setOnClickListener {
                chips.forEach { it.isChecked = false }
                chip.isChecked = true
                loadOrders(statuses[i])
            }
        }
        binding.chipAll.isChecked = true
    }

    private fun loadOrders(statusFilter: String) {
        val orders = if (statusFilter == "All") DataRepository.orders.toList()
                     else DataRepository.orders.filter { it.status == statusFilter }

        if (orders.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvOrders.visibility   = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvOrders.visibility   = View.VISIBLE
            adapter.updateList(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
