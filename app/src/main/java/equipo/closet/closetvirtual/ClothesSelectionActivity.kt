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
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterFragment
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterViewModel
import equipo.closet.closetvirtual.ui.clothesselection.adapters.SelectableGarmentAdapter
import kotlinx.coroutines.launch

class ClothesSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClothesSelectionBinding
    private val viewModel: SearchOutfitFilterViewModel by viewModels()
    private lateinit var garmentAdapter: SelectableGarmentAdapter
    private var allGarments: List<Garment> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothesSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryFilter = intent.getStringExtra("CATEGORY_FILTER") ?: "all"

        setupRecyclerView()
        setupListeners()
        setupObservers()
        loadGarments(categoryFilter)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // CAMBIO: Ahora el botón de filtro abre tu SearchOutfitFilterFragment
        binding.btnFilter.setOnClickListener {
            SearchOutfitFilterFragment().show(supportFragmentManager, "FilterBottomSheet")
        }

        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            performFiltering(searchText = text.toString(), tags = viewModel.tags.value)
        }
    }

    private fun setupObservers() {
        // Este observador se activa cuando guardas los tags desde el filtro
        viewModel.tags.observe(this) { tags ->
            performFiltering(searchText = binding.filteredGarmentSearchInput.text.toString(), tags = tags)
        }
    }

    private fun setupRecyclerView() {
        garmentAdapter = SelectableGarmentAdapter(emptyList()) { selectedGarment ->
            val resultIntent = Intent().apply {
                putExtra("SELECTED_GARMENT_ID", selectedGarment.id)
                putExtra("CATEGORY_FILTER", intent.getStringExtra("CATEGORY_FILTER"))
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.rvGarments.apply {
            layoutManager = GridLayoutManager(this@ClothesSelectionActivity, 2)
            adapter = garmentAdapter
        }
    }

    private fun loadGarments(initialCategory: String) {
        lifecycleScope.launch {
            try {
                allGarments = if (initialCategory != "all") {
                    FirebaseGarmentRepository.getByCategory(initialCategory)
                } else {
                    FirebaseGarmentRepository.getAll()
                }
                performFiltering() // Aplica filtros iniciales (ninguno al principio)
            } catch (e: Exception) {
                Toast.makeText(this@ClothesSelectionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performFiltering(searchText: String? = null, tags: List<String>? = null) {
        var filteredList = allGarments

        if (!searchText.isNullOrBlank()) {
            filteredList = filteredList.filter { it.name.contains(searchText, ignoreCase = true) }
        }

        if (!tags.isNullOrEmpty()) {
            filteredList = filteredList.filter { garment ->
                garment.tags.any { tag -> tags.contains(tag) }
            }
        }
        garmentAdapter.updateData(filteredList)
    }
}