package dev.jjablonski.lingommerse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguageList

class LanguageListAdapter(
    private val languageLists: List<LanguageList>,
    private val onItemClick: (LanguageList) -> Unit,
    private val onDeleteClick: (LanguageList) -> Unit
) : RecyclerView.Adapter<LanguageListAdapter.LanguageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language_list, parent, false)
        return LanguageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageListViewHolder, position: Int) {
        val list = languageLists[position]
        holder.listNameTextView.text = list.name

        holder.itemView.setOnClickListener {
            onItemClick(list)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(list)
        }
    }

    override fun getItemCount() = languageLists.size

    class LanguageListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNameTextView: TextView = view.findViewById(R.id.listNameTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }
}
