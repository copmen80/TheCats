package ua.devserhii.thecat.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.example_image_item.view.*
import ua.devserhii.thecat.R
import ua.devserhii.thecat.adapter.CatUiModel

class CatViewHolder(
    itemView: View,
    private val callback: (String, String) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    fun bind(entity: CatUiModel) {
        with(itemView) {
            Glide.with(this)
                .load(entity.url)
                .into(iv_item)

            iv_item.setOnClickListener { callback.invoke(entity.url, entity.id) }
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.example_image_item
    }
}