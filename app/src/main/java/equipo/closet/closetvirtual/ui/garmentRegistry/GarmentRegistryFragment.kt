package equipo.closet.closetvirtual.ui.garmentRegistry

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import java.io.File

class GarmentRegistryFragment : Fragment() {

    private val clothesRepository: Repository<Garment, Int> = GarmentRepositoryFactory.create()

    private lateinit var imageView: ImageView
    private lateinit var btnCamera : MaterialButton
    private lateinit var etName : TextInputEditText
    private lateinit var etTag : TextInputEditText
    private lateinit var spinnerCategory : Spinner
    private lateinit var spinnerColor : Spinner
    private lateinit var switchPrint : SwitchCompat
    private lateinit var btnRegister : MaterialButton

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageFile: File? = null
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_garment_registry, container, false)
    }

    /**
     * Initialize the fragment initialized variables here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            btnCamera = view.findViewById(R.id.btnCameraRegistry)
            etName = view.findViewById(R.id.etNameRegistry)
            etTag = view.findViewById(R.id.etTagRegistry)
            spinnerCategory = view.findViewById(R.id.spCategoryRegistry)
            spinnerColor = view.findViewById(R.id.spColorRegistry)
            switchPrint = view.findViewById(R.id.switchPrintRegistry)
            btnRegister = view.findViewById(R.id.btnAdd)
            imageView = view.findViewById(R.id.garmentImageRegistry)


            // --- THEN CALL METHODS THAT USE THESE VIEWS ---
            //set the behavior of the camera button
            openCamera()
            //set the behavior of the camera launcher
            setUpCamaraBehavior()
            //fill the category spinner
            setCategorySpinner()
            //fill the color spinner
            setColorSpinner()
            // Set up the register button listener (assuming you want to do this in onViewCreated)
            registerGarment() // You also call this from here, so btnRegister needs to be initialized first.

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
        btnCamera.setOnClickListener {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile!!
            )
            cameraLauncher.launch(imageUri)
        }
    }

    private fun createImageFile(): File {
        val storageDir = File(requireContext().filesDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()

        // Unique name
        // change this later kevin dont let yourself forget it
        return File.createTempFile("photo_", ".jpg", storageDir)
    }


    private fun setCategorySpinner() : Unit {
        // List of gender options
        val categoryOptions = listOf("Top", "Bottom", "Bodysuit", "Zapatos", "Accesorios")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            categoryOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spinnerCategory.adapter = adapter
    }

    private fun setColorSpinner() : Unit {
        // List of gender options
        val colorOptions = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            colorOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spinnerColor.adapter = adapter
    }

    private fun validateInputFields() : Boolean {
        //get the input fields
        val name = etName.text.toString()
        val tag = etTag.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val color = spinnerColor.selectedItem.toString()
        val image = imageUri.toString()

        //validate empty fields
        if (name.isEmpty()) {
            etName.error = "Ingrese un nombre"
            return false
        }
        if (tag.isEmpty()) {
            etTag.error = "Ingrese una etiqueta"
            return false
        }
        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Elija una categoria",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (color.isEmpty()) {
            Toast.makeText(requireContext(), "Elija un color",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (image.isEmpty()) {
            Toast.makeText(requireContext(), "Tome una foto de la prenda",
                Toast.LENGTH_SHORT).show()
            return false
        }

        //default case
        return true
    }

    private fun registerGarment() : Unit {
        btnRegister.setOnClickListener {
            if (validateInputFields()) {
                val name = etName.text.toString()
                val tag = etTag.text.toString()
                val category = spinnerCategory.selectedItem.toString()
                val color = spinnerColor.selectedItem.toString()
                val print = switchPrint.isChecked
                val image = imageUri.toString()

                val newGarment = Garment(
                    0,
                    name,
                    color,
                    tag,
                    category,
                    print,
                    imageUri = if (imageUri != null) imageUri.toString() else ""
                )

                // NOTE: Test message
                Toast.makeText(requireContext(), "Ruta imagen: ${imageUri}", Toast.LENGTH_LONG).show()

                this.clothesRepository.insert(newGarment)

                //succes mesagge
                Toast.makeText(requireContext(), "Prenda registrada exitosamente",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}