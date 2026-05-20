package com.kashta.kala.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Product
import com.kashta.kala.databinding.FragmentHomeBinding
import com.kashta.kala.databinding.ItemFeaturedCardBinding
import com.kashta.kala.databinding.ItemHeroBannerBinding
import com.kashta.kala.utils.SessionManager
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Redirect if not set up
        if (!sessionManager.isOnboardingShown()) {
            findNavController().navigate(R.id.action_home_to_onboarding)
            return
        }
        if (!sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_home_to_login)
            return
        }
        if (sessionManager.isAdmin()) {
            findNavController().navigate(R.id.navigation_admin)
            return
        }

        setupGreeting()
        setupHeroBanner()
        setupFeaturedProducts()
        setupCategoryChips()
        setupQuoteBanner()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else      -> "Good evening"
        }
        val name = sessionManager.getUserName()
        binding.tvGreeting.text = "$greeting, $name 👋"
    }

    private fun setupHeroBanner() {
        val featured = DataRepository.getFeaturedProducts()
        val bannerAdapter = HeroBannerAdapter(featured) { product ->
            val bundle = Bundle().apply { putInt("productId", product.id) }
            findNavController().navigate(R.id.action_home_to_product_detail, bundle)
        }
        binding.viewPagerHero.adapter = bannerAdapter

        // Auto-scroll every 4 seconds
        autoScrollRunnable = object : Runnable {
            override fun run() {
                val next = (binding.viewPagerHero.currentItem + 1) % featured.size
                binding.viewPagerHero.setCurrentItem(next, true)
                autoScrollHandler.postDelayed(this, 4000)
            }
        }
        autoScrollHandler.postDelayed(autoScrollRunnable!!, 4000)

        binding.viewPagerHero.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateHeroDots(position, featured.size)
            }
        })
        setupHeroDots(featured.size)
    }

    private fun setupHeroDots(count: Int) {
        binding.heroDots.removeAllViews()
        repeat(count) { i ->
            val dot = View(requireContext()).apply {
                val size = if (i == 0) 24 else 16
                val params = ViewGroup.MarginLayoutParams(size.dpToPx(), 8.dpToPx())
                params.setMargins(4, 0, 4, 0)
                layoutParams = params
                setBackgroundResource(if (i == 0) R.drawable.ic_dot_active else R.drawable.ic_dot_inactive)
            }
            binding.heroDots.addView(dot)
        }
    }

    private fun updateHeroDots(selected: Int, count: Int) {
        for (i in 0 until binding.heroDots.childCount) {
            val dot = binding.heroDots.getChildAt(i)
            val size = if (i == selected) 24 else 16
            val params = dot.layoutParams as ViewGroup.MarginLayoutParams
            params.width = size.dpToPx()
            dot.layoutParams = params
            dot.setBackgroundResource(if (i == selected) R.drawable.ic_dot_active else R.drawable.ic_dot_inactive)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun setupFeaturedProducts() {
        val featured = DataRepository.getFeaturedProducts()
        val adapter = FeaturedProductAdapter(featured) { product ->
            val bundle = Bundle().apply { putInt("productId", product.id) }
            findNavController().navigate(R.id.action_home_to_product_detail, bundle)
        }
        binding.rvFeatured.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeatured.adapter = adapter

        binding.tvSeeAllFeatured.setOnClickListener {
            findNavController().navigate(R.id.navigation_catalog)
        }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipSofas, binding.chipBeds, binding.chipDining,
            binding.chipChairs, binding.chipWardrobes, binding.chipOffice
        )
        val categories = listOf(
            "Sofas", "Beds", "Dining Tables", "Chairs", "Wardrobes", "Office Furniture"
        )
        chips.forEachIndexed { i, chip ->
            chip.setOnClickListener {
                val bundle = Bundle().apply { putString("filterCategory", categories[i]) }
                findNavController().navigate(R.id.navigation_catalog, bundle)
            }
        }
    }

    private fun setupQuoteBanner() {
        binding.cardQuoteBanner.setOnClickListener {
            findNavController().navigate(R.id.navigation_estimator)
        }
    }

    override fun onDestroyView() {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
        super.onDestroyView()
        _binding = null
    }

    // ── Hero Banner Adapter ───────────────────────────────────────────────────
    class HeroBannerAdapter(
        private val items: List<Product>,
        private val onClick: (Product) -> Unit
    ) : RecyclerView.Adapter<HeroBannerAdapter.VH>() {

        inner class VH(val b: ItemHeroBannerBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemHeroBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.tvHeroProductName.text = p.name
            if (p.imageResId != null) {
                Glide.with(holder.itemView).load(p.imageResId)
                    .centerCrop().into(holder.b.ivHeroBanner)
            }
            holder.b.root.setOnClickListener { onClick(p) }
        }

        override fun getItemCount() = items.size
    }

    // ── Featured Product Adapter ──────────────────────────────────────────────
    class FeaturedProductAdapter(
        private val items: List<Product>,
        private val onClick: (Product) -> Unit
    ) : RecyclerView.Adapter<FeaturedProductAdapter.VH>() {

        inner class VH(val b: ItemFeaturedCardBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemFeaturedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.tvFeaturedName.text     = p.name
            holder.b.tvFeaturedMaterial.text = p.material ?: ""
            holder.b.tvFeaturedPrice.text    = "₹${String.format("%,.0f", p.price)}"
            if (p.imageResId != null) {
                Glide.with(holder.itemView).load(p.imageResId)
                    .centerCrop().placeholder(R.drawable.ic_photo)
                    .into(holder.b.ivFeaturedImage)
            }
            holder.b.root.setOnClickListener { onClick(p) }
        }

        override fun getItemCount() = items.size
    }
}
