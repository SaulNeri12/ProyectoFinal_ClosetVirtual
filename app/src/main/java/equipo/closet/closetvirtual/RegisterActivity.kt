package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import equipo.closet.closetvirtual.databinding.ActivityRegisterBinding
import equipo.closet.closetvirtual.entities.User
import equipo.closet.closetvirtual.repositories.exceptions.RegistrationException
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillGenderSpinner()
        setBackButtonBehavior()
        setBirthDateFieldBehavior()
        register()
    }

    private fun register() {
        binding.btnRegister.setOnClickListener {
            if (validateInputFields()) {

                val dateString = binding.etBirthDate.text.toString().trim()
                val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
                val birthDateObject = try {
                    dateFormat.parse(dateString)
                } catch (e: Exception) {
                    null
                }

                if (birthDateObject == null) {
                    Toast.makeText(this, "Fecha inválida, usa formato d-M-yyyy", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val user = User(
                    uid = UUID.randomUUID().toString(),
                    name = binding.etName.text.toString().trim(),
                    email = binding.etMail.text.toString().trim(),
                    gender = binding.etGender.selectedItem.toString(),
                    birthdate = birthDateObject,
                    password = binding.etPassword.text.toString()
                )

                lifecycleScope.launch {
                    binding.btnRegister.isEnabled = false
                    try {
                        userRepository.signUp(user)

                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } catch (e: RegistrationException) {
                        Toast.makeText(this@RegisterActivity, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        binding.btnRegister.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setBirthDateFieldBehavior() {
        binding.birthDateContainer.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .build()

            datePicker.show(supportFragmentManager, "birth_date_picker")

            datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
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

    private fun setBackButtonBehavior() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun fillGenderSpinner() {
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.etGender.adapter = adapter
    }

    private fun validateInputFields(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etMail.text.toString().trim()
        val birthDateStr = binding.etBirthDate.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val gender = binding.etGender.selectedItem.toString()

        val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
        val birthDate = try {
            dateFormat.parse(birthDateStr)
        } catch (e: Exception) {
            null
        }

        // Validación de nombre
        if (name.isEmpty()) {
            binding.etName.error = "El nombre es requerido"
            return false
        } else if (!name.contains(" ")) {
            binding.etName.error = "Ingresa tu nombre completo"
            return false
        } else if (name.length > 50) {
            binding.etName.error = "Nombre demasiado largo (máx. 50 caracteres)"
            return false
        } else if (!Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+\$").matches(name)) {
            binding.etName.error = "El nombre solo debe contener letras y espacios"
            return false
        }

        // Validación de email
        val suspiciousDomains = listOf("mailinator.com", "guerrillamail.com", "10minutemail.com")
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etMail.error = "Introduce un email válido"
            return false
        } else if (email.length > 60) {
            binding.etMail.error = "Email demasiado largo (máx. 60 caracteres)"
            return false
        } else if (suspiciousDomains.any { email.endsWith(it) }) {
            binding.etMail.error = "No se permiten correos temporales"
            return false
        }

        // Validación de género
        if (gender.isBlank() || gender !in listOf("Masculino", "Femenino", "Otro")) {
            Toast.makeText(this, "Selecciona un género válido", Toast.LENGTH_LONG).show()
            return false
        }

        // Validación de fecha de nacimiento
        if (birthDate == null) {
            binding.etBirthDate.error = "Formato de fecha inválido. Usa d-M-yyyy"
            return false
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -13)
            if (birthDate.after(calendar.time)) {
                binding.etBirthDate.error = "Debes tener al menos 13 años"
                return false
            }

            val oldestAllowed = Calendar.getInstance()
            oldestAllowed.add(Calendar.YEAR, -120)
            if (birthDate.before(oldestAllowed.time)) {
                binding.etBirthDate.error = "Edad no válida"
                return false
            }
        }

        // Validación de contraseña
        val commonPasswords = listOf("123456", "password", "123456789", "qwerty", "abc123")
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")

        if (password.length < 8) {
            binding.etPassword.error = "La contraseña debe tener al menos 8 caracteres"
            return false
        } else if (!passwordPattern.containsMatchIn(password)) {
            binding.etPassword.error = "Debe incluir mayúscula, minúscula, número y carácter especial"
            return false
        } else if (password in commonPasswords) {
            binding.etPassword.error = "La contraseña es demasiado común"
            return false
        } else if (password.length > 50) {
            binding.etPassword.error = "Contraseña demasiado larga"
            return false
        }

        // Validación de confirmación de contraseña
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }

        return true
    }
}
