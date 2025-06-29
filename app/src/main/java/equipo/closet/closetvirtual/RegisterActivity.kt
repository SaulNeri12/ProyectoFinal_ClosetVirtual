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
import equipo.closet.closetvirtual.databinding.ActivityLoginBinding
import equipo.closet.closetvirtual.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.birthDateContainer.setOnClickListener {
            showDatePicker()
        }
        binding.etBirthDate.setOnClickListener {
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

            binding.etBirthDate.setText(selectedDate)
        }

        datePicker.show(this.supportFragmentManager, "DATE_PICKER")
    }


    /**
     * Set the behavior of the back button
     */
    private fun setBackButtonBehavior() {
        binding.ivBack.setOnClickListener{
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
        binding.etGender.adapter = adapter
        binding.etGender.setSelection(genderOptions.size - 1)
    }

    /**
     * Validate the input fields and return true if they are valid, false otherwise
     */
    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = binding.etName.text.toString()
        val mail = binding.etMail.text.toString()
        val birthDate = binding.etBirthDate.text.toString()
        val gender = binding.etGender.selectedItem.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mail.isEmpty()) {
            binding.etMail.error = "Email is required"
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (birthDate.isEmpty()) {
            binding.etBirthDate.error = "Birth date is required"
            Toast.makeText(this, "Birth date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Confirm password is required"
            Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
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
        binding.btnRegister.setOnClickListener {
            if (validateInputFields()) {
                //start the login activity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

}