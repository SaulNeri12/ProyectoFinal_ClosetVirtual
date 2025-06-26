package equipo.closet.closetvirtual.ui.clothes_category_cards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import equipo.closet.closetvirtual.LoginActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothes_category_cards.adapters.ClothesCategoryGridAdapter
import kotlinx.coroutines.launch

class ClothesCategoryCardsFragment : Fragment() {

    private val clothesRepository: Repository<Garment, Int> = GarmentRepositoryFactory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_clothes_category_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            // loads all clothes from repository (asynchronus)
            val clothes = clothesRepository.getAll().toMutableList()

            // ======[ Category "Top" ]========
            val topClothes = clothes.filter { it.category.lowercase() == "top" }
            val topClothesCategoryAdapter = ClothesCategoryGridAdapter(requireContext(), clothes) // makes the adapter
            val clothesTopCategoryGridView: GridView = view.findViewById(R.id.top_clothes_cards_grid) // loads the preview clothes cards form category card
            clothesTopCategoryGridView.adapter = topClothesCategoryAdapter // sets the preview cards adapter
            val topClothesCountLabel: MaterialTextView = view.findViewById(R.id.top_clothes_counter_label) // gets the label which contains the count of clothes
            topClothesCountLabel.text = formatCategoryClothesCount(topClothes.size) // sets the clothes count

            ///////////////////////////////////////////////////////////////////////

            // ======[ Category "Bottom" ]========
            val bottomClothes = clothes.filter { it.category.lowercase() == "bottom" }
            val bottomAdapter = ClothesCategoryGridAdapter(requireContext(), bottomClothes.toMutableList())
            view.findViewById<GridView>(R.id.bottom_clothes_cards_grid).adapter = bottomAdapter
            view.findViewById<MaterialTextView>(R.id.bottom_clothes_counter_label).text =
                formatCategoryClothesCount(bottomClothes.size)

            ///////////////////////////////////////////////////////////////////////

            // ======[ Category "Shoes" ]========
            val shoesClothes = clothes.filter { it.category.lowercase() == "zapatos" }
            val shoesAdapter = ClothesCategoryGridAdapter(requireContext(), shoesClothes.toMutableList())
            view.findViewById<GridView>(R.id.shoes_clothes_cards_grid).adapter = shoesAdapter
            view.findViewById<MaterialTextView>(R.id.shoes_clothes_counter_label).text =
                formatCategoryClothesCount(shoesClothes.size)

            ////////////////////////////////////////////////////////////////////////

            // ======[ Category "Bodysuit" ]========
            val bodysuitClothes = clothes.filter { it.category.lowercase() == "bodysuit" }
            val bodysuitAdapter = ClothesCategoryGridAdapter(requireContext(), bodysuitClothes.toMutableList())
            view.findViewById<GridView>(R.id.bodysuit_clothes_cards_grid).adapter = bodysuitAdapter
            view.findViewById<MaterialTextView>(R.id.bodysuit_clothes_counter_label).text =
                formatCategoryClothesCount(bodysuitClothes.size)

            ////////////////////////////////////////////////////////////////////////

            // ======[ Category "Accessories" ]========
            val accessoriesClothes = clothes.filter { it.category.lowercase() == "accesorios" }
            val accessoriesAdapter = ClothesCategoryGridAdapter(requireContext(), accessoriesClothes.toMutableList())
            view.findViewById<GridView>(R.id.accessories_clothes_cards_grid).adapter = accessoriesAdapter
            view.findViewById<MaterialTextView>(R.id.accessories_clothes_counter_label).text =
                formatCategoryClothesCount(accessoriesClothes.size)
        }
    }

    /**
     * Formats the clothes count depending the number
     */
    private fun formatCategoryClothesCount(count: Int): String {

        if (count <= 0) {
            return "0"
        }

        if (count > 100) {
            return "100+"
        }

        return count.toString()
    }
}
