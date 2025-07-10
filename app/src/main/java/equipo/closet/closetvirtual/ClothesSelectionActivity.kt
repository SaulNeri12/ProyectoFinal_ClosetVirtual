package equipo.closet.closetvirtual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import equipo.closet.closetvirtual.databinding.ActivityClothesSelectionBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.global.NavigationHelper
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesSelectionFilter.ClothesSelectionViewModel
import equipo.closet.closetvirtual.ui.clothesselection.adapters.SelectableGarmentAdapter
import kotlinx.coroutines.launch

class ClothesSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClothesSelectionBinding
    private val viewModel: ClothesSelectionViewModel by viewModels()

    private lateinit var garmentAdapter: SelectableGarmentAdapter
    private val garmentRepository = FirebaseGarmentRepository
    private var allGarments: List<Garment> = listOf() // Caché local para la lista completa de prendas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothesSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibe la categoría a filtrar desde OutfitCreationActivity
        val categoryFilter = intent.getStringExtra("CATEGORY_FILTER") ?: "all"

        setupRecyclerView()
        setupListeners()
        setupObservers()

        loadGarments(categoryFilter)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        binding.btnOpenClothesRegistry.setOnClickListener {
            val intent = Intent(this, OutfitCreationActivity::class.java)
            startActivity(intent)
        }
        binding.btnFilter.setOnClickListener { ClothesCategoryFilterFragment().show(supportFragmentManager, "ClothesCategoryFilterFragment") }

        // Listener para la búsqueda por texto
        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            performFiltering(searchText = text.toString(), tags = viewModel.tags.value)
        }
    }

    private fun setupRecyclerView() {
        garmentAdapter = SelectableGarmentAdapter(emptyList()) { selectedGarment ->
            // Al hacer clic, devuelve el ID de la prenda y cierra la pantalla
            val resultIntent = Intent().apply {
                putExtra("SELECTED_GARMENT_ID", selectedGarment.id)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.rvGarments.apply {
            layoutManager = GridLayoutManager(this@ClothesSelectionActivity, 2)
            adapter = garmentAdapter
        }
    }

    private fun setupObservers() {
        // Cuando los tags del filtro cambian, volvemos a filtrar la lista
        viewModel.tags.observe(this) { tags ->
            performFiltering(searchText = binding.filteredGarmentSearchInput.text.toString(), tags = tags)
        }
    }

    private fun loadGarments(initialCategory: String) {
        lifecycleScope.launch {
            try {
                // Obtenemos todas las prendas UNA SOLA VEZ
                allGarments = garmentRepository.getAll()
                // Aplicamos el filtro inicial que viene de la pantalla anterior
                performFiltering(category = initialCategory)
            } catch (e: Exception) {
                Toast.makeText(this@ClothesSelectionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Función central que aplica todos los filtros a la lista de prendas.
     */
    private fun performFiltering(searchText: String? = null, tags: List<String>? = null, category: String? = null) {
        var filteredList = allGarments

        // 1. Filtrar por la categoría inicial (solo se aplica una vez)
        if (category != null && category != "all") {
            filteredList = filteredList.filter { it.category.equals(category, ignoreCase = true) }
        }

        // 2. Filtrar por el texto de búsqueda
        if (!searchText.isNullOrBlank()) {
            filteredList = filteredList.filter { it.name.contains(searchText, ignoreCase = true) }
        }

        // 3. Filtrar por los tags seleccionados
        if (!tags.isNullOrEmpty()) {
            filteredList = filteredList.filter { garment -> garment.tags.any { tag -> tags.contains(tag) } }
        }

        // Actualiza el adaptador con el resultado final
        garmentAdapter.updateData(filteredList)
    }
}
