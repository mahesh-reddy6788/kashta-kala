package com.kashta.kala.ui.estimator

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.databinding.FragmentEstimatorBinding
import kotlin.math.ceil

class EstimatorFragment : Fragment() {

    private var _binding: FragmentEstimatorBinding? = null
    private val binding get() = _binding!!

    private var calculatedMaterialCost = 0.0
    private var lastDisplayedCost = 0.0

    private val woodTypes = DataRepository.woodTypes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstimatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWoodTypeDropdown()
        handleArguments()
        setupLiveCalculation()
        setupCalculateButton()
        setupSendToQuoteButton()
    }

    private fun setupWoodTypeDropdown() {
        val items = woodTypes.map { "${it.first}  —  ₹${it.second}/sqft" }
        val adapter = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, items
        )
        binding.actvWoodType.setAdapter(adapter)
        binding.actvWoodType.setText(items[0], false)
        binding.actvWoodType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { recalculateLive() }
        })
    }

    private fun handleArguments() {
        arguments?.getString("furnitureName")?.let {
            binding.etFurnitureName.setText(it)
        }
    }

    private fun setupLiveCalculation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { recalculateLive() }
        }
        binding.etLength.addTextChangedListener(watcher)
        binding.etWidth.addTextChangedListener(watcher)
        binding.etHeight.addTextChangedListener(watcher)
        binding.etThickness.addTextChangedListener(watcher)
    }

    private fun recalculateLive() {
        val l = binding.etLength.text.toString().toDoubleOrNull() ?: return
        val w = binding.etWidth.text.toString().toDoubleOrNull() ?: return
        val h = binding.etHeight.text.toString().toDoubleOrNull() ?: return

        val area   = l * w
        val volume = l * w * h
        val planks = ceil(volume / 1.5).toInt()
        val price  = getSelectedWoodPrice()
        val cost   = area * price

        binding.liveCalcCard.visibility = View.VISIBLE
        binding.tvLiveArea.text    = "Surface Area: $l × $w = ${String.format("%.2f", area)} sqft"
        binding.tvLiveVolume.text  = "Volume: $l × $w × $h = ${String.format("%.2f", volume)} cuft"
        binding.tvLivePlanks.text  = "Planks Required: ⌈$volume / 1.5⌉ = $planks planks"
        binding.tvLiveCost.text    = "Material Cost: ${String.format("%.2f", area)} × ₹$price = ₹${String.format("%.2f", cost)}"
    }

    private fun getSelectedWoodPrice(): Double {
        val text = binding.actvWoodType.text.toString()
        val name = text.substringBefore("  —").trim()
        return woodTypes.find { it.first == name }?.second ?: woodTypes[0].second
    }

    private fun getSelectedWoodName(): String {
        val text = binding.actvWoodType.text.toString()
        return text.substringBefore("  —").trim().ifEmpty { woodTypes[0].first }
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.setOnClickListener {
            val name = binding.etFurnitureName.text.toString().trim()
            val l    = binding.etLength.text.toString().toDoubleOrNull()
            val w    = binding.etWidth.text.toString().toDoubleOrNull()
            val h    = binding.etHeight.text.toString().toDoubleOrNull()
            val t    = binding.etThickness.text.toString().toDoubleOrNull()

            if (name.isEmpty()) {
                binding.tilFurnitureName.error = "Enter furniture name"; return@setOnClickListener
            }
            if (l == null || w == null || h == null || t == null || l <= 0 || w <= 0 || h <= 0 || t <= 0) {
                Snackbar.make(binding.root, "Please fill all dimensions with valid values", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.tilFurnitureName.error = null

            val area   = l * w
            val volume = l * w * h
            val planks = ceil(volume / 1.5).toInt()
            val price  = getSelectedWoodPrice()
            val cost   = area * price
            calculatedMaterialCost = cost

            // Animate result card in
            binding.resultCard.visibility = View.VISIBLE
            binding.resultCard.alpha = 0f
            binding.resultCard.translationY = 60f
            binding.resultCard.animate().alpha(1f).translationY(0f).setDuration(350).start()

            binding.tvResultWoodType.text = "Wood Type: ${getSelectedWoodName()}"
            binding.tvResultArea.text     = "Area: ${String.format("%.2f", area)} sqft"
            binding.tvResultVolume.text   = "Volume: ${String.format("%.2f", volume)} cuft"
            binding.tvResultPlanks.text   = "Planks Required: $planks"

            // Animated count-up for cost
            animateCost(lastDisplayedCost, cost)
            lastDisplayedCost = cost

            // Scroll to result
            binding.scrollView.postDelayed({
                binding.scrollView.smoothScrollTo(0, binding.resultCard.top)
            }, 200)
        }
    }

    private fun animateCost(from: Double, to: Double) {
        ValueAnimator.ofFloat(from.toFloat(), to.toFloat()).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val v = it.animatedValue as Float
                binding.tvResultCost.text = "₹${String.format("%,.2f", v)}"
            }
            start()
        }
    }

    private fun setupSendToQuoteButton() {
        binding.btnSendToQuote.setOnClickListener {
            if (calculatedMaterialCost == 0.0) {
                Snackbar.make(binding.root, "Please calculate first", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString("furnitureName",  binding.etFurnitureName.text.toString())
                putString("woodType",       getSelectedWoodName())
                putDouble("materialCost",   calculatedMaterialCost)
                putDouble("length",         binding.etLength.text.toString().toDoubleOrNull() ?: 0.0)
                putDouble("width",          binding.etWidth.text.toString().toDoubleOrNull() ?: 0.0)
                putDouble("height",         binding.etHeight.text.toString().toDoubleOrNull() ?: 0.0)
                putDouble("thickness",      binding.etThickness.text.toString().toDoubleOrNull() ?: 1.5)
            }
            findNavController().navigate(R.id.action_estimator_to_quote, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
