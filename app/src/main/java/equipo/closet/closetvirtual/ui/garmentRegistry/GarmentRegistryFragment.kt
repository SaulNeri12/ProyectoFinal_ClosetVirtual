package equipo.closet.closetvirtual.ui.garmentRegistry

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.R

class GarmentRegistryFragment : Fragment() {

    private lateinit var garmentImageView: ImageView


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                garmentImageView.setImageBitmap(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_garment_registry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        garmentImageView = view.findViewById(R.id.garment_form_image)
//        val btnBack: ImageView = view.findViewById(R.id.btn_back)
//        val btnCamera: MaterialButton = view.findViewById(R.id.btn_camera)
//        val btnRegister: MaterialButton = view.findViewById(R.id.buttonRegister)
//        val inputName: EditText = view.findViewById(R.id.inputName)
//        val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category)
//        val spinnerColor: Spinner = view.findViewById(R.id.spinner_color)
//        val switchPrint: SwitchCompat = view.findViewById(R.id.switchPrint)


//        btnBack.setOnClickListener {
//
//            findNavController().navigateUp()
//        }
//
//        btnCamera.setOnClickListener {
//
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            cameraLauncher.launch(cameraIntent)
//        }
//
//        btnRegister.setOnClickListener {
//            val name = inputName.text.toString()
//            val category = spinnerCategory.selectedItem.toString()
//            val hasPrint = switchPrint.isChecked
//
//
//            if (name.isEmpty()) {
//                Toast.makeText(requireContext(), "El nombre de la prenda es obligatorio", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//
//            val confirmationMessage = "Prenda '$name' registrada en '$category'. Estampado: $hasPrint"
//            Toast.makeText(requireContext(), confirmationMessage, Toast.LENGTH_LONG).show()
//
//
//            findNavController().navigateUp()
//        }
//
//
//        setupSpinners(view)
    }

    private fun setupSpinners(view: View) {
        val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category)
        val spinnerColor: Spinner = view.findViewById(R.id.spinner_color)


        val categories = arrayOf("Category", "Top", "Bottom", "Zapatos", "Accesorio")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter


        val colors = arrayOf("Color", "Rojo", "Azul", "Verde", "Negro", "Blanco")
        val colorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
    }
}