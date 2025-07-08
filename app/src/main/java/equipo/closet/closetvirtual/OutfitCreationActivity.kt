package equipo.closet.closetvirtual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.databinding.ActivityOutfitCreationBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.GarmentCache
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository

class OutfitCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutfitCreationBinding

    // Inicialización del ViewModel mediante un Factory para inyectar dependencias.
    private val viewModel: OutfitCreationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OutfitCreationViewModel::class.java)) {
                    val garmentRepository = FirebaseGarmentRepository
                    val outfitRepository = FirebaseOutfitRepository(garmentRepository)
                    @Suppress("UNCHECKED_CAST")
                    return OutfitCreationViewModel(outfitRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // Launcher para obtener el resultado de la pantalla de selección de prendas.
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

        setupObservers()
        setupClickListeners()
        setChipGroupData()
    }

    /**
     * Configura todos los listeners de clicks para los elementos interactivos.
     */
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnUseOutfit.setOnClickListener {
            if (validateFields()) {
                val name = binding.etOutfitName.text.toString().trim()
                val tags = getSelectedTags()
                viewModel.saveOutfit(name, tags)
            }
        }

        // Configura el listener para cada botón de categoría
        setupGarmentButtonListener("top", binding.btnTop)
        setupGarmentButtonListener("bottom", binding.btnBottom)
        setupGarmentButtonListener("bodysuit", binding.btnBodysuit)
        setupGarmentButtonListener("shoes", binding.btnShoes)
        setupGarmentButtonListener("accessory", binding.btnAccessory)
    }

    /**
     * Asigna un listener a un botón de prenda. Decide si eliminar una prenda existente
     * o lanzar el selector para añadir una nueva.
     *
     * @param category La categoría de la prenda ("top", "bottom", etc.).
     * @param button El botón de ImageView que activa la acción.
     */
    private fun setupGarmentButtonListener(category: String, button: ImageView) {
        button.setOnClickListener {
            val currentGarment = viewModel.currentOutfit.value?.getClothes()
                ?.find { it.category.equals(category, ignoreCase = true) }

            if (currentGarment != null) {
                viewModel.removeGarmentFromOutfit(currentGarment.id)
            } else {
                launchGarmentSelector(category)
            }
        }
    }

    /**
     * Observa los LiveData del ViewModel para actualizar la UI en tiempo real.
     */
    private fun setupObservers() {
        viewModel.currentOutfit.observe(this) { outfit ->
            updateSelectedGarmentsUI(outfit)
        }

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Outfit guardado con éxito", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "Error al guardar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Lanza la GarmentSelectionActivity, pasando la categoría deseada como filtro.
     */
    private fun launchGarmentSelector(category: String) {

        val intent = Intent(this, ClothesSelectionActivity::class.java)
        intent.putExtra("CATEGORY_FILTER", category)
        selectGarmentLauncher.launch(intent)
    }

    /**
     * Actualiza la visibilidad y el contenido de todas las tarjetas de prendas
     * basándose en el estado actual del outfit en el ViewModel.
     */
    private fun updateSelectedGarmentsUI(outfit: Outfit?) {
        val garments = outfit?.getClothes() ?: emptyList()

        // Función auxiliar genérica para manejar la lógica de una tarjeta
        fun updateCard(cardView: View, buttonIcon: ImageView, textView: TextView, imageView: ImageView, garment: Garment?) {
            if (garment != null) {
                cardView.visibility = View.VISIBLE
                textView.text = garment.name
                buttonIcon.setImageResource(R.drawable.ic_delete) // Ícono de eliminar
                Glide.with(this).load(garment.imageUri).centerCrop().into(imageView)
            } else {
                cardView.visibility = View.GONE
                buttonIcon.setImageResource(R.drawable.ic_add) // Ícono de añadir
            }
        }

        // Actualiza cada tarjeta usando la función auxiliar y los IDs correctos del binding
        updateCard(binding.cardTop, binding.btnTop, binding.tvTopName, binding.ivTopImage, garments.find { it.category.equals("top", ignoreCase = true) })
        updateCard(binding.cardBottom, binding.btnBottom, binding.tvBottomName, binding.ivBottomImage, garments.find { it.category.equals("bottom", ignoreCase = true) })
        updateCard(binding.cardBodysuit, binding.btnBodysuit, binding.tvBodysuitName, binding.ivBodysuitImage, garments.find { it.category.equals("bodysuit", ignoreCase = true) })
        updateCard(binding.cardShoes, binding.btnShoes, binding.tvShoesName, binding.ivShoesImage, garments.find { it.category.equals("shoes", ignoreCase = true) })
        updateCard(binding.cardAccessory, binding.btnAccessory, binding.tvAccessoryName, binding.ivAccessoryImage, garments.find { it.category.equals("accessory", ignoreCase = true) })
    }

    /**
     * Valida que los campos requeridos (nombre, prendas, etiquetas) no estén vacíos.
     */
    private fun validateFields(): Boolean {
        if (binding.etOutfitName.text.toString().trim().isEmpty()) {
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }
        if (viewModel.currentOutfit.value?.getClothes().isNullOrEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos una prenda", Toast.LENGTH_SHORT).show()
            return false
        }
        if (getSelectedTags().isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos una etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Puebla el ChipGroup con la lista de etiquetas predefinidas.
     */
    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )
        binding.chipGroupTags.removeAllViews() // Previene duplicados
        etiquetas.forEach { etiqueta ->
            val chip = Chip(this).apply {
                text = etiqueta
                isCheckable = true
                isClickable = true
            }
            binding.chipGroupTags.addView(chip)
        }
    }

    /**
     * Devuelve una lista con el texto de los Chips seleccionados.
     */
    private fun getSelectedTags(): List<String> {
        return binding.chipGroupTags.checkedChipIds.map { chipId ->
            findViewById<Chip>(chipId).text.toString()
        }
    }
}