package com.kashta.kala.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Order
import com.kashta.kala.databinding.FragmentAdminOrdersBinding
import com.kashta.kala.databinding.ItemAdminOrderBinding

class AdminOrdersFragment : Fragment() {

    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdminOrderAdapter { order, newStatus ->
            DataRepository.updateOrderStatus(order.id, newStatus)
            loadOrders()
            Snackbar.make(binding.root, "Order #${order.id} → $newStatus", Snackbar.LENGTH_SHORT).show()
        }
        binding.rvAdminOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminOrders.adapter = adapter
        loadOrders()
    }

    private fun loadOrders() {
        adapter.updateList(DataRepository.orders.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AdminOrderAdapter(
    private val onStatusChange: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.VH>() {

    private var orders: List<Order> = emptyList()
    private val statuses = arrayOf("Pending", "Processing", "Shipped", "Delivered")

    inner class VH(val b: ItemAdminOrderBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = orders[position]
        holder.b.tvAdminOrderId.text      = "#ORD-${order.id}"
        holder.b.tvAdminOrderName.text    = order.productName
        holder.b.tvAdminOrderDate.text    = order.orderDate
        holder.b.tvAdminOrderPrice.text   = "₹${String.format("%,.0f", order.productPrice)}"
        holder.b.tvAdminOrderStatus.text  = order.status

        val (bg, fg) = when (order.status) {
            "Pending"    -> Pair(0xFFFFF3E0.toInt(), 0xFFE65100.toInt())
            "Processing" -> Pair(0xFFE3F2FD.toInt(), 0xFF1565C0.toInt())
            "Shipped"    -> Pair(0xFFEDE7F6.toInt(), 0xFF4527A0.toInt())
            else         -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
        }
        holder.b.tvAdminOrderStatus.setBackgroundColor(bg)
        holder.b.tvAdminOrderStatus.setTextColor(fg)

        if (order.imageResId != null) {
            Glide.with(holder.itemView.context).load(order.imageResId)
                .centerCrop().placeholder(R.drawable.ic_photo)
                .into(holder.b.ivAdminOrderImage)
        } else {
            holder.b.ivAdminOrderImage.setImageResource(R.drawable.ic_photo)
        }

        holder.b.btnUpdateStatus.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Update Order Status")
                .setItems(statuses) { _, which ->
                    onStatusChange(order, statuses[which])
                }.show()
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }
}
