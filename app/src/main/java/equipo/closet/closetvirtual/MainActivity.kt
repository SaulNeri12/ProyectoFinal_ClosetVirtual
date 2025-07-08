package equipo.closet.closetvirtual

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.global.NavigationHelper
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.UserRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    val clothes: Repository<Garment, String> = GarmentRepositoryFactory.create()
    val outfits: Repository<Outfit, String> = OutfitRepositoryFactory.create()
    //val userRepository: UserRepository = UserRepositoryFactory.create()

    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: TEST
        //Toast.makeText(this, "Email session: ${SessionManager.user.email}, Name: ${SessionManager.user.name}", Toast.LENGTH_LONG).show()

        Toast.makeText(
            this,
            "¡Bienvenido, ${SessionManager.user.name}!",
            Toast.LENGTH_SHORT
        ).show()

        /*
        lifecycleScope.launch {
            try {
                val userEmail = FirebaseAuth.getInstance().currentUser?.email
                userEmail?.let { userRepository.sendPasswordResetMail(it) }
            } catch (e: AuthException) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        // Handle navigation based on the destination
        if (savedInstanceState == null) {
            intent.getStringExtra(NavigationHelper.EXTRA_NAV_DESTINATION)?.let { destination ->
                when (destination) {
                    NavigationHelper.DEST_GARMENT_REGISTRY -> navController.navigate(R.id.navigation_new_clothes)
                }
            }
        }

        navView.setupWithNavController(navController)
    }
}