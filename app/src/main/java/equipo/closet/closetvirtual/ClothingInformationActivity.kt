package equipo.closet.closetvirtual

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ClothingInformationActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etTag: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var switchPrint: SwitchCompat
    private lateinit var btnEdit: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing_information)

        etName = findViewById(R.id.etName)
        etTag = findViewById(R.id.inputTag)
        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerColor = findViewById(R.id.spinner_color)
        switchPrint = findViewById(R.id.switchPrint)
        btnEdit = findViewById(R.id.btn_use)

        //set the input fields information
        setInfo()
        //fill the category spinner
        setCategorySpinner()
        //fill the color spinner
        setColorSpinner()
        //set the behavior of the edit button
        setEditButton()

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