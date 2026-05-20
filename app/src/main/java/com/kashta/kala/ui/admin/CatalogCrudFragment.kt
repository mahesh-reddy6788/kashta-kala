package com.kashta.kala.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Product
import com.kashta.kala.databinding.FragmentCatalogCrudBinding
import com.kashta.kala.databinding.ItemAdminProductBinding

class CatalogCrudFragment : Fragment() {

    private var _binding: FragmentCatalogCrudBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAdminProducts.layoutManager = LinearLayoutManager(requireContext())
        
        setupList()

        binding.fabAddProduct.setOnClickListener {
            // Navigate to dynamic Add Product page
            findNavController().navigate(R.id.action_admin_to_add_product)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when popping back from AddProductFragment
        setupList()
    }

    private fun setupList() {
        val list = DataRepository.products
        adapter = AdminProductAdapter(list.toMutableList()) { productToDelete ->
            // Delete operation
            val pos = DataRepository.products.indexOfFirst { it.id == productToDelete.id }
            if (pos != -1) {
                val removed = DataRepository.products.removeAt(pos)
                setupList()
                
                // Show SnackBar with Undo
                Snackbar.make(binding.root, "\"${removed.name}\" deleted ✓", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        DataRepository.products.add(pos, removed)
                        setupList()
                    }
                    .setTextColor(resources.getColor(R.color.cream_white))
                    .setActionTextColor(resources.getColor(R.color.amber_highlight))
                    .setBackgroundTint(resources.getColor(R.color.walnut_darkest))
                    .show()

                // Notify main AdminFragment to update dynamic dashboard statistics!
                (parentFragment as? AdminFragment)?.updateStatsDashboard()
            }
        }
        binding.rvAdminProducts.adapter = adapter
        binding.emptyStateProducts.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AdminProductAdapter(
    private val products: MutableList<Product>,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.VH>() {

    inner class VH(val b: ItemAdminProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = products[position]
        holder.b.tvProductName.text = p.name
        holder.b.tvProductCategory.text = p.category
        holder.b.tvProductPrice.text = "₹ %,.0f".format(p.price)

        if (p.imageResId != null) {
            Glide.with(holder.itemView.context)
                .load(p.imageResId)
                .placeholder(R.drawable.ic_photo)
                .centerCrop()
                .into(holder.b.ivProductImage)
        } else {
            holder.b.ivProductImage.setImageResource(R.drawable.ic_photo)
        }

        holder.b.btnDeleteProduct.setOnClickListener { onDelete(p) }
    }

    override fun getItemCount(): Int = products.size
}
