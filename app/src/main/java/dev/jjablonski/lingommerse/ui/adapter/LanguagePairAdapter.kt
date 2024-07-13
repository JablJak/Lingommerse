package dev.jjablonski.lingommerse.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguagePair

// LanguagePairAdapter.kt
class LanguagePairAdapter(private val languagePairs: List<LanguagePair>) :
    RecyclerView.Adapter<LanguagePairAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originalTextView: TextView = itemView.findViewById(R.id.originalTextView)
        val translationTextView: TextView = itemView.findViewById(R.id.translationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_pair, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = languagePairs[position]
        holder.originalTextView.text = pair.original
        holder.translationTextView.text = pair.translation
    }

    override fun getItemCount() = languagePairs.size
}
