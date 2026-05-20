package com.kashta.kala.ui.quote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Quote
import com.kashta.kala.databinding.FragmentQuoteBinding
import java.text.SimpleDateFormat
import java.util.*

class QuoteFragment : Fragment() {

    private var _binding: FragmentQuoteBinding? = null
    private val binding get() = _binding!!

    private var furnitureName  = ""
    private var woodType       = ""
    private var materialCost   = 0.0
    private var length         = 0.0
    private var width          = 0.0
    private var height         = 0.0
    private var thickness      = 1.5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            furnitureName = it.getString("furnitureName", "")
            woodType      = it.getString("woodType", "")
            materialCost  = it.getDouble("materialCost", 0.0)
            length        = it.getDouble("length", 0.0)
            width         = it.getDouble("width", 0.0)
            height        = it.getDouble("height", 0.0)
            thickness     = it.getDouble("thickness", 1.5)
        }

        populatePreFilled()
        setupListeners()
        calculateTotal()
    }

    private fun populatePreFilled() {
        binding.tvPreFillFurniture.text  = "Furniture: $furnitureName"
        binding.tvPreFillWood.text       = "Wood: $woodType"
        binding.tvPreFillDimensions.text = "Dimensions: ${length}×${width}×${height} ft"
        binding.tvPreFillMaterial.text   = "Material Cost: ₹${String.format("%,.2f", materialCost)}"
        binding.tvQuoteDate.text         = "Quote generated: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}"
        binding.chipStatus.text          = "Draft"
    }

    private fun setupListeners() {
        binding.etDesignFee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { calculateTotal() }
        })

        binding.switchCustomFinish.setOnCheckedChangeListener { _, _ -> calculateTotal() }

        binding.btnSaveQuote.setOnClickListener { saveQuote() }

        binding.btnShareQuote.setOnClickListener { shareQuote() }

        binding.tvEditInEstimator.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun calculateTotal() {
        val designFee     = binding.etDesignFee.text.toString().toDoubleOrNull() ?: 0.0
        val customFinish  = binding.switchCustomFinish.isChecked
        val finishCharge  = if (customFinish) materialCost * 0.15 else 0.0
        val total         = materialCost + designFee + finishCharge

        binding.tvBreakdownMaterial.text = "Material Cost:  ₹${String.format("%,.2f", materialCost)}"
        binding.tvBreakdownDesign.text   = "Design Fee:     ₹${String.format("%,.2f", designFee)}"
        binding.tvBreakdownFinish.text   = if (customFinish)
            "Custom Finish (15%): ₹${String.format("%,.2f", finishCharge)}" else "Custom Finish: —"
        binding.tvTotalEstimate.text     = "₹${String.format("%,.2f", total)}"
    }

    private fun saveQuote() {
        val designFee    = binding.etDesignFee.text.toString().toDoubleOrNull() ?: 0.0
        val customFinish = binding.switchCustomFinish.isChecked
        val finishCharge = if (customFinish) materialCost * 0.15 else 0.0
        val total        = materialCost + designFee + finishCharge

        val quote = Quote(
            id            = DataRepository.nextQuoteId(),
            furnitureName = furnitureName,
            woodType      = woodType,
            length        = length,
            width         = width,
            height        = height,
            thickness     = thickness,
            materialCost  = materialCost,
            designFee     = designFee,
            customFinish  = customFinish,
            totalEstimate = total,
            quoteDate     = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            status        = "Draft"
        )
        DataRepository.addQuote(quote)

        // Brief success animation on button
        binding.btnSaveQuote.text = "✓ Saved!"
        binding.btnSaveQuote.postDelayed({ binding.btnSaveQuote.text = "Save Quote" }, 1500)

        Snackbar.make(binding.root, "Quote saved!", Snackbar.LENGTH_LONG)
            .setAction("View Now") {
                findNavController().navigate(R.id.action_quote_to_saved_quotes)
            }.show()
    }

    private fun shareQuote() {
        val designFee    = binding.etDesignFee.text.toString().toDoubleOrNull() ?: 0.0
        val customFinish = binding.switchCustomFinish.isChecked
        val finishCharge = if (customFinish) materialCost * 0.15 else 0.0
        val total        = materialCost + designFee + finishCharge

        val text = """
            ── Kashta Kala Quote ──
            Furniture : $furnitureName
            Wood Type : $woodType
            Dimensions: ${length}×${width}×${height} ft
            
            Material Cost : ₹${String.format("%,.2f", materialCost)}
            Design Fee    : ₹${String.format("%,.2f", designFee)}
            ${if (customFinish) "Custom Finish : ₹${String.format("%,.2f", finishCharge)}" else ""}
            ─────────────────────
            Total Estimate: ₹${String.format("%,.2f", total)}
            
            Crafted for You. Built to Last.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Kashta Kala Quote — $furnitureName")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share Quote via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
