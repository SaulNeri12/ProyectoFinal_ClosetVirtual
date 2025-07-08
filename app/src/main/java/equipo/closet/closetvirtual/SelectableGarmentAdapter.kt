package equipo.closet.closetvirtual.ui.clothesselection.adapters // O la ruta que prefieras

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.databinding.SelectableGarmentCardBinding // Necesitas crear este layout
import equipo.closet.closetvirtual.entities.Garment

// El lambda 'onGarmentClick' se ejecutará cuando el usuario toque una prenda
class SelectableGarmentAdapter(
    private var garments: List<Garment>,
    private val onGarmentClick: (Garment) -> Unit
) : RecyclerView.Adapter<SelectableGarmentAdapter.GarmentViewHolder>() {

    // El ViewHolder contiene las vistas de cada item de la lista
    inner class GarmentViewHolder(val binding: SelectableGarmentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(garment: Garment) {
            binding.tvGarmentName.text = garment.name
            Glide.with(binding.root.context)
                .load(garment.imageUri)
                .centerCrop()
                .into(binding.ivGarmentImage)

            // Configura el click listener para todo el item
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

    // Función para actualizar la lista de prendas en el adaptador
    fun updateData(newGarments: List<Garment>) {
        garments = newGarments
        notifyDataSetChanged()
    }
}