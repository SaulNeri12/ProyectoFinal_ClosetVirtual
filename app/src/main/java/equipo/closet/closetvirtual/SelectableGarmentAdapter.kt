package equipo.closet.closetvirtual.ui.clothesselection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.databinding.SelectableGarmentCardBinding
import equipo.closet.closetvirtual.entities.Garment

class SelectableGarmentAdapter(
    private var garments: List<Garment>,
    private val onGarmentClick: (Garment) -> Unit
) : RecyclerView.Adapter<SelectableGarmentAdapter.GarmentViewHolder>() {

    inner class GarmentViewHolder(val binding: SelectableGarmentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(garment: Garment) {
            binding.tvGarmentName.text = garment.name
            Glide.with(binding.root.context)
                .load(garment.imageUri)
                .centerCrop()
                .into(binding.ivGarmentImage)

            itemView.setOnClickListener {
                onGarmentClick(garment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GarmentViewHolder {
        val binding = SelectableGarmentCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GarmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GarmentViewHolder, position: Int) {
        holder.bind(garments[position])
    }

    override fun getItemCount(): Int = garments.size

    fun updateData(newGarments: List<Garment>) {
        garments = newGarments
        notifyDataSetChanged()
    }
}