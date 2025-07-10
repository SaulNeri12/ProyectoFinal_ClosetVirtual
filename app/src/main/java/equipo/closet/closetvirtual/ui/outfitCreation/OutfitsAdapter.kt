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
    private val outfits: List<Outfit>,
    private val allGarments: Map<String, Garment> // Un mapa para buscar prendas por ID rápidamente
) : ArrayAdapter<Outfit>(context, 0, outfits) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.outfit_list_item, parent, false)

        val outfit = outfits[position]

        val tvOutfitName = view.findViewById<TextView>(R.id.tvOutfitName)
        val imagesContainer = view.findViewById<LinearLayout>(R.id.garment_images_container)

        tvOutfitName.text = outfit.name
        imagesContainer.removeAllViews()

        // Cargar las imágenes de las prendas del outfit
        outfit.clothesIds.forEach { garmentId ->
            val garment = allGarments[garmentId]
            if (garment != null) {
                val imageView = ShapeableImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(120, 120) // Tamaño de la imagen
                    setPadding(0, 0, 8, 0)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    // Aquí puedes definir la forma de la imagen si quieres
                }
                Glide.with(context).load(garment.imageUri).into(imageView)
                imagesContainer.addView(imageView)
            }
        }
        return view
    }
}