
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

    private lateinit var binding : ActivityRegisterBinding

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
                    Toast.makeText(this, "Fecha inválida, use formato d-M-yyyy", Toast.LENGTH_LONG).show()
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
                        //Log.d("Register", "Intentando registrar usuario: ${user.email}")
                        userRepository.signUp(user)

                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } catch (e: RegistrationException) {
                        Toast.makeText(this@RegisterActivity, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Ocurrió un error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
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


    private fun setBackButtonBehavior() {
        binding.ivBack.setOnClickListener{
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
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = "El nombre es requerido"
            return false
        }
        if (binding.etMail.text.toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etMail.text.toString().trim()).matches()) {
            binding.etMail.error = "Introduce un email válido"
            return false
        }
        if (binding.etBirthDate.text.toString().trim().isEmpty()) {
            binding.etBirthDate.error = "La fecha de nacimiento es requerida"
            return false
        }
        if (binding.etPassword.text.toString().trim().length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (binding.etPassword.text.toString().trim() != binding.etConfirmPassword.text.toString().trim()) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
}
