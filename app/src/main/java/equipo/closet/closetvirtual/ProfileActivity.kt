package equipo.closet.closetvirtual

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import equipo.closet.closetvirtual.databinding.ActivityProfileBinding
import equipo.closet.closetvirtual.entities.User
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.ui.dialogLogout.ConfirmLogoutDialog
import equipo.closet.closetvirtual.ui.dialogLogout.ConfirmLogoutViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date
import kotlin.String

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val userRepository: UserRepository = UserRepositoryFactory.create()

    private lateinit var confirmLogoutViewModel: ConfirmLogoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        confirmLogoutViewModel = ViewModelProvider(this)[ConfirmLogoutViewModel::class.java]

        // Fill the gender spinner
        fillGenderSpinner()
        // Set the behavior of the birth date field
        setBirthDateFieldBehavior()
        // Switch light mode
        switchLightMode()
        // Logout
        showLogoutDialog()
        // Observe logout event
        observeLogoutEvent()
        // Edit profile
        editProfileInformation()
        // Update password
        updatePassword()
        // Set the back button behavior
        setBackBehavior()
        // Set the user information
        setUserInformation()

    }

    /**
     * Set the user information in the fragment except the password fields
     */
    private fun setUserInformation(): Unit {
        //instance of the current user
        val currentUser = SessionManager.user

        // Set the user email
        binding.tvEmail.text = currentUser.email
        // Set the user name
        binding.etName.setText(currentUser.name)
        // Set the user birth date
        binding.etBirthDate.setText(setDateObject(currentUser.birthdate))
        // Set the user gender
        for (i in 0 until binding.spGender.adapter.count) {
            if (binding.spGender.getItemAtPosition(i).toString() == currentUser.gender) {
                binding.spGender.setSelection(i)
                break
            }
        }
    }

    /**
     * Closes the activity
     */
    private fun setBackBehavior(): Unit {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Fill the gender spinner with the gender options
     */
    private fun fillGenderSpinner(): Unit {
        // List of gender options
        val genderOptions = listOf("Masculino", "Femenino", "Indefinido")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        binding.spGender.adapter = adapter
        binding.spGender.setSelection(genderOptions.size - 1)
    }

    /**
     * Set the behavior of the birth date field
     */
    private fun setBirthDateFieldBehavior() {
        binding.birthDateContainer.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .build()

            datePicker.show(supportFragmentManager, "birth_date_picker")

            datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
                val currentDateMillis = System.currentTimeMillis()

                if (selectedDateMillis > currentDateMillis) {
                    Toast.makeText(this, "No puedes seleccionar una fecha futura", Toast.LENGTH_LONG).show()
                    return@addOnPositiveButtonClickListener
                }

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDateMillis

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)

                val dateString = "$day-$month-$year"
                binding.etBirthDate.setText(dateString)
            }
        }
    }

    /**
     * Switch light mode
     */
    private fun switchLightMode(): Unit {
        binding.scThemeMode.setOnCheckedChangeListener { _, isChecked ->
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
    private fun showLogoutDialog(): Unit {
        binding.layoutLogout.setOnClickListener {
            ConfirmLogoutDialog().show(supportFragmentManager, "confirmDialog")
        }
    }

    private fun observeLogoutEvent() {
        confirmLogoutViewModel.delete.observe(this) {
            FirebaseAuth.getInstance().signOut()

            // Go to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
        val name = binding.etName.text.toString()
        val birthDate = binding.etBirthDate.text.toString()
        val gender = binding.spGender.selectedItem.toString()
        val currentPassword = binding.etCurrentPassword.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            binding.etName.error = "El nombre es obligatorio"
            return false
        }
        if (birthDate.isEmpty()) {
            binding.etBirthDate.error = "Fecha de nacimiento obligatoria"
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "El genero es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (currentPassword.isNotEmpty()) {
            return if (validatePasswordChange()) false else true    }
        //default case
        return true
    }

    /**
     * Validate the password change fields
     */
    private fun validatePasswordChange(): Boolean {
        //get the input fields
        val currentPassword = binding.etCurrentPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmNewPassword.text.toString()

        //if the thre password fields are so we infer that there is no
        //password change
        if (currentPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            return true
        }

        //validate empty fields
        if (currentPassword.isEmpty()) {
            binding.etCurrentPassword.error = "La contraseña actual es requerida"
            Toast.makeText(
                this, "La contraseña actual es requerida",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (newPassword.isEmpty()) {
            binding.etNewPassword.error = "La nueva contraseña es requerida"
            Toast.makeText(
                this, "La nueva contraseña es requerida",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.etConfirmNewPassword.error = "La contrasena de confirmación es requerida"
            Toast.makeText(
                this, "La contrasena de confirmación es requerida",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.etNewPassword.text.toString().trim().length < 6) {
            binding.etNewPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        //verify if password contains a numerical value
        if (!binding.etNewPassword.text.toString().any { it.isDigit() }) {
            binding.etNewPassword.error = "La  nueva contraseña debe contener al menos un número"
            return false
        }
        if (binding.etNewPassword.text.toString().trim() !=
            binding.etConfirmNewPassword.text.toString().trim()) {
            binding.etNewPassword.error = "Las contraseñas no coinciden"

           binding.etConfirmNewPassword.error = "Las contraseñas no coinciden"
            return false
        }
        //default case
        return true
    }

    /**
     * Define the behavior of the edit profile button
     */
    private fun editProfileInformation(): Unit {
        binding.btnUpdateProfileInfo.setOnClickListener {
            if (validateInputFields()) {

                val currentUser = SessionManager.user

                val editedUser = User(
                    uid = currentUser.uid,
                    name = binding.etName.text.toString(),
                    email = currentUser.email,
                    gender = binding.spGender.selectedItem.toString(),
                    birthdate = getDateObject(),
                    password = currentUser.password,
                    profileImgUrl = "",
                    fireAuthUID = currentUser.fireAuthUID
                )

                Log.d("ProfileActivity", "Edited user: $editedUser")

                lifecycleScope.launch {
                    try {
                        //change the user in the repository
                        userRepository.update(editedUser)
                        //update the user in the session manager
                        SessionManager.user = editedUser
                        //show a toast for success
                        Toast.makeText(
                            this@ProfileActivity,
                            "Se actualizó la información del perfil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    catch (exception: Exception){
                        Toast.makeText(this@ProfileActivity,
                            "Error al actualizar la información",
                            Toast.LENGTH_LONG).show()
                    }
                }
                Log.d("ProfileActivity", "Session user: $SessionManager.user")
            }
        }
    }

    /**
     * From a date string returns a date object with the format d-M-yyyy
     */
    private fun getDateObject(): Date? {
        val dateString = binding.etBirthDate.text.toString().trim()
        val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
        val birthDateObject = try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }

        if (birthDateObject == null) {
            Toast.makeText(this, "Fecha inválida, use formato d-M-yyyy", Toast.LENGTH_LONG).show()
            return null
        }

        return birthDateObject
    }

    /**
     * From a date object returns a date string with the format d-M-yyyy
     */
    private fun setDateObject(date: Date?): String {
        return if (date != null) {
            val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
            dateFormat.format(date)
        } else {
            ""
        }
    }

    /**
     * Logic to update the user password
     */
    private fun updatePassword() {
        binding.btnUpdateProfilePassword.setOnClickListener {
            if (validatePasswordChange()) {
                val currentPassword = binding.etCurrentPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, completa todos los campos de contraseña.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    try {

                        userRepository.updatePassword(currentPassword, newPassword)

                        // Éxito
                        Toast.makeText(
                            this@ProfileActivity,
                            "Contraseña actualizada exitosamente",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.etCurrentPassword.text?.clear()
                        binding.etNewPassword.text?.clear()
                        binding.etConfirmNewPassword.text?.clear()

                    } catch (e: Exception) {
                        // Error
                        Toast.makeText(this@ProfileActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}