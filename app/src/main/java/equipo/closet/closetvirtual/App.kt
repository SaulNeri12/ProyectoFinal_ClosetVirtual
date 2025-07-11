package equipo.closet.closetvirtual

import android.app.Application
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp

class App : Application() {

    val CLOUD_NAME = "dv16basxw"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-preset"

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val config = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)

        Toast.makeText(this, "Se inicializo firebase", Toast.LENGTH_LONG).show()
    }
}