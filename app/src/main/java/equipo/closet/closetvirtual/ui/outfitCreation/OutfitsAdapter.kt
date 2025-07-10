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
    private val allGarments: Map<String, Garment>
) : ArrayAdapter<Outfit>(context, 0, outfits) {

    // ViewHolder para almacenar las vistas y evitar llamadas repetidas a findViewById
    private class ViewHolder {
        lateinit var tvOutfitName: TextView
        lateinit var imagesContainer: LinearLayout
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            // Si la vista es nueva, la inflamos y creamos el ViewHolder
            view = LayoutInflater.from(context).inflate(R.layout.outfit_list_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tvOutfitName = view.findViewById(R.id.tvOutfitName)
            viewHolder.imagesContainer = view.findViewById(R.id.garment_images_container)
            view.tag = viewHolder
        } else {
            // Si la vista se está reciclando, solo obtenemos el ViewHolder
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val outfit = getItem(position)

        if (outfit != null) {
            viewHolder.tvOutfitName.text = outfit.name
            viewHolder.imagesContainer.removeAllViews() // Limpiar vistas anteriores

            // Cargar las imágenes de las prendas del outfit
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