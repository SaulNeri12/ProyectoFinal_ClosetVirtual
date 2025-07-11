package equipo.closet.closetvirtual.ui.searchOutfit

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
import equipo.closet.closetvirtual.OutfitCreationActivity
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.ui.outfitCreation.OutfitsAdapter
import equipo.closet.closetvirtual.ui.searchOutfit.adapters.OutfitSearchListAdapter
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterFragment // Asumiendo el nombre del filtro
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchOutfitFragment : Fragment() {

    private var _binding: FragmentSearchOutfitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchOutfitFilterViewModel by activityViewModels()

    private val outfitRepository = FirebaseOutfitRepository(FirebaseGarmentRepository)

    private var tags: List<String> = emptyList()

    private var adapter: OutfitSearchListAdapter? = null

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        loadInitialData()

    }

    override fun onResume() {
        super.onResume()
        triggerSearch()
    }


    private fun setupListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.btnProfile.setOnClickListener { /* TODO */ }

        binding.btnNewOutfit.setOnClickListener {
            startActivity(Intent(requireContext(), OutfitCreationActivity::class.java))
        }

        binding.btnFilter.setOnClickListener {
            SearchOutfitFilterFragment().show(childFragmentManager, "FilterFragment")
        }

        // Un solo listener para la búsqueda en tiempo real
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
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar outfits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            try {
                delay(500L)
                val searchText = binding.filteredGarmentSearchInput.text.toString().trim()

                val filterMap = mutableMapOf<String, Any>()
                if (searchText.isNotEmpty()) {
                    filterMap["name"] = searchText
                }
                if (tags.isNotEmpty()) {
                    filterMap["tags"] = tags
                }

                val outfits = outfitRepository.getAll(filterMap)
                updateOutfitList(outfits)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al buscar outfits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOutfitList(outfits: List<Outfit>) {
        adapter = OutfitSearchListAdapter(requireContext(), outfits.toMutableList())
        binding.outfitCardsListview.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}