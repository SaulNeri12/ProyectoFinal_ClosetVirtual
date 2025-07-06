package equipo.closet.closetvirtual.ui.garmentRegistry

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionBinding
import equipo.closet.closetvirtual.databinding.FragmentGarmentRegistryBinding
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.adapters.ColorItem
import equipo.closet.closetvirtual.adapters.ColorSpinnerAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class GarmentRegistryFragment : Fragment() {

    private lateinit var binding : FragmentGarmentRegistryBinding

    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()


    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageFile: File? = null
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentGarmentRegistryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initialize the fragment initialized variables here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set the behavior of the camera button
        openCamera()
        //set the behavior of the camera launcher
        setUpCamaraBehavior()
        //fill the category spinner
        setCategorySpinner()
        //fill the color spinner
        setColorSpinner()
        // Set up the register button listener (assuming you want to do this in onViewCreated)
        registerGarment()
        //set the behavior of the back button
        setBackBehavior()
        //set the behavior of the profile button
        setProfileBehavior()

    }

    private fun setBackBehavior() : Unit {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setProfileBehavior() : Unit {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
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
        binding.btnCamera.setOnClickListener {
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
        binding.spGarmentCategory.adapter = adapter
        //set the default value
        binding.spGarmentCategory.setSelection(0)
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

        val adapter = ColorSpinnerAdapter(requireContext(), colors)
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

    private fun validateInputFields() : Boolean {
        //get the input fields
        val name = binding.etGarmentName.text.toString()
        val tag = binding.etGarmentTag.text.toString()
        val category = binding.spGarmentCategory.selectedItem.toString()
        val color = getSelectedColor()
        val image = imageUri.toString()

        //validate empty fields
        if (name.isEmpty()) {
            binding.etGarmentName.error = "Ingrese un nombre"
            return false
        }
        if (tag.isEmpty()) {
            binding.etGarmentTag.error = "Ingrese una etiqueta"
            return false
        }
        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Elija una categoria",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (color.isNullOrEmpty()) {
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
        binding.btnAddGarment.setOnClickListener {
            if (validateInputFields()) {
                val name = binding.etGarmentName.text.toString()
                val tag = binding.etGarmentTag.text.toString()
                val category = binding.spGarmentCategory.selectedItem.toString()
                val color = getSelectedColor() ?: ""
                val print = binding.switchPrint.isChecked
                val image = if (imageUri != null) imageUri.toString() else ""

                val newGarment = Garment(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    color = color,
                    category = category,
                    print = print,
                    imageUri = image
                )

                // NOTE: Test message
                Toast.makeText(requireContext(), "Ruta imagen: ${imageUri}", Toast.LENGTH_LONG).show()

                lifecycleScope.launch {
                    clothesRepository.insert(newGarment)

                    //succes mesagge
                    Toast.makeText(
                        requireContext(), "Prenda registrada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}