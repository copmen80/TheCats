package ua.devserhii.thecat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.devserhii.thecat.adapter.ui.CatUiModel
import ua.devserhii.thecat.adapter.ui.UiModel
import ua.devserhii.thecat.adapter.viewholder.CatViewHolder


/**
 * Created by Serhii Boiko on 04.12.2020.
 */
class CatAdapter(private val callback: (String, String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var values = mutableListOf<UiModel>()

    override fun getItemCount() = values.size

    override fun getItemViewType(position: Int): Int {
        return CatViewHolder.LAYOUT_ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(viewType, parent, false)

        return when (viewType) {
            CatViewHolder.LAYOUT_ID -> CatViewHolder(itemView, callback)
            else -> throw IllegalArgumentException("Unsupported ViewType")
        }
    }

    fun update(newValues: List<UiModel>) {
        values = newValues.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val entity = values[position]) {
            is CatUiModel -> (holder as CatViewHolder).bind(entity)
        }
    }

    private fun add(mc: UiModel) {
        values.add(mc)
        notifyItemInserted(values.size - 1)
    }

    fun addAll(mcList: List<UiModel>) {
        for (mc in mcList) {
            add(mc)
        }
        notifyDataSetChanged()
    }
}