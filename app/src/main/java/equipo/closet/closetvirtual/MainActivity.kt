package equipo.closet.closetvirtual

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    val clothes: Repository<Garment, String> = GarmentRepositoryFactory.create()
    val outfits: Repository<Outfit, String> = OutfitRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: TEST
        Toast.makeText(this, "Email session: ${SessionManager.user.email}, Name: ${SessionManager.user.name}", Toast.LENGTH_LONG).show()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        /*
        // adding some clothes...
        lifecycleScope.launch {
        this.clothes.insert(Garment(UUID.randomUUID().toString(), "Maquina Azul", "Azul", "Formal", "Top", false))
        this.clothes.insert(Garment(UUID.randomUUID().toString(), "Pantalon Versachi", "Negro", "Elegante", "Bottom", false))
        this.clothes.insert(Garment(UUID.randomUUID().toString(), "Chaqueta Negra", "Negro", "Urbano", "Top", false))
        this.clothes.insert(Garment(UUID.randomUUID().toString(), "Zapatos De Guchi", "Negro", "Elegante", "Zapatos", false))

        // adding an outfit
        val outfitGala = Outfit(name="Traje de Gala")
        outfitGala.addGarment(this.clothes.getAll().first())
        outfitGala.addGarment(this.clothes.getAll().last())
        outfitGala.addGarment(this.clothes.getAll().get(2))

        this.outfits.insert(outfitGala)

         */

        navView.setupWithNavController(navController)
    }
}