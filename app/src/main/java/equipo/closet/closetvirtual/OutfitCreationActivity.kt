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
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
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
            val category = result.data?.getStringExtra("CATEGORY_FILTER")

            if (garmentId != null && category != null) {
                lifecycleScope.launch {
                    val garment = FirebaseGarmentRepository.getById(garmentId)
                    if (garment != null) {
                        viewModel.addGarmentToOutfit(garment)
                        updateUiForRow(category, garment)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutfitCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialState()
        setupListeners()
        setupSaveObserver()
        setChipGroupData()
    }

    private fun setupInitialState() {
        updateUiForRow("Top", null)
        updateUiForRow("Bottom", null)
        updateUiForRow("Bodysuit", null)
        updateUiForRow("Zapato", null)
        updateUiForRow("Accesorio", null)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnProfile.setOnClickListener { /* TODO */ }
        binding.btnSaveOutfit.setOnClickListener { handleSaveOutfit() }

        binding.rowTop.btnAddGarment.setOnClickListener { launchClothesSelection("Top") }
        binding.rowBottom.btnAddGarment.setOnClickListener { launchClothesSelection("Bottom") }
        binding.rowBodysuit.btnAddGarment.setOnClickListener { launchClothesSelection("Bodysuit") }
        binding.rowShoes.btnAddGarment.setOnClickListener { launchClothesSelection("Zapato") }
        binding.rowAccessory.btnAddGarment.setOnClickListener { launchClothesSelection("Accesorio") }
    }

    private fun launchClothesSelection(category: String) {
        val intent = Intent(this, ClothesSelectionActivity::class.java).apply {
            putExtra("CATEGORY_FILTER", category)
        }
        selectGarmentLauncher.launch(intent)
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
                // Si hay una prenda: muestra su info y el botón de borrar
                binding.tvGarmentName.text = garment.name
                Glide.with(this).load(garment.imageUri).centerCrop().into(binding.ivGarmentPreview)
                binding.btnAddGarment.visibility = View.GONE
                binding.btnCleanGarment.visibility = View.VISIBLE

                binding.btnCleanGarment.setOnClickListener {
                    viewModel.removeGarmentFromOutfit(garment.id)
                    updateUiForRow(category, null) // Resetea la UI de esta fila
                }
            } else {
                // Si no hay prenda: muestra la categoría y el botón de añadir
                binding.tvGarmentName.text = category
                binding.ivGarmentPreview.setImageResource(R.drawable.ic_placeholder_garment)
                binding.btnAddGarment.visibility = View.VISIBLE
                binding.btnCleanGarment.visibility = View.GONE
            }
        }
    }

    private fun handleSaveOutfit() {
        if (validateFields()) {
            val name = binding.etOutfitName.text.toString().trim()
            val tags = getSelectedTagsFromChipGroup()
            viewModel.saveOutfit(name, tags)
        }
    }

    private fun setupSaveObserver() {
        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Outfit guardado con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun validateFields(): Boolean {
        if (binding.etOutfitName.text.toString().trim().isEmpty()) {
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }
        return true
    }
}