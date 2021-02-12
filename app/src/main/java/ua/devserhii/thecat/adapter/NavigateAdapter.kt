package ua.devserhii.thecat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.example_button_item.view.*
import ua.devserhii.thecat.R
import ua.devserhii.thecat.model.Category

class NavigateAdapter(private val callback: (Int) -> Unit) :
    RecyclerView.Adapter<NavigateAdapter.NavigateViewHolder>() {
    private var values: List<Category> = emptyList()

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigateViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.example_button_item, parent, false)

        return NavigateViewHolder(itemView, callback)
    }

    override fun onBindViewHolder(holder: NavigateViewHolder, position: Int) {
        holder.bind(values[position])
    }

    fun update(newValues: List<Category>) {
        values = newValues
        notifyDataSetChanged()
    }

    class NavigateViewHolder(
        itemView: View,
        private val callback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Category) {
            with(itemView) {
                b_item.text = item.name

                b_item.setOnClickListener { callback.invoke(item.id) }
            }
        }
    }
}