package equipo.closet.closetvirtual.ui.clothes_category_cards.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.ImageView

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.R

class ClothesCategoryGridAdapter(
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

        // Aquí asigna la imagen que corresponda a la prenda
        // Si tienes un campo de imagen en Garment usa eso
        // Por ahora usas un recurso fijo
        holder.previewImage.setImageResource(R.mipmap.garment_bottom_test)

        return view
    }

    private data class ViewHolder(val previewImage: ImageView)
}
