package equipo.closet.closetvirtual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.databinding.ActivityOutfitCreationBinding
import equipo.closet.closetvirtual.databinding.OutfitCreationRowBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.OutfitCreationViewModel
import equipo.closet.closetvirtual.entities.GarmentCache

class OutfitCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutfitCreationBinding

    // --- ESTA ES LA PARTE CORREGIDA ---
    // Inicializa el ViewModel usando un Factory que SÍ sabe cómo crear el ViewModel.
    private val viewModel: OutfitCreationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OutfitCreationViewModel::class.java)) {
                    // 1. Se crean las dependencias que el ViewModel necesita
                    val garmentRepository = FirebaseGarmentRepository
                    val outfitRepository = FirebaseOutfitRepository(garmentRepository)

                    // 2. Se crea y devuelve la instancia del ViewModel
                    @Suppress("UNCHECKED_CAST")
                    return OutfitCreationViewModel(outfitRepository) as T
                }
                // 3. Si se pide una clase de ViewModel desconocida, lanza el error
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // El resto de la clase no necesita cambios...

    // Launcher para recibir la prenda seleccionada de ClothesSelectionActivity
    private val selectGarmentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val garmentId = result.data?.getStringExtra("SELECTED_GARMENT_ID")
            garmentId?.let {
                GarmentCache.getGarmentById(it)?.let { garment ->
                    viewModel.addGarmentToOutfit(garment)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutfitCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRows()
        setupObservers()
        setupGlobalButtons()
    }

    private fun setupGlobalButtons() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnProfile.setOnClickListener {
            // Intent para ir a tu ProfileActivity
        }

        binding.btnUseOutfit.setOnClickListener {
            val outfitName = binding.etOutfitName.text.toString()
            // val tags = getSelectedTags()
            // viewModel.saveOutfit(outfitName, tags)
            Toast.makeText(this, "Guardando outfit...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRows() {
        setupRow(binding.rowTop, "top")
        setupRow(binding.rowBottom, "bottom")
        setupRow(binding.rowBodysuit, "bodysuit")
        setupRow(binding.rowShoes, "shoes")
        setupRow(binding.rowAccessory, "accessory")
    }

    private fun setupRow(rowBinding: OutfitCreationRowBinding, category: String) {
        rowBinding.tvGarmentName.text = "Seleccionar ${category.replaceFirstChar { it.uppercase() }}"

        rowBinding.root.setOnClickListener {
            val existingGarment = viewModel.currentOutfit.value?.getClothes()
                ?.find { it.category.equals(category, ignoreCase = true) }

            if (existingGarment != null) {
                viewModel.removeGarmentFromOutfit(existingGarment.id)
            } else {
                launchGarmentSelector(category)
            }
        }
    }

    private fun setupObservers() {
        viewModel.currentOutfit.observe(this) { outfit ->
            updateUi(outfit.getClothes())
        }
    }

    private fun updateUi(selectedGarments: List<Garment>) {
        updateRow(binding.rowTop, selectedGarments.find { it.category == "top" }, "top")
        updateRow(binding.rowBottom, selectedGarments.find { it.category == "bottom" }, "bottom")
        updateRow(binding.rowBodysuit, selectedGarments.find { it.category == "bodysuit" }, "bodysuit")
        updateRow(binding.rowShoes, selectedGarments.find { it.category == "shoes" }, "shoes")
        updateRow(binding.rowAccessory, selectedGarments.find { it.category == "accessory" }, "accessory")
    }

    private fun updateRow(rowBinding: OutfitCreationRowBinding, garment: Garment?, category: String) {
        if (garment != null) {
            rowBinding.tvGarmentName.text = garment.name
            Glide.with(this)
                .load(garment.imageUri)
                .placeholder(R.drawable.ic_ropa)
                .into(rowBinding.ivGarmentImage)
            rowBinding.btnAction.setImageResource(R.drawable.ic_delete)
        } else {
            rowBinding.tvGarmentName.text = "Seleccionar ${category.replaceFirstChar { it.uppercase() }}"
            rowBinding.ivGarmentImage.setImageResource(R.drawable.ic_add_ropa)
            rowBinding.btnAction.setImageResource(R.drawable.ic_add)
        }
    }

    private fun launchGarmentSelector(category: String) {
        val intent = Intent(this, ClothesSelectionActivity::class.java)
        intent.putExtra("CATEGORY_FILTER", category)
        selectGarmentLauncher.launch(intent)
    }
}