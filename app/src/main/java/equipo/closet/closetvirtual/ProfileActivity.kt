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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.databinding.ActivityProfileBinding
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        editProfileInformation()
        // Update password
        updatePassword()
        // Set the back button behavior
        setBackBehavior()

    }

    /**
     * Set the user information in the fragment except the password fields
     */
    private fun setUserInformation(): Unit {
        // Set the user email
        binding.tvEmail.text = "example.com"
        // Set the user name
        binding.etName.setText("John Doe")
        // Set the user birth date
        binding.etBirthDate.setText("01/01/2000")
    }

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
    private fun setBirthDateFieldBehavior(): Unit {
        binding.etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)

                    // Get current date
                    val currentDate = Calendar.getInstance()

                    if (selectedDate.after(currentDate)) {
                        Toast.makeText(
                            this, "La fecha de nacimiento no puede ser posterior a la fecha actual",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                        binding.etBirthDate.setText(dat)
                    }
                }, year, month, day
            )
            datePickerDialog.show()
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
    private fun logout(): Unit {
        binding.layoutLogout.setOnClickListener {
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
        if (newPassword != confirmPassword) {
            binding.etConfirmNewPassword.error = "Las contraseñas no coinciden"
            Toast.makeText(
                this, "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT
            ).show()
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
                val name = binding.etName.text.toString()
                val birthDate = binding.etBirthDate.text.toString()
                val gender = binding.spGender.selectedItem.toString()

                //persist the user new information

                Toast.makeText(
                    this, "Se actualizó la información del perfil",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun updatePassword() {
        binding.btnUpdateProfilePassword.setOnClickListener {
            // 1. Validar los campos primero
            if (validatePasswordChange()) {
                val currentPassword = binding.etCurrentPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()

                // Si los campos están vacíos, no hacer nada (la validación ya lo permite)
                if (currentPassword.isEmpty() && newPassword.isEmpty()) {
                    return@setOnClickListener
                }

                // 2. Usar una corrutina para llamar al repositorio
                lifecycleScope.launch {
                    try {
                        // NOTA: Necesitarás el ID del usuario actual.
                        // Debes obtenerlo de SharedPreferences, un singleton, o como lo gestiones.
                        val currentUserId = "ID_DEL_USUARIO_LOGUEADO"

                        userRepository.updatePassword(currentUserId, currentPassword, newPassword)

                        // 3. Éxito: Notificar al usuario y limpiar campos
                        Toast.makeText(
                            this@ProfileActivity,
                            "Contraseña actualizada exitosamente",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.etCurrentPassword.text?.clear()
                        binding.etNewPassword.text?.clear()
                        binding.etConfirmNewPassword.text?.clear()

                    } catch (e: Exception) {
                        // 4. Error: Mostrar un mensaje adecuado

                        Toast.makeText(
                            this@ProfileActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}