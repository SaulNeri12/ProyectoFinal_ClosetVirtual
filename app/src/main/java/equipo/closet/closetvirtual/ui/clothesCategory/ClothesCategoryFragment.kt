
package equipo.closet.closetvirtual.ui.clothesCategory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryBinding
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategory.adapters.ClothesCategoryGridAdapter
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryViewModel
import kotlinx.coroutines.launch

class ClothesCategoryFragment : Fragment() {

    private lateinit var binding: FragmentClothesCategoryBinding
    private lateinit var viewModel : ClothesCategoryViewModel

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tag: String

    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ClothesCategoryViewModel::class.java]

        binding = FragmentClothesCategoryBinding.inflate(inflater, container, false)
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModelObserver()
        setBackButtonClickListener()
        setSearchButtonBehavior()
        setProfileButtonClickListener()
        setFilterButtonBehavior()
        setViewModelObserver()

        lifecycleScope.launch {
            val clothes = clothesRepository.getAll().toMutableList()

            // === Top ===
            val topClothes = clothes.filter { it.category.lowercase() == "top" }
            binding.topClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), topClothes.toMutableList())
            binding.topClothesCounterLabel.text = formatCategoryClothesCount(topClothes.size)

            // === Bottom ===
            val bottomClothes = clothes.filter { it.category.lowercase() == "bottom" }
            binding.bottomClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), bottomClothes.toMutableList())
            binding.bottomClothesCounterLabel.text = formatCategoryClothesCount(bottomClothes.size)

            // === Zapatos ===
            val shoesClothes = clothes.filter { it.category.lowercase() == "zapatos" }
            binding.shoesClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), shoesClothes.toMutableList())
            binding.shoesClothesCounterLabel.text = formatCategoryClothesCount(shoesClothes.size)

            // === Bodysuit ===
            val bodysuitClothes = clothes.filter { it.category.lowercase() == "bodysuit" }
            binding.bodysuitClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), bodysuitClothes.toMutableList())
            binding.bodysuitClothesCounterLabel.text = formatCategoryClothesCount(bodysuitClothes.size)

            // === Accesorios ===
            val accessoriesClothes = clothes.filter { it.category.lowercase() == "accesorios" }
            binding.accessoriesClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), accessoriesClothes.toMutableList())
            binding.accessoriesClothesCounterLabel.text = formatCategoryClothesCount(accessoriesClothes.size)
        }
    }

    private fun setViewModelObserver(){
        viewModel.tag.observe(viewLifecycleOwner) {
            this.tag = viewModel.tag.value.toString()
        }
    }

    private fun setProfileButtonClickListener() {
        binding.btnProfileCategoryCards.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBackButtonClickListener() {
        binding.btnBackCategoryCards.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setSearchButtonBehavior() {
        binding.btnSearchClothesCategory.setOnClickListener {
            Toast.makeText(requireContext(), "Search button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFilterButtonBehavior() {
        binding.btnFilterClothesCategory.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesCategoryFilterFragment")
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