package com.kashta.kala.ui.quote

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Quote
import com.kashta.kala.databinding.FragmentSavedQuotesBinding
import com.kashta.kala.databinding.ItemQuoteBinding

class SavedQuotesFragment : Fragment() {

    private var _binding: FragmentSavedQuotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: QuoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedQuotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeToDelete()
        loadQuotes()

        binding.btnGoEstimator.setOnClickListener {
            findNavController().navigate(R.id.navigation_estimator)
        }
    }

    private fun setupRecyclerView() {
        adapter = QuoteAdapter(
            onDelete = { quote ->
                val removed = quote
                DataRepository.deleteQuote(quote.id)
                loadQuotes()
                Snackbar.make(binding.root, "Quote deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        DataRepository.addQuote(removed)
                        loadQuotes()
                    }.show()
            },
            onShare = { quote -> shareQuote(quote) }
        )
        binding.rvSavedQuotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedQuotes.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos   = viewHolder.adapterPosition
                val quote = DataRepository.quotes[pos]
                DataRepository.deleteQuote(quote.id)
                loadQuotes()
                Snackbar.make(binding.root, "Quote deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        DataRepository.addQuote(quote)
                        loadQuotes()
                    }.show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvSavedQuotes)
    }

    private fun loadQuotes() {
        val quotes = DataRepository.quotes.toList()
        if (quotes.isEmpty()) {
            binding.emptyState.visibility    = View.VISIBLE
            binding.rvSavedQuotes.visibility = View.GONE
        } else {
            binding.emptyState.visibility    = View.GONE
            binding.rvSavedQuotes.visibility = View.VISIBLE
            adapter.updateData(quotes)
        }
    }

    private fun shareQuote(quote: Quote) {
        val text = """
            ── Kashta Kala Quote ──
            Furniture : ${quote.furnitureName}
            Wood Type : ${quote.woodType}
            Dimensions: ${quote.length}×${quote.width}×${quote.height} ft
            
            Material Cost : ₹${String.format("%,.2f", quote.materialCost)}
            Design Fee    : ₹${String.format("%,.2f", quote.designFee)}
            Total Estimate: ₹${String.format("%,.2f", quote.totalEstimate)}
            Date: ${quote.quoteDate}
            
            Crafted for You. Built to Last.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Kashta Kala Quote — ${quote.furnitureName}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share Quote via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Adapter ──────────────────────────────────────────────────────────────────
class QuoteAdapter(
    private val onDelete: (Quote) -> Unit,
    private val onShare: (Quote) -> Unit
) : RecyclerView.Adapter<QuoteAdapter.VH>() {

    private var quotes: List<Quote> = emptyList()
    private val expandedIds = mutableSetOf<Int>()

    inner class VH(val b: ItemQuoteBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemQuoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val q = quotes[position]
        holder.b.tvFurnitureName.text  = q.furnitureName
        holder.b.tvWoodDimensions.text = "${q.woodType} · ${q.length}×${q.width}×${q.height} ft"
        holder.b.tvTotalEstimate.text  = "₹${String.format("%,.2f", q.totalEstimate)}"
        holder.b.tvQuoteDate.text      = q.quoteDate

        // Status chip colour
        val (bgColor, textColor) = when (q.status) {
            "Submitted" -> Pair(0xFFE3F2FD.toInt(), 0xFF1565C0.toInt())
            "Accepted"  -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
            else        -> Pair(0xFFFFF3E0.toInt(), 0xFFE65100.toInt())
        }
        holder.b.chipStatus.setBackgroundColor(bgColor)
        holder.b.chipStatus.setTextColor(textColor)
        holder.b.chipStatus.text = q.status

        // Expand/collapse breakdown
        val isExpanded = expandedIds.contains(q.id)
        holder.b.breakdownLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (isExpanded) {
            holder.b.tvBreakdownMaterial.text = "Material: ₹${String.format("%,.2f", q.materialCost)}"
            holder.b.tvBreakdownDesign.text   = "Design Fee: ₹${String.format("%,.2f", q.designFee)}"
            holder.b.tvBreakdownFinish.text   = if (q.customFinish) "Custom Finish: ✓" else "Custom Finish: —"
        }

        holder.b.root.setOnClickListener {
            if (isExpanded) expandedIds.remove(q.id) else expandedIds.add(q.id)
            notifyItemChanged(position)
        }

        holder.b.btnDelete.setOnClickListener { onDelete(q) }
        holder.b.btnShare.setOnClickListener  { onShare(q) }
    }

    override fun getItemCount() = quotes.size

    fun updateData(newList: List<Quote>) {
        quotes = newList
        notifyDataSetChanged()
    }
}
