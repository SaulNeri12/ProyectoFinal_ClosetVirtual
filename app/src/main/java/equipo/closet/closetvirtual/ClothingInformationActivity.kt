package equipo.closet.closetvirtual

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import equipo.closet.closetvirtual.adapters.ColorItem
import equipo.closet.closetvirtual.adapters.ColorSpinnerAdapter
import equipo.closet.closetvirtual.databinding.ActivityClothingInformationBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.utils.ChipGroupStyler
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// Actividad para gestionar la información de una prenda (ver, editar, eliminar).
class ClothingInformationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClothingInformationBinding

    // Launchers para la cámara y la galería.
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galeryLauncher: ActivityResultLauncher<String>
    // Archivo y URI de la imagen de la prenda.
    private var imageFile: File? = null
    private var imageUri: Uri? = null

    // Repositorio para operaciones de datos de prendas.
    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothingInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa los spinners, chips y la información de la prenda.
        setCategorySpinner()
        setColorSpinner()
        setChipGroupData()
        setGarmentInfo()

        // Configura los botones y sus acciones.
        handleGarmentEdit()
        openCamera()
        setUpCamaraBehavior()
        openGalery()
        setUpGaleryBehavior()
        setBtnBackBehavior()
        handleGarmentDelete()
    }

    // Configura el comportamiento del botón de retroceso.
    private fun setBtnBackBehavior() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    // Configura el launcher para la captura de imágenes con la cámara.
    private fun setUpCamaraBehavior() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageFile != null) {
                binding.garmentImage.setImageURI(imageUri)
            }
        }
    }

    // Configura el launcher para seleccionar imágenes de la galería.
    private fun setUpGaleryBehavior() {
        galeryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream = this.contentResolver.openInputStream(uri)
                    val copiedFile = createImageFile()
                    val outputStream = FileOutputStream(copiedFile)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    imageFile = copiedFile
                    imageUri = copiedFile.toUri()
                    binding.garmentImage.setImageURI(imageUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Abre la aplicación de la cámara.
    private fun openCamera() {
        binding.btnOpenCamera.setOnClickListener {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(this,
                "${this.packageName}.fileprovider", imageFile!!
            )
            cameraLauncher.launch(imageUri)
        }
    }

    // Abre la galería de imágenes.
    private fun openGalery() {
        binding.btnOpenGallery.setOnClickListener {
            galeryLauncher.launch("image/*")
        }
    }

    // Crea un archivo de imagen temporal.
    private fun createImageFile(): File {
        val storageDir = File(this.filesDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()
        val name = "IMG_${System.currentTimeMillis()}"
        return File.createTempFile(name, ".jpg", storageDir)
    }

    // Establece la información de la prenda en los campos de la interfaz.
    private fun setGarmentInfo(){
        val extras = intent.extras
        if (extras != null) {
            @Suppress("DEPRECATION")
            val garment = extras.getParcelable<Garment>("garment")
            binding.garmentImage.setImageURI(garment!!.imageUri.toUri())
            binding.etGarmentName.setText(garment.name)
            binding.switchPrint.isChecked = garment.print
            setSelectedTags(garment.tags)
            binding.spGarmentColor.setSelection(getIndex(binding.spGarmentColor, garment.color))
            binding.spGarmentCategory.setSelection(getIndex(binding.spGarmentCategory, garment.category))
        }
    }

    // Obtiene el índice de un elemento en un Spinner por su nombre.
    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            val item = spinner.getItemAtPosition(i)
            if (item is ColorItem && item.name.equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    // Configura el spinner de selección de color.
    private fun setColorSpinner() {
        val colors = listOf(
            ColorItem("Seleccionar color", R.color.gray_light),
            ColorItem("Rojo", R.color.garment_red),
            ColorItem("Azul", R.color.garment_blue),
            ColorItem("Verde", R.color.garment_green),
            ColorItem("Amarillo", R.color.garment_yellow),
            ColorItem("Naranja", R.color.garment_orange),
            ColorItem("Morado", R.color.garment_purple),
            ColorItem("Rosa", R.color.garment_pink),
            ColorItem("Café", R.color.garment_brown),
            ColorItem("Gris", R.color.garment_gray),
            ColorItem("Negro", R.color.garment_black),
            ColorItem("Blanco", R.color.garment_white),
            ColorItem("Beige", R.color.garment_beige),
            ColorItem("Azul Marino", R.color.garment_navy),
            ColorItem("Verde Azulado", R.color.garment_teal),
            ColorItem("Lima", R.color.garment_lime),
            ColorItem("Cian", R.color.garment_cyan),
            ColorItem("Índigo", R.color.garment_indigo),
            ColorItem("Ámbar", R.color.garment_amber),
            ColorItem("Naranja Intenso", R.color.garment_deep_orange),
            ColorItem("Azul Claro", R.color.garment_light_blue)
        )
        val adapter = ColorSpinnerAdapter(this, colors)
        binding.spGarmentColor.adapter = adapter
    }

    // Obtiene el color seleccionado del spinner.
    private fun getSelectedColor(): String? {
        val selectedPosition = binding.spGarmentColor.selectedItemPosition
        return if (selectedPosition > 0) {
            val colorItem = binding.spGarmentColor.selectedItem as ColorItem
            colorItem.name
        } else {
            null
        }
    }

    // Selecciona las etiquetas en el ChipGroup según una lista dada.
    private fun setSelectedTags(tags: MutableList<String>) {
        val chipGroup = binding.chipGroupTags
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.isChecked = tags.contains(chip?.text.toString())
        }
    }

    // Obtiene una lista de las etiquetas seleccionadas del ChipGroup.
    private fun getTags(): MutableList<String> {
        val selectedTags = mutableListOf<String>()
        for (i in 0 until  binding.chipGroupTags.childCount) {
            val chip =  binding.chipGroupTags.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedTags.add(chip.text.toString())
            }
        }
        return selectedTags
    }

    // Configura el spinner de selección de categoría.
    private fun setCategorySpinner() {
        val categoryOptions = listOf("Top", "Bottom", "Bodysuit", "Zapato", "Accesorio")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spGarmentCategory.adapter = adapter
        binding.spGarmentCategory.setSelection(0)
    }

    // Establece los datos para el ChipGroup de etiquetas.
    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )
        val chipGroup = binding.chipGroupTags
        etiquetas.forEach { etiqueta ->
            ChipGroupStyler.addStyledChip(this, chipGroup, etiqueta, ChipGroupStyler.ChipStyle.MINIMALIST, true)
        }
        ChipGroupStyler.animateChipsStaggered(chipGroup)
    }

    // Maneja la edición de la información de la prenda.
    private fun handleGarmentEdit(){
        binding.btnEditGarment.setOnClickListener {
            if (validateInputFields()){
                @Suppress("DEPRECATION")
                val id = intent.getParcelableExtra<Garment>("garment")?.id.toString()
                val name = binding.etGarmentName.text.toString()
                val color = getSelectedColor() ?: ""
                val category = binding.spGarmentCategory.selectedItem.toString()
                val print = binding.switchPrint.isChecked
                @Suppress("DEPRECATION")
                val image = if (this.imageUri != null) this.imageUri.toString()
                            else intent.getParcelableExtra<Garment>("garment")?.imageUri.toString()
                val userId = SessionManager.user.uid
                val tags = getTags()

                val editedGarment = Garment(
                    id = id,
                    name = name,
                    color = color,
                    category = category,
                    print = print,
                    imageUri =  image,
                    userId = userId,
                    tags = tags,
                )

                lifecycleScope.launch {
                    try{
                        clothesRepository.update(editedGarment)
                        Toast.makeText(this@ClothingInformationActivity, "Prenda actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    catch (e: Exception){
                        Toast.makeText(this@ClothingInformationActivity, e.message, Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
            }
        }
    }

    // Maneja la eliminación de una prenda.
    private fun handleGarmentDelete(){
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesion")
                .setMessage("¿Estás seguro de que quiere eliminar la prenda?")
                .setPositiveButton("Sí") { dialog, which ->
                    lifecycleScope.launch {
                        try{
                            @Suppress("DEPRECATION")
                            val id = intent.getParcelableExtra<Garment>("garment")?.id
                            clothesRepository.delete(id.toString())
                        }
                        catch (e: Exception){
                            Toast.makeText(this@ClothingInformationActivity, e.message, Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }
                    Toast.makeText(this, "Prenda eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Valida los campos de entrada del formulario.
    private fun validateInputFields() : Boolean {
        val name = binding.etGarmentName.text.toString()
        val tags = binding.chipGroupTags.checkedChipIds
        val category = binding.spGarmentCategory.selectedItem.toString()
        val color = getSelectedColor()
        val image = imageUri.toString()

        if (name.isEmpty()) {
            binding.etGarmentName.error = "Ingrese un nombre"
            return false
        }
        if(tags.isEmpty()){
            Toast.makeText(this, "No has seleccionado ninguna etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        if(tags.size > 5) {
            Toast.makeText(this, "No puedes seleccionar más de 5 etiquetas", Toast.LENGTH_SHORT).show()
            return false
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Elija una categoria",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (color.isNullOrEmpty()) {
            Toast.makeText(this, "Elija un color",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (image.isEmpty()) {
            Toast.makeText(this, "Tome una foto de la prenda",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
