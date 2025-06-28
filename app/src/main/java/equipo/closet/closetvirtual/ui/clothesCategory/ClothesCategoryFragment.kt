
package equipo.closet.closetvirtual.ui.clothesCategory

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategory.adapters.ClothesCategoryGridAdapter
import kotlinx.coroutines.launch

class ClothesCategoryFragment : Fragment() {

    private lateinit var btnBackCategoryCards: MaterialButton
    private lateinit var btnProfileCategoryCards: MaterialButton
    private lateinit var btnSearch: ImageView
    private lateinit var btnFilter: ImageView

    private val clothesRepository: Repository<Garment, Int> = GarmentRepositoryFactory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_clothes_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBackCategoryCards = view.findViewById(R.id.btnBackCategoryCards)
        btnProfileCategoryCards = view.findViewById(R.id.btnProfileCategoryCards)
        btnSearch = view.findViewById(R.id.btnSearchClothesCategory)
        btnFilter = view.findViewById(R.id.btnFilterClothesCategory)

        // Back button behavior
        setBackButtonClickListener()
        // Search button behavior
        setSearchButtonBehavior()
        // Profile button behavior
        setProfileButtonClickListener()
        // Filter button behavior
        setFilterButtonBehavior()

        lifecycleScope.launch {
            val clothes = clothesRepository.getAll().toMutableList()

            // === Top ===
            val topClothes = clothes.filter { it.category.lowercase() == "top" }
            view.findViewById<GridView>(R.id.top_clothes_cards_grid).adapter =
                ClothesCategoryGridAdapter(requireContext(), topClothes.toMutableList())
            view.findViewById<MaterialTextView>(R.id.top_clothes_counter_label).text =
                formatCategoryClothesCount(topClothes.size)

            // === Bottom ===
            val bottomClothes = clothes.filter { it.category.lowercase() == "bottom" }
            view.findViewById<GridView>(R.id.bottom_clothes_cards_grid).adapter =
                ClothesCategoryGridAdapter(requireContext(), bottomClothes.toMutableList())
            view.findViewById<MaterialTextView>(R.id.bottom_clothes_counter_label).text =
                formatCategoryClothesCount(bottomClothes.size)

            // === Zapatos ===
            val shoesClothes = clothes.filter { it.category.lowercase() == "zapatos" }
            view.findViewById<GridView>(R.id.shoes_clothes_cards_grid).adapter =
                ClothesCategoryGridAdapter(requireContext(), shoesClothes.toMutableList())
            view.findViewById<MaterialTextView>(R.id.shoes_clothes_counter_label).text =
                formatCategoryClothesCount(shoesClothes.size)

            // === Bodysuit ===
            val bodysuitClothes = clothes.filter { it.category.lowercase() == "bodysuit" }
            view.findViewById<GridView>(R.id.bodysuit_clothes_cards_grid).adapter =
                ClothesCategoryGridAdapter(requireContext(), bodysuitClothes.toMutableList())
            view.findViewById<MaterialTextView>(R.id.bodysuit_clothes_counter_label).text =
                formatCategoryClothesCount(bodysuitClothes.size)

            // === Accesorios ===
            val accessoriesClothes = clothes.filter { it.category.lowercase() == "accesorios" }
            view.findViewById<GridView>(R.id.accessories_clothes_cards_grid).adapter =
                ClothesCategoryGridAdapter(requireContext(), accessoriesClothes.toMutableList())
            view.findViewById<MaterialTextView>(R.id.accessories_clothes_counter_label).text =
                formatCategoryClothesCount(accessoriesClothes.size)
        }
    }

    private fun setProfileButtonClickListener() {
        btnProfileCategoryCards.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBackButtonClickListener() {
        btnBackCategoryCards.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setSearchButtonBehavior() {
        btnSearch.setOnClickListener {
            TODO("Not yet implemented")
        }
    }

    private fun setFilterButtonBehavior() {
        btnFilter.setOnClickListener {
            val editText = EditText(requireContext()).apply {
                hint = "Escribe la etiqueta"
                setPadding(32, 24, 32, 24)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_dark))
                setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_medium))
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Filtrar por etiqueta")
                .setView(editText)
                .setPositiveButton("Filtrar") { dialog, _ ->
                    val tag = editText.text.toString().trim()
                    if (tag.isNotEmpty()) {
                        TODO("Not yet implemented")
                    } else {
                        Toast.makeText(requireContext(), "Ingrese una etiqueta válida", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun formatCategoryClothesCount(count: Int): String {
        return when {
            count <= 0 -> "0"
            count > 100 -> "100+"
            else -> count.toString()
        }
    }
}
