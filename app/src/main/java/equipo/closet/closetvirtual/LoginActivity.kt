package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.databinding.ActivityLoginBinding
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //declare the function login
        login();
        register();

    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            if (validateInputFields()) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                lifecycleScope.launch {
                    try {
                        val user = userRepository.login(email, password)

                        SessionManager.user = user;

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } catch (e: AuthException) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun register() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun validateInputFields(): Boolean{
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }

        //Default case
        return true
    }

}