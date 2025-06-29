package equipo.closet.closetvirtual

import android.app.Application
import android.widget.Toast
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)


        Toast.makeText(this, "Se inicializo firebase", Toast.LENGTH_LONG).show()
    }
}