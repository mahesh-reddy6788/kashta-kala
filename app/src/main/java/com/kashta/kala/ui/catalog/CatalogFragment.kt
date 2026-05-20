package com.kashta.kala.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Product
import com.kashta.kala.databinding.FragmentCatalogBinding

class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CatalogAdapter
    private var selectedCategory = "All"
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-filter from Home category chips
        arguments?.getString("filterCategory")?.let {
            selectedCategory = it
        }

        setupRecyclerView()
        setupCategoryChips()
        setupSearch()
        applyFilters()
    }

    private fun setupRecyclerView() {
        adapter = CatalogAdapter(
            items = listOf(),
            onFavoriteClick = { product ->
                if (DataRepository.isInWishlist(product.id)) {
                    DataRepository.removeFromWishlist(product.id)
                } else {
                    DataRepository.addToWishlist(product)
                }
                applyFilters()
            },
            onItemClick = { product ->
                val bundle = Bundle().apply { putInt("productId", product.id) }
                findNavController().navigate(R.id.action_catalog_to_product_detail, bundle)
            }
        )
        binding.rvCatalog.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCatalog.adapter = adapter
        binding.rvCatalog.setHasFixedSize(true)
    }

    private fun setupCategoryChips() {
        val allCategories = listOf("All") + DataRepository.categories
        allCategories.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = (cat == selectedCategory)
                setChipBackgroundColorResource(R.color.chip_state_list)
                setTextColor(resources.getColorStateList(R.color.chip_text_state_list, null))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.walnut_medium)
            }
            chip.setOnClickListener {
                selectedCategory = cat
                // Uncheck all others
                for (i in 0 until binding.chipGroupFilter.childCount) {
                    (binding.chipGroupFilter.getChildAt(i) as? Chip)?.isChecked = false
                }
                chip.isChecked = true
                applyFilters()
            }
            binding.chipGroupFilter.addView(chip)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query ?: ""; applyFilters(); return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""; applyFilters(); return true
            }
        })
    }

    private fun applyFilters() {
        var list = DataRepository.getProductsByCategory(selectedCategory)
        if (searchQuery.isNotEmpty()) {
            list = list.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        if (list.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvCatalog.visibility  = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvCatalog.visibility  = View.VISIBLE
            adapter.updateList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
