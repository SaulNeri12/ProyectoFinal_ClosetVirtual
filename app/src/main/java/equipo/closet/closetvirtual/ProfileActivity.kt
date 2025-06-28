package equipo.closet.closetvirtual

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnEditProfile: MaterialButton
    private lateinit var etConfirmPassword: EditText
    private lateinit var etCurrentPassword: EditText
    private lateinit var btnBack: MaterialButton
    private lateinit var etNewPassword: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var spGender: Spinner
    private lateinit var tvEmail: TextView
    private lateinit var etName: EditText

    private lateinit var scLightMode: SwitchCompat
    private lateinit var loyoutLogout: LinearLayout

    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnBack = findViewById<MaterialButton>(R.id.btnBackProfile)
        tvEmail = findViewById<TextView>(R.id.tvEmailProfile)
        etName = findViewById<EditText>(R.id.etNameProfile)
        spGender = findViewById<Spinner>(R.id.spGenderProfile)
        etBirthDate = findViewById<EditText>(R.id.etBirthDateProfile)
        etCurrentPassword = findViewById<EditText>(R.id.etCurrentPasswordProfile)
        etNewPassword =  findViewById<EditText>(R.id.etNewPasswordProfile)
        etConfirmPassword = findViewById<EditText>(R.id.etConfirmPasswordProfile)
        btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        scLightMode = findViewById<SwitchCompat>(R.id.scLightModeProfile)
        loyoutLogout = findViewById<LinearLayout>(R.id.logoutLayoutProfile)

        // Set the user information
        setUserInformation()
        // Fill the gender spinner
        fillGenderSpinner()
        // Set the behavior of the birth date field
        setBirthDateFieldBehavior()
        // Switch light mode
        switchLightMode()
        // Logout
        logout()
        // Edit profile
        editProfile()
        // Set the back button behavior
        setBackBehavior()

    }

    /**
     * Set the user information in the fragment except the password fields
     */
    private fun setUserInformation() : Unit{
        // Set the user email
        tvEmail.text = "example.com"
        // Set the user name
        etName.setText("John Doe")
        // Set the user gender
        spGender.setSelection(1)
        // Set the user birth date
        etBirthDate.setText("01/01/2000")
    }

    private fun setBackBehavior() : Unit {
        btnBack.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fill the gender spinner with the gender options
     */
    private fun fillGenderSpinner() : Unit {
        // List of gender options
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            genderOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spGender.adapter = adapter
    }

    /**
     * Set the behavior of the birth date field
     */
    private fun setBirthDateFieldBehavior() : Unit {
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
                        Toast.makeText(this, "La fecha de nacimiento no puede ser posterior a la fecha actual",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                        etBirthDate.setText(dat)
                    }
                }, year, month, day)
            datePickerDialog.show()
        }
    }

    /**
     * Switch light mode
     */
    private fun switchLightMode(): Unit {
        scLightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to light mode
            } else {
                // Switch to dark mode
            }
        }
    }

    /**
     * Set the behavior of the logout button
     */
    private fun logout(): Unit {
        loyoutLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesion")
                .setMessage("¿Estás seguro de que quiere salir de la sesión?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Go to the login activity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    /**
     * Validate the input fields fot the user to make sure each
     * field has the right input before editing the profile information
     *
     * @return true if the input fields are valid, false otherwise
     */
    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = etName.text.toString()
        val birthDate = etBirthDate.text.toString()
        val gender = spGender.selectedItem.toString()
        val currentPassword = etCurrentPassword.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            etName.error = "El nombre es obligatorio"
            return false
        }
        if (birthDate.isEmpty()) {
            etBirthDate.error = "Fecha de nacimiento obligatoria"
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "El genero es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (currentPassword.isNotEmpty()) {
            return if (validatePasswordChange()) false else true
        }
        //default case
        return true
    }

    /**
     * Validate the password change fields
     */
    private fun validatePasswordChange(): Boolean {
        //get the input fields
        val currentPassword = etCurrentPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        //if the thre password fields are so we infer that there is no
        //password change
        if (currentPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            return true
        }

        //validate empty fields
        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "La contraseña actual es requerida"
            Toast.makeText(this, "La contraseña actual es requerida",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword.isEmpty()) {
            etNewPassword.error = "La nueva contraseña es requerida"
            Toast.makeText(this, "La nueva contraseña es requerida",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "La contrasena de confirmación es requerida"
            Toast.makeText(this, "La contrasena de confirmación es requerida",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            Toast.makeText(this, "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT).show()
            return false
        }
        //default case
        return true
    }

    /**
     * Define the behavior of the edit profile button
     */
    private fun editProfile() : Unit {
        btnEditProfile.setOnClickListener {
            if (validateInputFields()) {
                val name = etName.text.toString()
                val birthDate = etBirthDate.text.toString()
                val gender = spGender.selectedItem.toString()
                val currentPassword = if (etCurrentPassword.toString().isEmpty() &&
                    etNewPassword.toString().isEmpty() &&
                    etConfirmPassword.toString().isEmpty()) etCurrentPassword.text.toString()
                else etNewPassword.text.toString()

                //persist the user new information

                Toast.makeText(
                    this, "Se actualizó la información del perfil",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

}