package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import equipo.closet.closetvirtual.databinding.ActivityForgotPasswordBinding
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendEmail()

    }


    private fun validateEmailInput(): Boolean{
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Porfavor escriba su correo"
            return false
        }
        //check if the email contains @gmail and .
        if (!email.contains("@gmail.com")) {
            binding.etEmail.error = "El correo debe contener @gmail.com"
            return false
        }
        //Default case
        return true
    }


    fun sendEmail() {
        binding.btnSendEmail.setOnClickListener {
            if (validateEmailInput()) {
                lifecycleScope.launch {
                    try {
                        val userEmail = binding.etEmail.text.toString()
                        userEmail?.let { userRepository.sendPasswordResetMail(it) }
                        Toast.makeText(this@ForgotPasswordActivity, "Correo enviado", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: AuthException) {
                        Toast.makeText(this@ForgotPasswordActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}