package equipo.closet.closetvirtual.ui.usageHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.databinding.HistoryGarmentItemBinding // Se genera a partir del nuevo XML
import equipo.closet.closetvirtual.entities.Garment

class UsageHistoryAdapter(
    private var garments: List<Garment>
) : RecyclerView.Adapter<UsageHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: HistoryGarmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(garment: Garment) {
            binding.tvGarmentName.text = garment.name
            Glide.with(binding.root.context)
                .load(garment.imageUri)
                .centerCrop()
                .into(binding.ivGarmentImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryGarmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(garments[position])
    }

    override fun getItemCount(): Int = garments.size

    fun updateData(newGarments: List<Garment>) {
        this.garments = newGarments
        notifyDataSetChanged()
    }
}