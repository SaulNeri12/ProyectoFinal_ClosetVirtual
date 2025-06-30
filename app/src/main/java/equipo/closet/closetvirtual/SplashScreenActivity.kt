package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import equipo.closet.closetvirtual.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.animation.Animator
import android.animation.AnimatorListenerAdapter

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash_screen)

        val splashLogo = binding.splashLogo

        CoroutineScope(Dispatchers.Main).launch {
            // Simulate loading data
            delay(2000)

            // Fade out the splash screen
            splashLogo.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
                .start()
        }
    }
}
