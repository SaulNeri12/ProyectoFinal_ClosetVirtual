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
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import equipo.closet.closetvirtual.databinding.ActivityClothingInformationBinding
import java.io.File

class ClothingInformationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClothingInformationBinding

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageFile: File? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing_information)


        val extras = intent.extras

        //set the input fields information
        setInfo()
        //fill the category spinner
        setCategorySpinner()
        //fill the color spinner
        setColorSpinner()
        //set the behavior of the edit button
        setEditButton()
        //set the behavior of the camera button
        setUpCamaraBehavior()
        //open the camera
        openCamera()
        //set the behavior of the back button
        setBtnBackBehavior()
        //set the behavior of the delete button
        setBtnDeleteBehavior()

    }

    private fun setBtnBackBehavior() : Unit {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setBtnDeleteBehavior() : Unit {
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesion")
                .setMessage("¿Estás seguro de que quiere eliminar la prenda?")
                .setPositiveButton("Sí") { dialog, which ->
                    finish()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
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

    private fun openCamera() : Unit {
        binding.btnEditImage.setOnClickListener {
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

        val extras = intent.extras

        if (extras != null) {
            val id = extras.getInt("garment_id", -1)
            val name = extras.getString("garment_name") ?: ""
            val color = extras.getString("garment_color") ?: ""
            val tag = extras.getString("garment_tag") ?: ""
            val category = extras.getString("garment_category") ?: ""
            val print = extras.getBoolean("garment_print", false)
            val imageUri = extras.getString("garment_image_uri") ?: ""

            binding.etGarmentName.setText(name)
            binding.etGarmentTag.setText(tag)
            binding.switchPrint.isChecked = print


            binding.spGarmentColor.setSelection(getIndex(binding.spGarmentColor, color))
            binding.spGarmentCategory.setSelection(getIndex(binding.spGarmentCategory, category))


            if (imageUri.isNotEmpty()) {
                val uri = imageUri.toUri()
                Glide.with(this)
                    .load(uri)
                    .into(binding.garmentImage)
            } else {
                Glide.with(this)
                    .load(R.mipmap.garment_bottom_test)
                    .into(binding.garmentImage)
            }

        }

    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0..<spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }

        return 0
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

    private fun setColorSpinner() {
        // List of gender options
        val colorOptions = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            colorOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        binding.spGarmentColor.adapter = adapter
        //set the default value
        binding.spGarmentColor.setSelection(0)
    }

    private fun setEditButton(){
        binding.btnEditGarment.setOnClickListener {
            if (validateInputFields()){
                val name = binding.etGarmentName.text.toString()
                val tag = binding.etGarmentTag.text.toString()
                val category = binding.spGarmentCategory.selectedItem.toString()
                val color = binding.spGarmentColor.selectedItem.toString()
                val print = binding.switchPrint.isChecked

                //succes mesagge
                Toast.makeText(this, "Clothes edited successfully",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = binding.etGarmentName.text.toString()
        val tag = binding.etGarmentTag.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            binding.etGarmentName.error = "El nombre es requerido"
            return false
        }
        if (tag.isEmpty()) {
            binding.etGarmentTag.error = "Elija una etiqueta"
            return false
        }
        //default case
        return true
    }

}