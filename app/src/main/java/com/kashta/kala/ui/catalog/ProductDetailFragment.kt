package com.kashta.kala.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Order
import com.kashta.kala.databinding.FragmentProductDetailBinding
import com.kashta.kala.databinding.BottomSheetOrderConfirmBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("productId", -1) ?: -1
        val product = DataRepository.getProductById(productId)

        if (product == null) {
            findNavController().popBackStack()
            return
        }

        // Hero image
        if (product.imageResId != null) {
            Glide.with(this).load(product.imageResId).centerCrop()
                .placeholder(R.drawable.ic_photo).into(binding.ivProductHero)
        }

        binding.tvProductName.text     = product.name
        binding.tvProductCategory.text = product.category
        binding.tvProductPrice.text    = "₹${String.format("%,.0f", product.price)}"
        binding.tvProductDescription.text = product.description
        binding.tvMaterialValue.text   = product.material ?: "N/A"
        binding.tvDimensionsValue.text = product.dimensions ?: "N/A"
        binding.tvWarrantyValue.text   = product.warranty ?: "N/A"

        // Back button
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Wishlist toggle
        updateWishlistIcon(product.id)
        binding.btnWishlist.setOnClickListener {
            if (DataRepository.isInWishlist(product.id)) {
                DataRepository.removeFromWishlist(product.id)
                Snackbar.make(binding.root, "Removed from wishlist", Snackbar.LENGTH_SHORT).show()
            } else {
                DataRepository.addToWishlist(product)
                Snackbar.make(binding.root, "Added to wishlist ❤️", Snackbar.LENGTH_SHORT).show()
            }
            updateWishlistIcon(product.id)
        }

        // Estimate button
        binding.btnGetEstimate.setOnClickListener {
            val bundle = Bundle().apply { putString("furnitureName", product.name) }
            findNavController().navigate(R.id.action_product_detail_to_estimator, bundle)
        }

        // Order Now
        binding.btnOrderNow.setOnClickListener {
            showOrderConfirmSheet(product.id, product.name, product.price, product.imageResId)
        }
    }

    private fun updateWishlistIcon(productId: Int) {
        binding.btnWishlist.setImageResource(
            if (DataRepository.isInWishlist(productId)) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun showOrderConfirmSheet(
        productId: Int, name: String, price: Double, imageResId: Int?
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetOrderConfirmBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.tvOrderProductName.text = name
        sheetBinding.tvOrderProductPrice.text = "₹${String.format("%,.0f", price)}"
        if (imageResId != null) {
            Glide.with(this).load(imageResId).centerCrop()
                .into(sheetBinding.ivOrderProduct)
        }

        sheetBinding.btnConfirmOrder.setOnClickListener {
            val order = Order(
                id          = DataRepository.nextOrderId(),
                productName = name,
                productPrice = price,
                status      = "Pending",
                orderDate   = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                imageResId  = imageResId,
                quantity    = 1
            )
            DataRepository.addOrder(order)
            dialog.dismiss()
            Snackbar.make(binding.root, "Order placed successfully! 🎉", Snackbar.LENGTH_LONG)
                .setAction("View Orders") {
                    findNavController().navigate(R.id.navigation_orders)
                }.show()
        }

        sheetBinding.btnCancelOrder.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
