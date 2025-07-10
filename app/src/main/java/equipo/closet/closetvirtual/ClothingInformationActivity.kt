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
import androidx.lifecycle.ViewModelProvider
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
import equipo.closet.closetvirtual.repositories.factories.GarmentUsageTrackerFactory
import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.deleteDialog.ConfirmDeleteDialog
import equipo.closet.closetvirtual.ui.deleteDialog.ConfirmDeleteViewModel
import equipo.closet.closetvirtual.ui.dialogLogout.ConfirmLogoutDialog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ClothingInformationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClothingInformationBinding

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private lateinit var galeryLauncher: ActivityResultLauncher<String>

    private var imageFile: File? = null

    private var imageUri: Uri? = null

    //repository instance for persisting data
    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

    private val garmentUsageTracker: IGarmentUsageTracker = GarmentUsageTrackerFactory.create()

    private lateinit var cofirmDelateViewModel: ConfirmDeleteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothingInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cofirmDelateViewModel = ViewModelProvider(this)[ConfirmDeleteViewModel::class.java]

        //fill the category spinner
        setCategorySpinner()
        //fill the color spinner
        setColorSpinner()
        //set the chip group data
        setChipGroupData()
        //set the input fields information
        setGarmentInfo()
        //set the behavior of the edit button
        handleGarmentEdit()
        //set the behavior of the camera button
        openCamera()
        //set the behavior of the camera launcher
        setUpCamaraBehavior()
        // set the behavior of the gallery launcher
        openGalery()
        //set the behavior of the camera launcher
        setUpGaleryBehavior()
        //set the behavior of the back button
        setBtnBackBehavior()
        //show the delete dialog
        showDeleteDialog()
        //observe the delete event
        observeDeleteEvent()

    }

    private fun setBtnBackBehavior() : Unit {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpCamaraBehavior() : Unit {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageFile != null) {
                // show img
                binding.garmentImage.setImageURI(imageUri)

                // pa guardar despues
                val imagePath = imageFile!!.absolutePath

                //para obtener la imagen
//                val imagePath = garment.imagePath
//                val imageFile = File(imagePath)
//                binding.garmentFormImage.setImageURI(imageFile.toUri())
            }
        }
    }

    private fun setUpGaleryBehavior() : Unit {
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

                    // Ahora imageFile!!.absolutePath es usable como en cámara
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openCamera() : Unit {
        binding.btnOpenCamera.setOnClickListener {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(this,
                "${this.packageName}.fileprovider", imageFile!!
            )
            cameraLauncher.launch(imageUri)
        }
    }

    private fun openGalery() : Unit {
        binding.btnOpenGallery.setOnClickListener {
            galeryLauncher.launch("image/*")
        }
    }

    private fun createImageFile(): File {
        val storageDir = File(this.filesDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()

        val name = "IMG_${System.currentTimeMillis()}"
        return File.createTempFile(name, ".jpg", storageDir)
    }

    private fun setGarmentInfo(){

        val extras = intent.extras

        if (extras != null) {

            @Suppress("DEPRECATION")
            val garment = extras.getParcelable<Garment>("garment")

            val id = garment!!.id.toString()
            binding.garmentImage.setImageURI(garment.imageUri.toUri())
            binding.etGarmentName.setText(garment.name)
            binding.switchPrint.isChecked = garment.print
            setSelectedTags(garment.tags)
            binding.spGarmentColor.setSelection(getIndex(binding.spGarmentColor, garment.color))
            binding.spGarmentCategory.setSelection(getIndex(binding.spGarmentCategory, garment.category))

        }

    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            val item = spinner.getItemAtPosition(i)
            if (item is ColorItem && item.name.equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun setColorSpinner() : Unit {
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

    private fun getSelectedColor(): String? {
        val selectedPosition = binding.spGarmentColor.selectedItemPosition
        return if (selectedPosition > 0) { // Excluir "Seleccionar color"
            val colorItem = binding.spGarmentColor.selectedItem as ColorItem
            colorItem.name
        } else {
            null
        }
    }

    /**
     * Set the selected tags in the chip group based on the list
     * of tags given by the parameter. If a chip's text exists in
     * the list, it gets selected.
     */
    private fun setSelectedTags(tags: MutableList<String>) {
        val chipGroup = binding.chipGroupTags

        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.isChecked = tags.contains(chip?.text.toString())
        }
    }

    private fun getTags(): MutableList<String> {
        val selectedTags = mutableListOf<String>()

        //make a loop to get the selected tags
        for (i in 0 until  binding.chipGroupTags.childCount) {
            val chip =  binding.chipGroupTags.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedTags.add(chip.text.toString())
            }
        }
        return selectedTags
    }

    private fun setCategorySpinner() {
        // List of gender options
        val categoryOptions = listOf("Top", "Bottom", "Bodysuit", "Zapato", "Accesorio")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        binding.spGarmentCategory.adapter = adapter
        //set the default value
        binding.spGarmentCategory.setSelection(0)
    }

    private fun setChipGroupData() {

        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )

        val chipGroup = binding.chipGroupTags

        etiquetas.forEach { etiqueta ->
            val chip = Chip(this).apply {
                text = etiqueta
                isCheckable = true
                isClickable = true
            }
            chipGroup.addView(chip)
        }
    }

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
                        //Succes mesagge
                        Toast.makeText(this@ClothingInformationActivity, "Prenda actualizada", Toast.LENGTH_SHORT).show()
                        //close the activity
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

    private fun handleGarmentDelete(){
        lifecycleScope.launch {
            try{
                @Suppress("DEPRECATION")
                val id = intent.getParcelableExtra<Garment>("garment")?.id
                clothesRepository.delete(id.toString())

                //Succes mesagge
                Toast.makeText(this@ClothingInformationActivity, "Prenda eliminada", Toast.LENGTH_SHORT).show()
                //Close the activity
                finish()
            }
            catch (e: Exception){
                Toast.makeText(this@ClothingInformationActivity, e.message, Toast.LENGTH_SHORT).show()
                return@launch
            }
        }
    }

    private fun observeDeleteEvent() {
        cofirmDelateViewModel.delete.observe(this) {
            handleGarmentDelete()
        }
    }

    private fun showDeleteDialog() {
        binding.btnDelete.setOnClickListener {
            ConfirmDeleteDialog().show(supportFragmentManager, "deleteDialog")
        }
    }

    private fun validateInputFields() : Boolean {
        //get the input fields
        val name = binding.etGarmentName.text.toString()
        val tags = binding.chipGroupTags.checkedChipIds
        val category = binding.spGarmentCategory.selectedItem.toString()
        val color = getSelectedColor()
        val image = imageUri.toString()

        //validate empty fields
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

        //default case
        return true
    }

}