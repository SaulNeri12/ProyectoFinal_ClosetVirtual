package equipo.closet.closetvirtual

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFilter: MaterialButton = findViewById(R.id.btn_filter)

        btnFilter.setOnClickListener {
            Toast.makeText(
                this,
                "Filter button click!!!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
