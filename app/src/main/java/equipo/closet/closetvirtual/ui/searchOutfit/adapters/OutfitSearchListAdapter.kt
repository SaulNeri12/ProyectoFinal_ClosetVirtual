package equipo.closet.closetvirtual.ui.searchOutfit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.global.ClothesCache
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import android.util.Log
import android.widget.AbsListView

class OutfitSearchListAdapter : BaseAdapter {

    private lateinit var context: Context
    private lateinit var outfits: MutableList<Outfit>
    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

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
        holder.outfitTag.text = getTagsString(outfit.tags)

        // setting up clothes list adapters

        /*
        // lifecycleSciope.launch porque getById es asincrono
        var clothes = mutableListOf<Garment>()

        for (garmentId in outfit.clothesIds) {
            val garment = clothesRepository.getById(garmentId)
            if (garment != null) {
                clothes.add(garment)
            }
        }*/

        Log.w("#### OutfitSearchListAdapter", "Clothes CACHE: ${ClothesCache.getAllGarments()}")

        var clothes = mutableListOf<Garment>()

        for (garmentId in outfit.clothesIds) {
            ClothesCache.getGarmentById(garmentId)?.let { clothes.add(it) }
        }

        Log.w("#### OutfitSearchListAdapter", "Clothes FETCHED FROM CACHE: $clothes")

        holder.clothesCardsGrid.adapter = OutfitClothesGridAdapter(view.context, clothes)

        // Aplica margen inferior para separar las tarjetas
        val layoutParams = view.layoutParams ?: AbsListView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.bottomMargin = 20.dpToPx(context)
            view.layoutParams = layoutParams
        }

        return view
    }

    private data class ViewHolder(
        val outfitName: TextView,
        val outfitTag: TextView,
        val clothesCardsGrid: GridView
    )

    private fun getTagsString(tags : MutableList<String>) : String {
        val tagsString = StringBuilder()
        for (tag in tags) {
            tagsString.append(tag)
            tagsString.append(", ")
        }
        return tagsString.toString().dropLast(2)
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}