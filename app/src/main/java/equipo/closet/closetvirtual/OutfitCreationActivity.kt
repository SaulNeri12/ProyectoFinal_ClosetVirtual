package equipo.closet.closetvirtual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.databinding.ActivityOutfitCreationBinding
import equipo.closet.closetvirtual.databinding.OutfitCreationRowBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

class OutfitCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutfitCreationBinding

    private val viewModel: OutfitCreationViewModel by viewModels {
        // 1. GarmentRepository no necesita parámetros porque es un 'object'
        val garmentRepository = FirebaseGarmentRepository

        // 2. OutfitRepository SÍ necesita el garmentRepository que acabamos de referenciar
        val outfitRepository = FirebaseOutfitRepository(garmentRepository)

        // 3. Finalmente, la fábrica usa el outfitRepository que creamos
        OutfitCreationViewModelFactory(outfitRepository)
    }
    private val selectGarmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedGarmentId = result.data?.getStringExtra("SELECTED_GARMENT_ID")
            val category = result.data?.getStringExtra("CATEGORY_FILTER")

            if (selectedGarmentId != null && category != null) {
                lifecycleScope.launch {
                    val garment = FirebaseGarmentRepository.getById(selectedGarmentId)
                    if (garment != null) {
                        viewModel.addGarmentToOutfit(garment)
                        updateOutfitRowUI(category, garment)
                        Toast.makeText(this@OutfitCreationActivity, "${garment.name} añadido", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@OutfitCreationActivity, "Error: Prenda no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutfitCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de filas
        setupOutfitRow(binding.rowTop, "Top", R.drawable.ic_placeholder_garment)
        setupOutfitRow(binding.rowBottom, "Bottom", R.drawable.ic_placeholder_garment)
        setupOutfitRow(binding.rowBodysuit, "Bodysuit", R.drawable.ic_placeholder_garment)
        setupOutfitRow(binding.rowShoes, "Zapato", R.drawable.ic_placeholder_garment)
        setupOutfitRow(binding.rowAccessory, "Accesorio", R.drawable.ic_placeholder_garment)

        // Listeners de botones
        binding.btnBack.setOnClickListener { finish() }
        binding.btnUseOutfit.setOnClickListener { handleSaveOutfit() }

        // Observador para el resultado del guardado
        setupSaveObserver()
    }

    private fun setupOutfitRow(rowBinding: OutfitCreationRowBinding, category: String, placeholderResId: Int) {
        rowBinding.tvGarmentCategory.text = category
        rowBinding.ivGarmentPreview.setImageResource(placeholderResId)
        rowBinding.btnAddGarment.setOnClickListener {
            launchClothesSelection(category)
        }
    }

    private fun launchClothesSelection(category: String) {
        val intent = Intent(this, ClothesSelectionActivity::class.java).apply {
            putExtra("CATEGORY_FILTER", category)
        }
        selectGarmentLauncher.launch(intent)
    }

    private fun updateOutfitRowUI(category: String, garment: Garment) {
        val rowBinding = when (category) {
            "Top" -> binding.rowTop
            "Bottom" -> binding.rowBottom
            "Bodysuit" -> binding.rowBodysuit
            "Zapato" -> binding.rowShoes
            "Accesorio" -> binding.rowAccessory
            else -> null
        }
        rowBinding?.let {
            it.tvGarmentCategory.text = garment.name
            Glide.with(this)
                .load(garment.imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_garment)
                .into(it.ivGarmentPreview)
        }
    }

    private fun handleSaveOutfit() {
        val outfitName = binding.etOutfitName.text.toString().trim()
        if (outfitName.isEmpty()) {
            Toast.makeText(this, "Por favor, dale un nombre al outfit", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedTags = getSelectedTagsFromChipGroup()
        viewModel.saveOutfit(outfitName, selectedTags)
    }

    private fun getSelectedTagsFromChipGroup(): List<String> {
        return binding.chipGroupTags.checkedChipIds.mapNotNull { chipId ->
            findViewById<Chip?>(chipId)?.text.toString()
        }
    }

    private fun setupSaveObserver() {
        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "¡Outfit guardado con éxito!", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}