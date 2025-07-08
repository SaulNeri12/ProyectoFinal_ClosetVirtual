package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import equipo.closet.closetvirtual.databinding.ActivityForgotPasswordBinding
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private var sendCode: String? = ""
    private var receivedCode: String? = ""

    private lateinit var digit1: String
    private lateinit var digit2: String
    private lateinit var digit3: String
    private lateinit var digit4: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        digit1 = binding.etDigit1.text.toString()
        digit2 = binding.etDigit2.text.toString()
        digit3 = binding.etDigit3.text.toString()
        digit4 = binding.etDigit4.text.toString()

        initialConfig()
        handleSendCodeByEmail()
        setupOTPInputs()
        handleConfirmCode()
    }

    private fun initialConfig() {
        binding.etEmail.requestFocus()
        binding.codeContainer.visibility = View.GONE
    }

    private fun validateEmailInput(): Boolean{
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        //check if the email contains @gmail and .
        if (!email.contains("@gmail.com")) {
            binding.etEmail.error = "Email must contain @gmail.com"
            Toast.makeText(this, "Email must contain @gmail.com", Toast.LENGTH_SHORT).show()
            return false
        }
        //Default case
        return true
    }

    private fun handleSendCodeByEmail() {
        binding.btnSendCode.setOnClickListener {
            if (validateEmailInput()) {
                val email = binding.etEmail.text.toString()
                sendCode = generateCode()
                sendEmail(sendCode!!, email)
                Toast.makeText(this, "Codigo enviado a $email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendEmail(code: String, mail: String) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("skevinjaredfigueroa@gmail.com",
                    "rlqu vumt wfuj kasz")
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("skevinjaredfigueroa@gmail.com"))
                setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mail)
                )
                subject = "Codigo de confirmacion"
                setText("Su codigo de confirmacion es el siguiente: $code")
            }

            Thread {
                try {
                    Transport.send(message)
                    runOnUiThread {
                        onMailSentSuccess()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        onMailSentError()
                    }
                }
            }.start()

        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun generateCode() : String{
        val code = (1000..9999).random()
        return code.toString()
    }

    private fun onMailSentSuccess() {
        binding.codeContainer.visibility = View.VISIBLE
        binding.etDigit1.requestFocus()
    }

    private fun onMailSentError() {
        binding.codeContainer.visibility = View.GONE
        Toast.makeText(this, "Error al enviar el codigo", Toast.LENGTH_SHORT).show()
    }

    private fun setupOTPInputs() {
        val etDigit1 = binding.etDigit1
        val etDigit2 = binding.etDigit2
        val etDigit3 = binding.etDigit3
        val etDigit4 = binding.etDigit4

        val editTexts = listOf(etDigit1, etDigit2, etDigit3, etDigit4)

        for (i in 0 until editTexts.size) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun handleConfirmCode() {
        binding.btnConfirmCode.setOnClickListener {
            if (validateCodeInput()) {

                val enteredCode = digit1 + digit2 + digit3 + digit4
                if (enteredCode == receivedCode) {
                    Toast.makeText(this, "Codigo correcto",
                        Toast.LENGTH_SHORT).show()
                    //go to the next activity
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Codigo incorrecto",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateCodeInput(): Boolean{

        val digit1 = binding.etDigit1.text.toString()
        val digit2 = binding.etDigit2.text.toString()
        val digit3 = binding.etDigit3.text.toString()
        val digit4 = binding.etDigit4.text.toString()

        if (digit1.isEmpty() || digit2.isEmpty() || digit3.isEmpty() || digit4.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos",
                Toast.LENGTH_SHORT).show()
            return false
        }

        //defoult case
        return true
    }

}