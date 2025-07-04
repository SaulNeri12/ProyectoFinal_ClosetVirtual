package equipo.closet.closetvirtual.ui.clothesCategory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategory.adapters.ClothesCategoryGridAdapter
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClothesCategoryFragment : Fragment() {

    private lateinit var binding: FragmentClothesCategoryBinding
    private val viewModel: ClothesCategoryViewModel by activityViewModels()
    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

    private var allGarments: List<Garment> = listOf()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClothesCategoryBinding.inflate(inflater, container, false)
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAllGarments()

        setBackButtonClickListener()
        setSearchByTagButtonBehavior()
        setProfileButtonClickListener()
        setFilterButtonBehavior()
        setRealTimeSearchByName()
    }

    private fun fetchAllGarments() {
        lifecycleScope.launch {
            try {
                allGarments = clothesRepository.getAll()
                updateGarmentViews(allGarments)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setRealTimeSearchByName() {

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500L)
                    val searchText = s.toString().trim()
                    filterGarmentsByName(searchText)
                }
            }
        })
    }

    private fun filterGarmentsByName(searchText: String) {
        val filteredList = if (searchText.isEmpty()) {
            allGarments
        } else {
            allGarments.filter { garment ->
                garment.name.contains(searchText, ignoreCase = true)
            }
        }
        activity?.runOnUiThread {
            updateGarmentViews(filteredList)
        }
    }

    private fun updateGarmentViews(garments: List<Garment>) {
        val categoryMap = garments.groupBy { it.category.lowercase() }

        val topClothes = categoryMap["top"] ?: emptyList()
        binding.topClothesCardsGrid.adapter = ClothesCategoryGridAdapter(requireContext(), topClothes.toMutableList())
        binding.topClothesCounterLabel.text = formatCategoryClothesCount(topClothes.size)

        val bottomClothes = categoryMap["bottom"] ?: emptyList()
        binding.bottomClothesCardsGrid.adapter = ClothesCategoryGridAdapter(requireContext(), bottomClothes.toMutableList())
        binding.bottomClothesCounterLabel.text = formatCategoryClothesCount(bottomClothes.size)

        val shoesClothes = categoryMap["zapatos"] ?: emptyList()
        binding.shoesClothesCardsGrid.adapter = ClothesCategoryGridAdapter(requireContext(), shoesClothes.toMutableList())
        binding.shoesClothesCounterLabel.text = formatCategoryClothesCount(shoesClothes.size)

        val bodysuitClothes = categoryMap["bodysuit"] ?: emptyList()
        binding.bodysuitClothesCardsGrid.adapter = ClothesCategoryGridAdapter(requireContext(), bodysuitClothes.toMutableList())
        binding.bodysuitClothesCounterLabel.text = formatCategoryClothesCount(bodysuitClothes.size)

        val accessoriesClothes = categoryMap["accesorios"] ?: emptyList()
        binding.accessoriesClothesCardsGrid.adapter = ClothesCategoryGridAdapter(requireContext(), accessoriesClothes.toMutableList())
        binding.accessoriesClothesCounterLabel.text = formatCategoryClothesCount(accessoriesClothes.size)
    }

    private fun setSearchByTagButtonBehavior() {
        binding.btnSearchClothesCategory.setOnClickListener {
            val tagToSearch = viewModel.tag.value?.trim()
            val filters = if (!tagToSearch.isNullOrEmpty()) {
                mapOf("tag" to tagToSearch)
            } else {
                emptyMap()
            }
            fetchAndDisplayGarmentsByTag(filters)
        }
    }

    private fun fetchAndDisplayGarmentsByTag(filters: Map<String, Any>) {
        lifecycleScope.launch {
            try {
                val clothes = clothesRepository.getAll(filters)
                if (clothes.isEmpty() && filters.containsKey("tag")) {
                    Toast.makeText(requireContext(), "No se encontraron prendas con la etiqueta '${filters["tag"]}'", Toast.LENGTH_SHORT).show()
                }
                updateGarmentViews(clothes)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
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