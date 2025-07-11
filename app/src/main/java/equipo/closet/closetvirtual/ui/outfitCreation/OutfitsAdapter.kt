package equipo.closet.closetvirtual.ui.outfitCreation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit

class OutfitsAdapter(
    context: Context,
    // El mapa de prendas es necesario para buscar las imágenes
    outfits: List<Outfit>,
    private var allGarments: Map<String, Garment>
) : ArrayAdapter<Outfit>(context, 0) { // El constructor ya no necesita la lista inicial

    // ViewHolder para un rendimiento eficiente
    private class ViewHolder {
        lateinit var tvOutfitName: TextView
        lateinit var imagesContainer: LinearLayout
    }

    /**
     * Esta función ahora es la forma correcta de actualizar los datos del adapter.
     */
    fun updateData(newOutfits: List<Outfit>, newGarmentsMap: Map<String, Garment>) {
        this.allGarments = newGarmentsMap
        // Se limpia la lista interna del adapter
        clear()
        // Se añaden los nuevos outfits a la lista interna del adapter
        addAll(newOutfits)
        // Se notifica a la vista que los datos han cambiado
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.outfit_list_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tvOutfitName = view.findViewById(R.id.tvOutfitName)
            viewHolder.imagesContainer = view.findViewById(R.id.garment_images_container)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        // getItem(position) usa la lista interna del ArrayAdapter
        val outfit = getItem(position)

        if (outfit != null) {
            viewHolder.tvOutfitName.text = outfit.name
            viewHolder.imagesContainer.removeAllViews() // Limpiar imágenes anteriores

            outfit.clothesIds.forEach { garmentId ->
                val garment = allGarments[garmentId]
                if (garment != null) {
                    val imageView = ShapeableImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(120, 120)
                        setPadding(0, 0, 8, 0)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    Glide.with(context).load(garment.imageUri).into(imageView)
                    viewHolder.imagesContainer.addView(imageView)
                }
            }
        }
        return view
    }
}