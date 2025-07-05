package equipo.closet.closetvirtual.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import equipo.closet.closetvirtual.R

data class ColorItem(
    val name: String,
    val colorResId: Int
)

class ColorSpinnerAdapter(
    private val context: Context,
    private val colors: List<ColorItem>
) : BaseAdapter() {

    override fun getCount(): Int = colors.size

    override fun getItem(position: Int): Any = colors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent, true)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup?, isDropDown: Boolean): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            if (isDropDown) R.layout.spinner_color_item_dropdown else R.layout.spinner_color_item,
            parent,
            false
        )

        val colorSample = view.findViewById<MaterialCardView>(R.id.colorSample)
        val colorName = view.findViewById<TextView>(R.id.colorName)

        val colorItem = colors[position]

        // Establecer el color de la muestra
        colorSample.setCardBackgroundColor(ContextCompat.getColor(context, colorItem.colorResId))

        // Establecer el nombre del color
        colorName.text = colorItem.name

        return view
    }
}