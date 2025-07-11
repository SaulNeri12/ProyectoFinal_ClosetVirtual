package equipo.closet.closetvirtual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.databinding.ActivityOutfitCreationBinding
import equipo.closet.closetvirtual.databinding.OutfitCreationRowDetailedBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.ui.clothesSelection.ClothesSelectionActivity
import kotlinx.coroutines.launch

class OutfitCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutfitCreationBinding
    private val viewModel: OutfitCreationViewModel by viewModels {
        OutfitCreationViewModelFactory(FirebaseOutfitRepository(FirebaseGarmentRepository))
    }

    private val selectGarmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val garmentId = result.data?.getStringExtra("SELECTED_GARMENT_ID")
            if (garmentId != null) {
                lifecycleScope.launch {
                    val garment = FirebaseGarmentRepository.getById(garmentId)
                    if (garment != null) {
                        viewModel.addGarmentToOutfit(garment)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutfitCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
        setChipGroupData()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSaveOutfit.setOnClickListener { handleSaveOutfit() }
        binding.btnProfile.setOnClickListener { /* TODO */ }

        binding.rowTop.btnAddGarment.setOnClickListener { launchClothesSelection("Top") }
        binding.rowBottom.btnAddGarment.setOnClickListener { launchClothesSelection("Bottom") }
        binding.rowBodysuit.btnAddGarment.setOnClickListener { launchClothesSelection("Bodysuit") }
        binding.rowShoes.btnAddGarment.setOnClickListener { launchClothesSelection("Zapato") }
        binding.rowAccessory.btnAddGarment.setOnClickListener { launchClothesSelection("Accesorio") }
    }

    private fun setupObservers() {
        viewModel.currentOutfit.observe(this) { outfit ->
            redrawAllRows(outfit)
        }

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "¡Outfit guardado con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchClothesSelection(category: String) {
        val intent = Intent(this, ClothesSelectionActivity::class.java).apply {
            putExtra("CATEGORY_FILTER", category)
        }
        selectGarmentLauncher.launch(intent)
    }

    private fun redrawAllRows(outfit: Outfit) {
        val categories = listOf("Top", "Bottom", "Bodysuit", "Zapato", "Accesorio")
        categories.forEach { category ->
            val garmentForCategory = outfit.getGarmentForCategory(category)
            updateUiForRow(category, garmentForCategory)
        }
    }

    private fun updateUiForRow(category: String, garment: Garment?) {
        val rowBinding = when (category) {
            "Top" -> binding.rowTop
            "Bottom" -> binding.rowBottom
            "Bodysuit" -> binding.rowBodysuit
            "Zapato" -> binding.rowShoes
            "Accesorio" -> binding.rowAccessory
            else -> null
        }

        rowBinding?.let { binding ->
            if (garment != null) {
                binding.tvGarmentName.text = garment.name
                Glide.with(this).load(garment.imageUri).centerCrop().into(binding.ivGarmentPreview)
                binding.btnAddGarment.visibility = View.GONE
                binding.btnCleanGarment.visibility = View.VISIBLE
                binding.btnCleanGarment.setOnClickListener {
                    viewModel.removeGarmentFromOutfit(category)
                }
            } else {
                binding.tvGarmentName.text = category
                binding.ivGarmentPreview.setImageResource(R.drawable.ic_placeholder_garment)
                binding.btnAddGarment.visibility = View.VISIBLE
                binding.btnCleanGarment.visibility = View.GONE
            }
        }
    }

    private fun handleSaveOutfit() {
        // Se llama a la función de validación antes de guardar
        if (validateForm()) {
            val name = binding.etOutfitName.text.toString().trim()
            val tags = getSelectedTagsFromChipGroup()
            viewModel.saveOutfit(name, tags)
        }
    }

    /**
     * NUEVA FUNCIÓN: Valida los campos del formulario.
     */
    private fun validateForm(): Boolean {
        // 1. Validar que el nombre no esté vacío
        if (binding.etOutfitName.text.toString().trim().isEmpty()) {
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }

        // 2. Validar las etiquetas
        val selectedTags = getSelectedTagsFromChipGroup()
        if (selectedTags.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos una etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedTags.size > 5) {
            Toast.makeText(this, "No puedes seleccionar más de 5 etiquetas", Toast.LENGTH_SHORT).show()
            return false
        }

        // Si todas las validaciones pasan, devuelve true
        return true
    }

    private fun getSelectedTagsFromChipGroup(): List<String> {
        return binding.chipGroupTags.checkedChipIds.mapNotNull { chipId ->
            findViewById<Chip?>(chipId)?.text.toString()
        }
    }

    private fun setChipGroupData() {
        val etiquetas = listOf("Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta", "Trabajo", "Deportivo")
        etiquetas.forEach { etiqueta ->
            val chip = Chip(this).apply { text = etiqueta; isCheckable = true }
            binding.chipGroupTags.addView(chip)
        }
    }
}
