package equipo.closet.closetvirtual.ui.searchOutfit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterFragment // Asumiendo el nombre del filtro
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchOutfitFragment : Fragment() {

    private var _binding: FragmentSearchOutfitBinding? = null
    private val binding get() = _binding!!

    // Usamos activityViewModels para compartir el estado del filtro con el diálogo
    private val viewModel: SearchOutfitFilterViewModel by activityViewModels()

    private val outfitRepository = FirebaseOutfitRepository(FirebaseGarmentRepository)
    private val garmentRepository = FirebaseGarmentRepository

    // CORRECCIÓN: Se inicializa la lista para evitar errores
    private var tags: List<String> = emptyList()
    private var allGarmentsMap: Map<String, Garment> = emptyMap()
    private var adapter: OutfitsAdapter? = null

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

        // Se inicializa el adaptador una sola vez
        setupAdapter()

        // Se configuran los listeners y observers
        setupListeners()
        setupObservers()

        // Se cargan los datos iniciales
        loadInitialData()
    }

    private fun setupAdapter() {
        // Se crea el adaptador vacío. Se llenará cuando lleguen los datos.
        adapter = OutfitsAdapter(requireContext(), emptyList(), allGarmentsMap)
        binding.outfitCardsListview.adapter = adapter
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
        // Observa los cambios de tags desde el diálogo de filtro
        viewModel.tags.observe(viewLifecycleOwner) { newTags ->
            this.tags = newTags
            // Al cambiar los tags, se dispara una nueva búsqueda
            triggerSearch()
        }
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                // Carga todas las prendas una vez para tener sus datos (imágenes, etc.)
                allGarmentsMap = garmentRepository.getAll().associateBy { it.id }
                // Carga todos los outfits y los muestra
                val outfits = outfitRepository.getAll()
                updateOutfitList(outfits)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            try {
                delay(500L) // Pequeña espera para no buscar con cada letra tecleada

                // CORRECCIÓN: Se usa .text para obtener el contenido del EditText
                val searchText = binding.filteredGarmentSearchInput.text.toString().trim()

                // Se construye el mapa de filtros dinámicamente
                val filterMap = mutableMapOf<String, Any>()
                if (searchText.isNotEmpty()) {
                    filterMap["name"] = searchText
                }
                if (tags.isNotEmpty()) {
                    filterMap["tags"] = tags
                }

                // Se buscan los outfits con los filtros aplicados
                val outfits = outfitRepository.getAll(filterMap)
                updateOutfitList(outfits)

            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CORRECCIÓN: Esta función actualiza los datos del adaptador existente, no crea uno nuevo
    private fun updateOutfitList(outfits: List<Outfit>) {
        adapter?.updateData(outfits, allGarmentsMap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}