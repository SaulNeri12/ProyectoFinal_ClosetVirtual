package equipo.closet.closetvirtual.ui.garmentRegistry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.databinding.FragmentGarmentRegistryBinding
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.adapters.ColorItem
import equipo.closet.closetvirtual.adapters.ColorSpinnerAdapter
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.utils.ChipGroupStyler
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class GarmentRegistryFragment : Fragment() {

    private lateinit var binding : FragmentGarmentRegistryBinding

    private val clothesRepository: Repository<Garment, String> = GarmentRepositoryFactory.create()

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galeryLauncher: ActivityResultLauncher<String>
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

            //set the chip group data
            setChipGroupData()
            //set the behavior of the camera button
            openCamera()
            //set the behavior of the camera launcher
            setUpCamaraBehavior()
            // set the behavior of the gallery launcher
            openGalery()
            //set the behavior of the camera launcher
            setUpGaleryBehavior()
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
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
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
                    Toast.makeText(requireContext(), "Error al guardar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openCamera() : Unit {
        binding.btnCamera.setOnClickListener {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(requireContext(),
                "${requireContext().packageName}.fileprovider", imageFile!!
            )
            cameraLauncher.launch(imageUri)
        }
    }

    private fun openGalery() : Unit {
        binding.btnUpload.setOnClickListener {
            galeryLauncher.launch("image/*")
        }
    }

    private fun createImageFile(): File {
        val storageDir = File(requireContext().filesDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()

        val name = "IMG_${System.currentTimeMillis()}"
        return File.createTempFile(name, ".jpg", storageDir)
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
            Toast.makeText(requireContext(), "No has seleccionado ninguna etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        if(tags.size > 5) {
            Toast.makeText(requireContext(), "No puedes seleccionar más de 5 etiquetas", Toast.LENGTH_SHORT).show()
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
            if (!validateInputFields()) return@setOnClickListener

            val name = binding.etGarmentName.text.toString()
            val color = getSelectedColor() ?: ""
            val category = binding.spGarmentCategory.selectedItem.toString()
            val print = binding.switchPrint.isChecked
            val userId = SessionManager.user.uid
            val tags = getTags()

            if (imageUri == null) {
                Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MediaManager.get().upload(imageUri)
                .unsigned("pokemon-preset")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.i("Cloudinary", "Inicio de carga $requestId")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.i("Cloudinary", "Cargando imagen: $bytes/$totalBytes")
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val imageUrl = resultData?.get("secure_url") as? String
                        if (imageUrl == null) {
                            Toast.makeText(requireContext(), "Error al obtener la URL de imagen", Toast.LENGTH_SHORT).show()
                            return
                        }

                        val newGarment = Garment(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            color = color,
                            category = category,
                            print = print,
                            imageUri = imageUrl,
                            userId = userId,
                            tags = tags
                        )

                        lifecycleScope.launch {
                            clothesRepository.insert(newGarment)
                            Toast.makeText(requireContext(), "Prenda registrada exitosamente", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_navigation_new_clothes_to_navigation_home)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Cloudinary", "Error al subir imagen: ${error?.description}")
                        Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.i("Cloudinary", "Subida reprogramada: ${error?.description}")
                    }
                })
                .dispatch()
        }
        /*
        binding.btnAddGarment.setOnClickListener {
            if (validateInputFields()) {
                val name = binding.etGarmentName.text.toString()
                val color = getSelectedColor() ?: ""
                val category = binding.spGarmentCategory.selectedItem.toString()
                val print = binding.switchPrint.isChecked
                val image = if (imageUri != null) imageUri.toString() else ""
                val userId = SessionManager.user.uid
                val tags = getTags()

                val newGarment = Garment(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    color = color,
                    category = category,
                    print =  print,
                    imageUri = image,
                    userId = userId,
                    tags = tags,
                )

                val requestId = MediaManager
                    .get()
                    .upload(imageUri)
                    .unsigned("pokemon-preset")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Log.i("Cloudinary", "Inicio de carga $requestId")
                        }

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                            Log.i("Cloudinary", "Cargando imagen: $bytes/$totalBytes")
                        }

                        override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                            val imageUrl = resultData?.get("secure_url") as? String

                            if (imageUrl == null) {
                                Toast.makeText(requireContext(), "Error al obtener URL de imagen", Toast.LENGTH_SHORT).show()
                                return
                            }

                            lifecycleScope.launch {
                                val name = binding.etGarmentName.text.toString()
                                val color = getSelectedColor() ?: ""
                                val category = binding.spGarmentCategory.selectedItem.toString()
                                val print = binding.switchPrint.isChecked
                                val userId = SessionManager.user.uid
                                val tags = getTags()

                                val newGarment = Garment(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    color = color,
                                    category = category,
                                    print = print,
                                    imageUri = imageUrl, // Aquí va la URL de Cloudinary
                                    userId = userId,
                                    tags = tags
                                )

                                clothesRepository.insert(newGarment)

                                Toast.makeText(requireContext(), "Prenda registrada exitosamente", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_navigation_new_clothes_to_navigation_home)
                            }
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Log.e("Cloudinary", "Error al subir imagen: ${error?.description}")
                            Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            Log.i("Cloudinary", "Subida reprogramada: ${error?.description}")
                        }
                    })
                    .dispatch()

                // go to home fragment
                findNavController().navigate(R.id.action_navigation_new_clothes_to_navigation_home)
            }
        }*/
    }

    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )

        val chipGroup = binding.chipGroupTags

        etiquetas.forEach { etiqueta ->
            ChipGroupStyler.addStyledChip(requireContext(), chipGroup, etiqueta, ChipGroupStyler.ChipStyle.SOFT_GRAY, true)
        }
        ChipGroupStyler.animateChipsStaggered(chipGroup)
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

}
