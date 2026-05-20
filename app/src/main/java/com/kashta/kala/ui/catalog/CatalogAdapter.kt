package com.kashta.kala.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Product
import com.kashta.kala.databinding.ItemDesignBinding

class CatalogAdapter(
    private var items: List<Product>,
    private val onFavoriteClick: (Product) -> Unit,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvName.text     = item.name
        holder.binding.tvCategory.text = item.category
        holder.binding.tvPrice.text    = "₹${String.format("%,.0f", item.price)}"

        if (item.imageResId != null) {
            Glide.with(holder.itemView.context)
                .load(item.imageResId)
                .centerCrop()
                .placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_photo)
                .into(holder.binding.ivDesign)
        } else {
            holder.binding.ivDesign.setImageResource(R.drawable.ic_photo)
        }

        val isFav = DataRepository.isInWishlist(item.id)
        holder.binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )

        holder.binding.btnFavorite.setOnClickListener {
            // Heart animation
            holder.binding.btnFavorite.animate()
                .scaleX(1.4f).scaleY(1.4f).setDuration(150)
                .withEndAction {
                    holder.binding.btnFavorite.animate()
                        .scaleX(1f).scaleY(1f).setDuration(200).start()
                }.start()
            onFavoriteClick(item)
        }

        holder.binding.root.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }
}
