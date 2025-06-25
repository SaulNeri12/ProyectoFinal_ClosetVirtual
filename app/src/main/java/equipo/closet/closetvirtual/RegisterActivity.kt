package equipo.closet.closetvirtual

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var ivBack: android.widget.ImageView
    private lateinit var etName: EditText
    private lateinit var etMail: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etGender: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: android.widget.Button

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
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)

                    // Get current date
                    val currentDate = Calendar.getInstance()

                    if (selectedDate.after(currentDate)) {
                        Toast.makeText(this, "Birth date cannot be after current date", Toast.LENGTH_SHORT).show()
                    } else {
                        val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                        etBirthDate.setText(dat)
                    }
                }, year, month, day)
            datePickerDialog.show()
        }
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
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        etGender.adapter = adapter
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