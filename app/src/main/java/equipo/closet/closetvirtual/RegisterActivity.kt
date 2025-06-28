package equipo.closet.closetvirtual

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var ivBack: android.widget.ImageView
    private lateinit var etName: EditText
    private lateinit var etMail: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etGender: AppCompatSpinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: android.widget.Button
    private lateinit var birthDateContainer: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ivBack = findViewById<android.widget.ImageView>(R.id.ivBack)
        etName = findViewById(R.id.etName)
        etMail = findViewById(R.id.etMail)
        etBirthDate = findViewById(R.id.etBirthDate)
        etGender = findViewById(R.id.etGender)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        birthDateContainer = findViewById(R.id.birthDateContainer)

        //fill the gender spinner
        fillGenderSpinner()
        //declare the behavior of the back button
        setBackButtonBehavior()
        //declare the behavior of the birth date field
        setBirthDateFieldBehavior()
        //declare the behavior of the register button
        register()

    }

    /**
     * Set the behavior of the birth date field
     */
    private fun setBirthDateFieldBehavior() {
        birthDateContainer.setOnClickListener {
            showDatePicker()
        }
        etBirthDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona tu fecha de nacimiento")
            .setTheme(R.style.CustomDatePickerTheme)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(selectedDateInMillis))

            etBirthDate.setText(selectedDate)
        }

        datePicker.show(this.supportFragmentManager, "DATE_PICKER")
    }


    /**
     * Set the behavior of the back button
     */
    private fun setBackButtonBehavior() {
        ivBack.setOnClickListener{
            finish()
        }
    }

    /**
     * Fill the gender spinner with the gender options
     */
    private fun fillGenderSpinner() {
        // List of gender options
        val genderOptions = listOf("Masculino", "Femenino", "Indefinido")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        etGender.adapter = adapter
        etGender.setSelection(genderOptions.size - 1)
    }

    /**
     * Validate the input fields and return true if they are valid, false otherwise
     */
    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = etName.text.toString()
        val mail = etMail.text.toString()
        val birthDate = etBirthDate.text.toString()
        val gender = etGender.selectedItem.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            etName.error = "Name is required"
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mail.isEmpty()) {
            etMail.error = "Email is required"
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (birthDate.isEmpty()) {
            etBirthDate.error = "Birth date is required"
            Toast.makeText(this, "Birth date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirm password is required"
            Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        //Default case
        return true
    }

    /**
     * Set the behavior of the register button
     */
    private fun register() {
        btnRegister.setOnClickListener {
            if (validateInputFields()) {
                //start the login activity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

}