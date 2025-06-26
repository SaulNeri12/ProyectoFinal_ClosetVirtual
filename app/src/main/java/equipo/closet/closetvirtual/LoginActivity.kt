package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initialize the variables
        tvRegister = findViewById(R.id.tvRegister)
        btnLogin = findViewById(R.id.btnLogin)

        //initialize the input fields
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        //declare the function login
        login();
        register();

    }

    private fun login() {
        btnLogin.setOnClickListener {
            if (validateInputFields()) {
                //start the main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun register() {
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun validateInputFields(): Boolean{
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }

        //Default case
        return true
    }

}