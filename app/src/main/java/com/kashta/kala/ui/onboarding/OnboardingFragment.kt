package com.kashta.kala.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kashta.kala.R
import com.kashta.kala.databinding.FragmentOnboardingBinding
import com.kashta.kala.databinding.ItemOnboardingSlideBinding
import com.kashta.kala.utils.SessionManager

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    data class Slide(val title: String, val description: String, val imageResId: Int)

    private val slides = listOf(
        Slide(
            "Discover Craftsmanship",
            "Browse our curated catalog of handcrafted furniture — from solid teak sofas to custom dining tables.",
            R.drawable.royal_velvet_sofa
        ),
        Slide(
            "Design Your Vision",
            "Input your exact dimensions and wood type. Get a precise material cost estimate in seconds.",
            R.drawable.modern_oak_table
        ),
        Slide(
            "Order with Confidence",
            "Track every order from workshop to doorstep. Save quotes and revisit your wishlist anytime.",
            R.drawable.imperial_king_bed
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = SlideAdapter(slides)
        setupIndicators(slides.size)
        setCurrentIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)
                if (position == slides.size - 1) {
                    binding.btnNext.text = "Get Started →"
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = "Next →"
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < slides.size - 1) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun setupIndicators(count: Int) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        repeat(count) {
            val dot = ImageView(requireContext()).apply {
                setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_dot_inactive))
                layoutParams = params
            }
            binding.layoutIndicators.addView(dot)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        for (i in 0 until binding.layoutIndicators.childCount) {
            val dot = binding.layoutIndicators.getChildAt(i) as ImageView
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == index) R.drawable.ic_dot_active else R.drawable.ic_dot_inactive
                )
            )
        }
    }

    private fun finishOnboarding() {
        sessionManager.setOnboardingShown()
        findNavController().navigate(R.id.action_onboarding_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SlideAdapter(private val items: List<Slide>) :
        RecyclerView.Adapter<SlideAdapter.SlideVH>() {

        inner class SlideVH(val b: ItemOnboardingSlideBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SlideVH(ItemOnboardingSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: SlideVH, position: Int) {
            val slide = items[position]
            holder.b.tvSlideTitle.text       = slide.title
            holder.b.tvSlideDescription.text = slide.description
            holder.b.ivSlideIllustration.setImageResource(slide.imageResId)
        }

        override fun getItemCount() = items.size
    }
}
