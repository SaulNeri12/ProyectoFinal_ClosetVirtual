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
import equipo.closet.closetvirtual.utils.ChipGroupStyler
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
                        // La Activity solo notifica al ViewModel la nueva prenda.
                        // El ViewModel se encargará de la lógica y de actualizar el LiveData.
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
        handleOnProfileButtonClicked()
        handleOnBackButtonClicked()
    }

    private fun setupListeners() {
        binding.btnSaveOutfit.setOnClickListener { handleSaveOutfit() }

        // Los listeners solo se preocupan de iniciar la selección para una categoría
        binding.rowTop.btnAddGarment.setOnClickListener { launchClothesSelection("Top") }
        binding.rowBottom.btnAddGarment.setOnClickListener { launchClothesSelection("Bottom") }
        binding.rowBodysuit.btnAddGarment.setOnClickListener { launchClothesSelection("Bodysuit") }
        binding.rowShoes.btnAddGarment.setOnClickListener { launchClothesSelection("Zapato") }
        binding.rowAccessory.btnAddGarment.setOnClickListener { launchClothesSelection("Accesorio") }
    }

    private fun setupObservers() {
        // Observador principal: Se activa cada vez que el outfit en el ViewModel cambia.
        viewModel.currentOutfit.observe(this) { outfit ->
            // Redibuja toda la UI para reflejar el estado actual del outfit.
            redrawAllRows(outfit)
        }

        // Observador para el resultado del guardado.
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

    // Redibuja el estado de todas las filas basado en el objeto Outfit actual.
    private fun redrawAllRows(outfit: Outfit) {
        val categories = listOf("Top", "Bottom", "Bodysuit", "Zapato", "Accesorio")
        categories.forEach { category ->
            val garmentForCategory = outfit.getGarmentForCategory(category)
            updateUiForRow(category, garmentForCategory)
        }
    }

    // Actualiza una fila específica para mostrar una prenda o el estado por defecto.
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
                // Estado con prenda: Muestra nombre, imagen y botón de borrar.
                binding.tvGarmentName.text = garment.name
                Glide.with(this).load(garment.imageUri).centerCrop().into(binding.ivGarmentPreview)
                binding.btnAddGarment.visibility = View.GONE
                binding.btnCleanGarment.visibility = View.VISIBLE

                binding.btnCleanGarment.setOnClickListener {
                    viewModel.removeGarmentFromOutfit(category)
                }
            } else {
                // Estado sin prenda: Muestra categoría, placeholder y botón de añadir.
                binding.tvGarmentName.text = category
                binding.ivGarmentPreview.setImageResource(R.drawable.ic_placeholder_garment)
                binding.btnAddGarment.visibility = View.VISIBLE
                binding.btnCleanGarment.visibility = View.GONE
            }
        }
    }

    private fun handleSaveOutfit() {
        val name = binding.etOutfitName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return
        }
        val tags = getSelectedTagsFromChipGroup()
        viewModel.saveOutfit(name, tags)
    }

    private fun getSelectedTagsFromChipGroup(): List<String> {
        return binding.chipGroupTags.checkedChipIds.mapNotNull { chipId ->
            findViewById<Chip?>(chipId)?.text.toString()
        }
    }

    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista")

        val chipGroup = binding.chipGroupTags

        etiquetas.forEach { etiqueta ->
            ChipGroupStyler.addStyledChip(this, chipGroup, etiqueta, ChipGroupStyler.ChipStyle.SOFT_GRAY, true)
        }
        ChipGroupStyler.animateChipsStaggered(chipGroup)
    }

    private fun handleOnProfileButtonClicked() {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleOnBackButtonClicked() {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            this.onBackPressed()
        }
    }

}