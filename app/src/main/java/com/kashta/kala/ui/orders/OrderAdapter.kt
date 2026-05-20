package com.kashta.kala.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kashta.kala.R
import com.kashta.kala.data.Order
import com.kashta.kala.databinding.ItemOrderBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.VH>() {

    private var orders: List<Order> = emptyList()

    inner class VH(val b: ItemOrderBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = orders[position]
        holder.b.tvOrderId.text          = "#ORD-${order.id}"
        holder.b.tvOrderProductName.text = order.productName
        holder.b.tvOrderDate.text        = order.orderDate
        holder.b.tvOrderPrice.text       = "₹${String.format("%,.0f", order.productPrice)}"
        if (order.quantity > 1) holder.b.tvOrderQty.text = "Qty: ${order.quantity}"
        else holder.b.tvOrderQty.visibility = View.GONE

        if (order.imageResId != null) {
            Glide.with(holder.itemView.context)
                .load(order.imageResId).centerCrop()
                .placeholder(R.drawable.ic_photo)
                .into(holder.b.ivOrderProduct)
        } else {
            holder.b.ivOrderProduct.setImageResource(R.drawable.ic_photo)
        }

        // Status tracker steps
        val steps = listOf("Pending", "Processing", "Shipped", "Delivered")
        val currentStep = steps.indexOf(order.status).coerceAtLeast(0)

        val stepViews = listOf(
            holder.b.stepPending, holder.b.stepProcessing,
            holder.b.stepShipped, holder.b.stepDelivered
        )
        val lineViews = listOf(
            holder.b.line1, holder.b.line2, holder.b.line3
        )

        stepViews.forEachIndexed { i, stepView ->
            when {
                i < currentStep  -> stepView.setBackgroundResource(R.drawable.bg_step_done)
                i == currentStep -> stepView.setBackgroundResource(R.drawable.bg_step_active)
                else             -> stepView.setBackgroundResource(R.drawable.bg_step_inactive)
            }
        }
        lineViews.forEachIndexed { i, line ->
            line.setBackgroundResource(
                if (i < currentStep) R.color.walnut_dark else R.color.divider_light
            )
        }

        // Status badge colour
        val (bg, fg) = when (order.status) {
            "Pending"    -> Pair(0xFFFFF3E0.toInt(), 0xFFE65100.toInt())
            "Processing" -> Pair(0xFFE3F2FD.toInt(), 0xFF1565C0.toInt())
            "Shipped"    -> Pair(0xFFEDE7F6.toInt(), 0xFF4527A0.toInt())
            else         -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
        }
        holder.b.tvOrderStatus.setBackgroundColor(bg)
        holder.b.tvOrderStatus.setTextColor(fg)
        holder.b.tvOrderStatus.text = order.status
    }

    override fun getItemCount() = orders.size

    fun updateList(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }
}
