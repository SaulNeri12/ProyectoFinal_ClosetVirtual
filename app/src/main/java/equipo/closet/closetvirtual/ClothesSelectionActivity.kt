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

    // Adaptador para el RecyclerView
    private lateinit var garmentAdapter: SelectableGarmentAdapter
    private val garmentRepository = FirebaseGarmentRepository
    private var allGarments: List<Garment> = listOf() // Caché local para las prendas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothesSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryFilter = intent.getStringExtra("CATEGORY_FILTER") ?: "all"

        setupRecyclerView()
        setupListeners()
        setViewModelObserver()

        loadGarments(categoryFilter)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnOpenClothesRegistry.setOnClickListener {
            NavigationHelper.openMainActivityAt(this, NavigationHelper.DEST_GARMENT_REGISTRY)
        }

        binding.btnFilter.setOnClickListener {
            ClothesCategoryFilterFragment().show(supportFragmentManager, "ClothesCategoryFilterFragment")
        }

        // Listener para la búsqueda por texto
        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            performFiltering(text.toString(), viewModel.tags.value)
        }
    }

    private fun setupRecyclerView() {
        garmentAdapter = SelectableGarmentAdapter(emptyList()) { selectedGarment ->
            // Cuando se hace clic en una prenda, devuelve su ID
            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_GARMENT_ID", selectedGarment.id)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.rvGarments.apply {
            layoutManager = GridLayoutManager(this@ClothesSelectionActivity, 2)
            adapter = garmentAdapter
        }
    }

    private fun setViewModelObserver() {
        // Cuando los tags del filtro cambian, volvemos a filtrar la lista
        viewModel.tags.observe(this) { tags ->
            performFiltering(binding.filteredGarmentSearchInput.text.toString(), tags)
        }
    }

    private fun loadGarments(initialCategory: String) {
        lifecycleScope.launch {
            try {
                // Obtenemos todas las prendas UNA SOLA VEZ y las guardamos en memoria
                allGarments = garmentRepository.getAll()
                // Aplicamos el filtro inicial que viene desde la pantalla de creación de outfit
                performFiltering(category = initialCategory)
            } catch (e: Exception) {
                Toast.makeText(this@ClothesSelectionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Esta función central se encarga de aplicar TODOS los filtros a la lista de prendas.
     */
    private fun performFiltering(searchText: String? = null, tags: List<String>? = null, category: String? = null) {
        var filteredList = allGarments

        // 1. Filtrar por categoría inicial (si se proporciona)
        if (category != null && category != "all") {
            filteredList = filteredList.filter { it.category.equals(category, ignoreCase = true) }
        }

        // 2. Filtrar por el texto de búsqueda
        if (!searchText.isNullOrEmpty()) {
            filteredList = filteredList.filter { it.name.contains(searchText, ignoreCase = true) }
        }

        // 3. Filtrar por los tags seleccionados en el fragmento
        if (!tags.isNullOrEmpty()) {
            filteredList = filteredList.filter { garment -> garment.tags.any { tag -> tags.contains(tag) } }
        }

        // Actualiza el adaptador con el resultado final
        garmentAdapter.updateData(filteredList)
    }
}