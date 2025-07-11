package equipo.closet.closetvirtual.ui.clothesSelection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.databinding.ClothesSelectionCardBinding
import equipo.closet.closetvirtual.entities.Garment

class ClothesSelectionAdapter(
    private val context: Context,
    private var garments: List<Garment>,
    private val onGarmentClick: (Garment) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = garments.size

    override fun getItem(position: Int): Any = garments[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ClothesSelectionCardBinding

        val view: View = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            val newBinding = ClothesSelectionCardBinding.inflate(inflater, parent, false)
            binding = newBinding
            binding.root.tag = binding // Guarda el binding como tag
            binding.root
        } else {
            binding = convertView.tag as ClothesSelectionCardBinding
            convertView
        }

        val garment = garments[position]

        binding.tvGarmentName.text = garment.name
        Glide.with(context)
            .load(garment.imageUri)
            .centerCrop()
            .into(binding.ivGarmentImage)

        binding.root.setOnClickListener {
            onGarmentClick(garment)
        }

        return view
    }

    fun updateData(newGarments: List<Garment>) {
        this.garments = newGarments
        notifyDataSetChanged()
    }
}


