package equipo.closet.closetvirtual.ui.clothesCategory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategory.adapters.ClothesCategoryGridAdapter
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClothesCategoryFragment : Fragment() {

    //this is where we save the tag gotten from the filter fragment
    private var tags: MutableList<String> = emptyList<String>().toMutableList()

    private lateinit var binding: FragmentClothesCategoryBinding
    private lateinit var viewModel : ClothesCategoryFilterViewModel

    //repository instance for persisting data
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
        viewModel = ViewModelProvider(requireActivity())[ClothesCategoryFilterViewModel::class.java]

        fetchAllGarments()
        showFiltersFragment()
        setRealTimeSearchByName()
        setBackButtonClickListener()
        setProfileButtonClickListener()
        setTagsObserver()
        setSearchEventObserver()
        searchGarmentEvent()
    }

    /**
     * in this method we fetch all the garments from the database when the fragment
     * gets the focus back after being created
     */
    override fun onResume() {
        super.onResume()
        fetchAllGarments()
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
                    try {
                        delay(500L)

                        val searchText = s.toString().lowercase()

                        val filterMap = mutableMapOf<String, Any>()

                        if (searchText.isNotEmpty()) {
                            filterMap["name"] = searchText
                        }
                        if (!tags.isEmpty()) {
                            filterMap["tags"] = tags
                        }

                        val garments = clothesRepository.getAll(filterMap)
                        updateGarmentViews(garments)

                    } catch (e: Exception) {
                        Log.e("Search", "Error: ${e.message}", e)
                    }
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

    private fun showFiltersFragment() {
        binding.btnFilterClothesCategory.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesCategoryFilterFragment")
        }
    }

    private fun setTagsObserver() : Unit {
        viewModel.tags.observe(viewLifecycleOwner) {
            tags = viewModel.tags.value as MutableList<String>
        }
    }

    private fun setSearchEventObserver() : Unit {
        viewModel.searchEvent.observe(viewLifecycleOwner) {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                try {
                    delay(500L)

                    val searchText = binding.etSearch.toString().lowercase()

                    val filterMap = mutableMapOf<String, Any>()

                    if (searchText.isNotEmpty()) {
                        filterMap["name"] = searchText
                    }
                    if (!tags.isEmpty()) {
                        filterMap["tags"] = tags
                    }

                    val garments = clothesRepository.getAll(filterMap)
                    updateGarmentViews(garments)

                } catch (e: Exception) {
                    Log.e("Search", "Error: ${e.message}", e)
                }
            }
        }
    }

    private fun searchGarmentEvent() : Unit{
        lifecycleScope.launch {
            val searchText = binding.etSearch.toString().trim()
            filterGarmentsByName(searchText) //Change this to the correct function
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