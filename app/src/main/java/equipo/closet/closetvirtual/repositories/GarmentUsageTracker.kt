package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID

object GarmentUsageTracker : IGarmentUsageTracker {

    private val GARMENT_USAGE_COLLECTION_NAME = "garment_usage"

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
            // inicio del mes (1ro a las 00:00:00)
            val startCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // fin del mes (último día a las 23:59:59)
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
            //throw Exception("Error al contar usos mensuales de la prenda.")
        }
    }


    override suspend fun registerUsage(garmentId: String): String {
        val db = FirebaseFirestore.getInstance()

        try {
            val id = UUID.randomUUID().toString()

            val calendar = Calendar.getInstance()
            val currentDate = calendar.time

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
}