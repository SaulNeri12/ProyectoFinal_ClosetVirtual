package equipo.closet.closetvirtual.ui.searchOutfit

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.OutfitCreationActivity
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.global.ClothesCache
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.ui.outfitCreation.OutfitsAdapter
import equipo.closet.closetvirtual.ui.searchOutfit.adapters.OutfitSearchListAdapter
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterFragment // Asumiendo el nombre del filtro
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchOutfitFragment : Fragment() {

    private lateinit var binding: FragmentSearchOutfitBinding

    private val viewModel: SearchOutfitFilterViewModel by activityViewModels()

    private val clothesRepository = GarmentRepositoryFactory.create()
    private val outfitRepository = FirebaseOutfitRepository(clothesRepository)

    private var tags: List<String> = emptyList()

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentSearchOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAllClothes()

        setupListeners()
        setupObservers()
        loadInitialData()

    }

    private fun loadAllClothes() {
        lifecycleScope.launch {
            val clothes = clothesRepository.getAll()
            Log.w("#### SearchOutfitFragment", "Clothes CACHED: $clothes")
            ClothesCache.setGarments(clothes)
        }
    }


    override fun onResume() {
        super.onResume()
        triggerSearch()
    }


    private fun setupListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        binding.btnNewOutfit.setOnClickListener {
            startActivity(Intent(requireContext(), OutfitCreationActivity::class.java))
        }

        binding.btnFilter.setOnClickListener {
            SearchOutfitFilterFragment().show(childFragmentManager, "FilterFragment")
        }

        binding.filteredGarmentSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                triggerSearch()
            }
        })
    }

    private fun setupObservers() {
        viewModel.tags.observe(viewLifecycleOwner) { newTags ->
            this.tags = newTags
            triggerSearch()
        }
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                val outfits = outfitRepository.getAll()
                updateOutfitList(outfits)
                Toast.makeText(requireContext(), "Loaded initial data", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar los outfits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            try {
                delay(500L)
                val searchText = binding.filteredGarmentSearchInput.text.toString().lowercase()

                val filterMap = mutableMapOf<String, Any>()
                if (searchText.isNotEmpty()) {
                    filterMap["name"] = searchText
                }
                if (tags.isNotEmpty()) {
                    filterMap["tags"] = tags
                }

                val outfits = outfitRepository.getAll(filterMap)

                Log.w("#### SearchOutfitFragment", "Outfits: $outfits")
                Log.w("#### SearchOutfitFragment", "search text: $searchText")

                updateOutfitList(outfits)

            } catch (e: CancellationException) {
                // no hagas nada...
            }
            catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar los outfits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOutfitList(outfits: List<Outfit>) {
        val adapter = OutfitSearchListAdapter(requireContext(), outfits.toMutableList())
        binding.outfitCardsListview.adapter = adapter
    }


}