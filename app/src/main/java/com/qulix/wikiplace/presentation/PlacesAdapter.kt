package com.qulix.wikiplace.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qulix.wikiplace.R
import com.qulix.wikiplace.domain.entity.WikiPlace
import kotlin.properties.Delegates

class PlacesAdapter(initialItems: List<WikiPlace>) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    // TODO: use DiffUtil.
    var items: List<WikiPlace> by Delegates.observable(initialItems) { _, _, _ -> notifyDataSetChanged() }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_place, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val resources = itemView.context.resources

        private val titleTextView: TextView = itemView.findViewById(R.id.textview_placeitem_title)
        private val subtitleTextView: TextView = itemView.findViewById(R.id.textview_placeitem_subtitle)

        fun bind(item: WikiPlace) {
            titleTextView.text = item.title
            subtitleTextView.text = resources.getQuantityString(
                R.plurals.places_numberOfImages,
                item.numberOfImages,
                item.numberOfImages
            )
        }
    }
}