package equipo.closet.closetvirtual

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
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import androidx.core.net.toUri

class ClothingInformationActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var etName: TextInputEditText
    private lateinit var etTag: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var switchPrint: SwitchCompat
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnEditPhoto: MaterialButton

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageFile: File? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing_information)

        etName = findViewById(R.id.etName)
        etTag = findViewById(R.id.inputTag)
        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerColor = findViewById(R.id.spinner_color)
        switchPrint = findViewById(R.id.switchPrint)
        btnEdit = findViewById(R.id.btn_use)
        btnDelete = findViewById(R.id.btnDelete)
        btnEditPhoto = findViewById(R.id.btnEditPhoto)
        imageView = findViewById(R.id.imageView)

        val extras = intent.extras

        //set the input fields information
        setInfo()
        //fill the category spinner
        setCategorySpinner()
        //fill the color spinner
        setColorSpinner()
        //set the behavior of the edit button
        setEditButton()

        if (extras != null) {
            val id = extras.getInt("garment_id", -1)
            val name = extras.getString("garment_name") ?: ""
            val color = extras.getString("garment_color") ?: ""
            val tag = extras.getString("garment_tag") ?: ""
            val category = extras.getString("garment_category") ?: ""
            val print = extras.getBoolean("garment_print", false)
            val imageUri = extras.getString("garment_image_uri") ?: ""

            etName.setText(name)
            etTag.setText(tag)
            switchPrint.isChecked = print

            /*
            spinnerColor.setSelection(getIndexOfSpinnerItem(spinnerColor, color))
            spinnerCategory.setSelection(getIndexOfSpinnerItem(spinnerCategory, category))
             */

            if (imageUri.isNotEmpty()) {
                val uri = imageUri.toUri()
                Glide.with(this)
                    .load(uri)
                    .into(imageView)
            } else {
                Glide.with(this)
                    .load(R.mipmap.garment_bottom_test)
                    .into(imageView)
            }
        }

    }

    private fun getIndexOfSpinnerItem(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun setUpCamaraBehavior() : Unit {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageFile != null) {
                // show img
                imageView.setImageURI(imageUri)

                // pa guardar despues
                val imagePath = imageFile!!.absolutePath

                //para obtener la imagen
//                val imagePath = garment.imagePath
//                val imageFile = File(imagePath)
//                binding.garmentFormImage.setImageURI(imageFile.toUri())
            }
        }
    }

    private fun openCamera() : Unit {
        btnEditPhoto.setOnClickListener {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.fileprovider",
                imageFile!!
            )
            cameraLauncher.launch(imageUri)
        }
    }

    private fun createImageFile(): File {
        val storageDir = File(this.filesDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()

        // Unique name
        // change this later kevin dont let yourself forget it
        return File.createTempFile("photo_", ".jpg", storageDir)
    }

    private fun setInfo(){
        //set the input fields
        etName.setText("La irreverente")
        etTag.setText("Formal")
        spinnerCategory.setSelection(2)
        spinnerColor.setSelection(2)
        switchPrint.isChecked = true
    }


    private fun setCategorySpinner() {
        // List of gender options
        val categoryOptions = listOf("Top", "Bottom", "Bodysuit", "Zapato", "Accesorio")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spinnerCategory.adapter = adapter
    }

    private fun setColorSpinner() {
        // List of gender options
        val colorOptions = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            colorOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spinnerColor.adapter = adapter
    }

    private fun setEditButton(){
        btnEdit.setOnClickListener {
            if (validateInputFields()){
                val name = etName.text.toString()
                val tag = etTag.text.toString()
                val category = spinnerCategory.selectedItem.toString()
                val color = spinnerColor.selectedItem.toString()
                val print = switchPrint.isChecked

                //succes mesagge
                Toast.makeText(this, "Clothes edited successfully",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = etName.text.toString()
        val tag = etTag.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val color = spinnerColor.selectedItem.toString()

        //validate empty fields
        if (name.isEmpty()) {
            etName.error = "El nombre es requerido"
            return false
        }
        if (tag.isEmpty()) {
            etTag.error = "Elija una etiqueta"
            return false
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Elija una categoría",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (color.isEmpty()) {
            Toast.makeText(this, "Elija un color",
                Toast.LENGTH_SHORT).show()
            return false
        }
        //default case
        return true
    }

}