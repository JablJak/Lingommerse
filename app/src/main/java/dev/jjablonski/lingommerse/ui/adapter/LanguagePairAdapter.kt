package dev.jjablonski.lingommerse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguagePair

class LanguagePairAdapter(
    private val languagePairs: List<LanguagePair>,
    private val onItemDelete: (LanguagePair) -> Unit
) : RecyclerView.Adapter<LanguagePairAdapter.LanguagePairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagePairViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language_pair, parent, false)
        return LanguagePairViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguagePairViewHolder, position: Int) {
        val pair = languagePairs[position]
        holder.originalTextView.text = pair.original
        holder.translationTextView.text = pair.translation

        holder.itemView.setOnLongClickListener {
            onItemDelete(pair)
            true
        }
    }

    override fun getItemCount() = languagePairs.size

    class LanguagePairViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val originalTextView: TextView = view.findViewById(R.id.originalTextView)
        val translationTextView: TextView = view.findViewById(R.id.translationTextView)
    }
}
