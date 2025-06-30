package equipo.closet.closetvirtual.ui.searchOutfit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment

class OutfitClothesGridAdapter (
    private val context: Context,
    private val clothes: MutableList<Garment>
    ) : BaseAdapter() {

    override fun getCount(): Int = clothes.size

    override fun getItem(position: Int): Garment = clothes[position]

    override fun getItemId(position: Int): Long = clothes[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.garment_preview_card, parent, false)
            holder = ViewHolder(view.findViewById(R.id.preview_garment_image))
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val garment = getItem(position)

        if (garment.imageUri.isEmpty() || garment.imageUri.isBlank()) {
            holder.previewImage.setImageResource(R.mipmap.garment_bottom_test)
        } else {
            Glide.with(context)
                .load(garment.imageUri)
                .into(holder.previewImage)
        }

        return view
    }

    private data class ViewHolder(val previewImage: ImageView)
}