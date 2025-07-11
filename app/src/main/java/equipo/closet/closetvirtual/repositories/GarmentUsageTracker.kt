package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.GarmentUsage
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.factories.GarmentUsageTrackerFactory
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID

object GarmentUsageTracker : IGarmentUsageTracker {

    private val GARMENT_USAGE_COLLECTION_NAME = "garment_usage"

    override suspend fun getUsedClothes(userId: String): List<GarmentUsage> {
        val db = FirebaseFirestore.getInstance()

        try {
            val snapshot = db.collection(GARMENT_USAGE_COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val usedClothes = mutableListOf<GarmentUsage>()

            for (document in snapshot.documents) {
                val garmentId = document.getString("garmentId")
                val date = document.getDate("date")
                val id = document.getString("id")
                val userId = document.getString("userId")

                val usage = GarmentUsage(
                    id = id.toString(),
                    garmentId = garmentId.toString(),
                    date = date!!,
                    userId = userId.toString()
                )

                usedClothes.add(usage)
            }

            return usedClothes
        } catch (e: Exception) {
            throw Exception("No se pudo obtener la lista de prendas usadas. Inténtelo de nuevo más tarde.")
        }
    }

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


    override suspend fun registerUsage(userId: String, garmentId: String): String {
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
                        "garmentId" to garmentId,
                        "userId" to userId,
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


    /*

    suspend fun getUsagesForDate(date: java.util.Date): List<String> {
        val db = FirebaseFirestore.getInstance()

        // Configura el inicio del día (00:00:00)
        val startOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        // Configura el fin del día (23:59:59)
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

            // Devuelve una lista con los IDs de las prendas usadas ese día
            return snapshot.documents.mapNotNull { it.getString("garmentId") }
        } catch (e: Exception) {
            throw Exception("Error al obtener los usos para la fecha seleccionada.")
        }
    }*/

}