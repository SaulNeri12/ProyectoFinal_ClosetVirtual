
package equipo.closet.closetvirtual

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

    private val userRepository: UserRepository = UserRepositoryFactory.create()

    private lateinit var btnRegister: android.widget.Button
    private lateinit var ivBack: android.widget.ImageView
    private lateinit var etName: EditText
    private lateinit var etMail: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etGender: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText


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

        fillGenderSpinner()
        setBackButtonBehavior()
        setBirthDateFieldBehavior()
        register()
    }

    private fun register() {
        btnRegister.setOnClickListener {
            if (validateInputFields()) {

                val dateString = etBirthDate.text.toString().trim()
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
                    name = etName.text.toString().trim(),
                    email = etMail.text.toString().trim(),
                    gender = etGender.selectedItem.toString(),
                    birthdate = birthDateObject,
                    password = etPassword.text.toString()
                )

                lifecycleScope.launch {
                    btnRegister.isEnabled = false
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
                        btnRegister.isEnabled = true
                    }
                }
            }
        }
    }

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
                    val currentDate = Calendar.getInstance()
                    if (selectedDate.after(currentDate)) {
                        Toast.makeText(this, "La fecha de nacimiento no puede ser futura", Toast.LENGTH_SHORT).show()
                    } else {
                        // El formato aquí "d-M-yyyy"
                        val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                        etBirthDate.setText(dat)
                    }
                }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun setBackButtonBehavior() {
        ivBack.setOnClickListener{
            finish()
        }
    }

    private fun fillGenderSpinner() {
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etGender.adapter = adapter
    }

    private fun validateInputFields(): Boolean {
        if (etName.text.toString().trim().isEmpty()) {
            etName.error = "El nombre es requerido"
            return false
        }
        if (etMail.text.toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(etMail.text.toString().trim()).matches()) {
            etMail.error = "Introduce un email válido"
            return false
        }
        if (etBirthDate.text.toString().trim().isEmpty()) {
            etBirthDate.error = "La fecha de nacimiento es requerida"
            return false
        }
        if (etPassword.text.toString().trim().length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (etPassword.text.toString().trim() != etConfirmPassword.text.toString().trim()) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
}
