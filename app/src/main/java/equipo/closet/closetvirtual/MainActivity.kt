package equipo.closet.closetvirtual

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository

class MainActivity : AppCompatActivity() {

    val clothes: Repository<Garment, Int> = GarmentRepositoryFactory.create()
    val outfits: Repository<Outfit, Int> = OutfitRepositoryFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        // adding some clothes...
        this.clothes.insert(Garment(0, "Maquina Azul", "Azul", "Formal", "Top", false))
        this.clothes.insert(Garment(0, "Camisa Blanca", "Blanco", "Casual", "Top", false))
        this.clothes.insert(Garment(0, "Pantalon Versachi", "Negro", "Elegante", "Bottom", false))
        this.clothes.insert(Garment(0, "Chaqueta Negra", "Negro", "Urbano", "Top", false))
        this.clothes.insert(Garment(0, "Zapatos De Guchi", "Negro", "Elegante", "Zapatos", false))

        // adding an outfit
        val outfitGala = Outfit(name="Traje de Gala")
        outfitGala.addGarment(this.clothes.getAll().first())
        outfitGala.addGarment(this.clothes.getAll().last())
        outfitGala.addGarment(this.clothes.getAll().get(2))

        this.outfits.insert(outfitGala)

        navView.setupWithNavController(navController)
    }
}