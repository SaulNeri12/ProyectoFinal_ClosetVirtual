package equipo.closet.closetvirtual.ui.clothesCategory.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.ClothingInformationActivity

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.R

class ClothesCategoryGridAdapter(
    private val context: Context,
    private val clothes: MutableList<Garment>
) : BaseAdapter() {

    override fun getCount(): Int = clothes.size

    override fun getItem(position: Int): Garment = clothes[position]

    override fun getItemId(position: Int): Long = position.toLong()

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
                .error(R.mipmap.garment_bottom_test)
                .into(holder.previewImage)
        }

        // Apply slide-in-bottom animation to the card
        //equipo.closet.closetvirtual.utils.AnimationHelper.applySlideInBottomAnimation(view, context)

        holder.previewImage.setOnClickListener {
            var intent: Intent = Intent(context, ClothingInformationActivity::class.java)

            intent.putExtra("garment_id", garment.id)
            intent.putExtra("garment_name", garment.name)
            intent.putExtra("garment_color", garment.color)
            intent.putExtra("garment_tag", garment.tag)
            intent.putExtra("garment_category", garment.category)
            intent.putExtra("garment_print", garment.print)
            intent.putExtra("garment_image_uri", garment.imageUri)

            context.startActivity(intent)
        }

        return view
    }

    private data class ViewHolder(val previewImage: ImageView)
}
