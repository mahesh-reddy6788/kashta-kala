package com.kashta.kala.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.FavoriteItem
import com.kashta.kala.data.Order
import com.kashta.kala.databinding.FragmentWishlistBinding
import com.kashta.kala.databinding.ItemWishlistBinding
import java.text.SimpleDateFormat
import java.util.*

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WishlistAdapter(
            onRemove = { item ->
                DataRepository.removeFromWishlist(item.productId)
                loadWishlist()
                Snackbar.make(binding.root, "Removed from wishlist", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        val product = DataRepository.getProductById(item.productId)
                        if (product != null) DataRepository.addToWishlist(product)
                        loadWishlist()
                    }.show()
            },
            onOrderNow = { item ->
                val order = Order(
                    id           = DataRepository.nextOrderId(),
                    productName  = item.name,
                    productPrice = item.price,
                    status       = "Pending",
                    orderDate    = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                    imageResId   = item.imageResId,
                    quantity     = 1
                )
                DataRepository.addOrder(order)
                Snackbar.make(binding.root, "Order placed! 🎉", Snackbar.LENGTH_LONG)
                    .setAction("View Orders") {
                        findNavController().navigate(R.id.navigation_orders)
                    }.show()
            }
        )
        binding.rvWishlist.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvWishlist.adapter = adapter

        binding.btnBrowseCatalog.setOnClickListener {
            findNavController().navigate(R.id.navigation_catalog)
        }

        loadWishlist()
    }

    override fun onResume() {
        super.onResume()
        loadWishlist()
    }

    private fun loadWishlist() {
        val items = DataRepository.wishlist.toList()
        if (items.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvWishlist.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvWishlist.visibility = View.VISIBLE
            adapter.updateList(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Adapter ──────────────────────────────────────────────────────────────────
class WishlistAdapter(
    private val onRemove: (FavoriteItem) -> Unit,
    private val onOrderNow: (FavoriteItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.VH>() {

    private var items: List<FavoriteItem> = emptyList()

    inner class VH(val b: ItemWishlistBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvWishlistName.text     = item.name
        holder.b.tvWishlistCategory.text = item.category
        holder.b.tvWishlistPrice.text    = "₹${String.format("%,.0f", item.price)}"

        if (item.imageResId != null) {
            Glide.with(holder.itemView.context)
                .load(item.imageResId).centerCrop()
                .placeholder(R.drawable.ic_photo)
                .into(holder.b.ivWishlistImage)
        } else {
            holder.b.ivWishlistImage.setImageResource(R.drawable.ic_photo)
        }

        holder.b.btnRemoveWishlist.setOnClickListener {
            holder.b.btnRemoveWishlist.animate()
                .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                .withEndAction {
                    holder.b.btnRemoveWishlist.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }.start()
            onRemove(item)
        }
        holder.b.btnOrderNow.setOnClickListener { onOrderNow(item) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<FavoriteItem>) {
        items = newList
        notifyDataSetChanged()
    }
}
