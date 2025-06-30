package equipo.closet.closetvirtual.ui.searchOutfit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Outfit

class OutfitSearchListAdapter : BaseAdapter {

    private lateinit var context: Context
    private lateinit var outfits: MutableList<Outfit>

    constructor(context: Context, outfits: MutableList<Outfit>) {
        this.context = context
        this.outfits = outfits
    }

    override fun getCount(): Int {
        return this.outfits.size
    }

    override fun getItem(position: Int): Outfit {
        return this.outfits.get(position)
    }

    override fun getItemId(position: Int): Long {
        return this.outfits.get(position).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.outfit_card, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.outfit_name_label),
                view.findViewById(R.id.outfit_tag_label),
                view.findViewById(R.id.clothes_cards_list_grid)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val outfit: Outfit = this.getItem(position)

        holder.outfitName.text = outfit.name
        holder.outfitTag.text = "Tag Test"

        // setting up clothes list adapters
        holder.clothesCardsGrid.adapter = OutfitClothesGridAdapter(view.context, outfit.getClothes().toMutableList())

        return view
    }

    private data class ViewHolder(
        val outfitName: TextView,
        val outfitTag: TextView,
        val clothesCardsGrid: GridView
    )
}