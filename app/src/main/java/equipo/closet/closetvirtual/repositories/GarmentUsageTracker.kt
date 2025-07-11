package equipo.closet.closetvirtual.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID

object GarmentUsageTracker : IGarmentUsageTracker {

    private const val GARMENT_USAGE_COLLECTION_NAME = "garment_usage"
    // La instancia de Auth ya no es necesaria aquí, pero la dejamos por si otros métodos la usan.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun countGarmentUsages(garmentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        try {
            val snapshot = db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .whereEqualTo("garmentId", garmentId)
                .get()
                .await()
            return snapshot.size()
        } catch (e: Exception) {
            throw Exception("Error al contar los usos totales de la prenda.")
        }
    }

    override suspend fun countGarmentUsageMonthly(garmentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        try {
            val startCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val snapshot = db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .whereEqualTo("garmentId", garmentId)
                .whereGreaterThanOrEqualTo("date", startCal.time)
                .whereLessThanOrEqualTo("date", endCal.time)
                .get()
                .await()
            return snapshot.size()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    // CAMBIO: Este método ya no guarda el userId
    override suspend fun registerUsage(garmentId: String): String {
        val db = FirebaseFirestore.getInstance()
        try {
            val id = UUID.randomUUID().toString()
            val currentDate = Calendar.getInstance().time

            db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .document(id)
                .set(
                    mapOf(
                        "id" to id,
                        "date" to currentDate,
                        "garmentId" to garmentId
                    )
                ).await()

            return id
        } catch (e: Exception) {
            throw Exception("Error al registrar el uso, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun deleteUsage(garmentId: String): String {
        val db = FirebaseFirestore.getInstance()
        try {
            val snapshot = db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .whereEqualTo("garmentId", garmentId)
                .get()
                .await()
            for (document in snapshot.documents) {
                db.collection(GARMENT_USAGE_COLLECTION_NAME)
                    .document(document.id)
                    .delete()
                    .await()
            }
            return garmentId
        } catch (e: Exception) {
            throw Exception("Error al eliminar registros de uso, inténtelo de nuevo más tarde.")
        }
    }

    // CAMBIO: Este método ahora trae todos los usos de una fecha, sin filtrar por usuario
    suspend fun getUsagesForDate(date: java.util.Date): List<String> {
        val db = FirebaseFirestore.getInstance()

        val startOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        try {
            val snapshot = db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThanOrEqualTo("date", endOfDay)
                .get()
                .await()

            return snapshot.documents.mapNotNull { it.getString("garmentId") }
        } catch (e: Exception) {
            throw Exception("Error al obtener los usos para la fecha seleccionada.")
        }
    }
}