package equipo.closet.closetvirtual.global

import android.content.Context
import android.content.Intent
import equipo.closet.closetvirtual.MainActivity

object NavigationHelper {

    const val EXTRA_NAV_DESTINATION = "navigateTo"

    // Destinations
    const val DEST_GARMENT_REGISTRY = "garment_registry"

    /**
     * Opens the MainActivity with the specified destination.
     */
    fun openMainActivityAt(context: Context, destination: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(EXTRA_NAV_DESTINATION, destination)
        context.startActivity(intent)
    }
}
