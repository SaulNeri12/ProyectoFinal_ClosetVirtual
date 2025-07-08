package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import equipo.closet.closetvirtual.databinding.ActivityChangePasswordBinding
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    private val userRepository: UserRepository = UserRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    private fun handleConfirmPasswordChange(){
        binding.btnConfirmPasswordChange.setOnClickListener {
            if (validatePasswordInput()) {
                val newPassword = binding.etNewPassword.text.toString()
                val mail = intent.getStringExtra("mail")

                //update the password in the database


                //go to login activity
                val intet = Intent(this, LoginActivity::class.java)
                startActivity(intet)
                finish()
            }
        }
    }

    private fun validatePasswordInput(): Boolean{
        val password = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (password.isEmpty()) {
            binding.etNewPassword.error = "Password is required"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Confirm password is required"
            return false
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

}