package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.exceptions.SearchException
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.objects.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID


object FirebaseGarmentRepository : Repository<Garment, String> {

    private val CLOTHES_COLLECTION_NAME = "clothes"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun getAll(): List<Garment> {
        val db = FirebaseFirestore.getInstance()

        return try {

            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

            val snapshot = db.collection(CLOTHES_COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // converts documents fetched into "POJO"s
            return snapshot.documents.mapNotNull { doc ->
                doc.toObject(Garment::class.java)?.apply {
                    id = doc.id
                }
            }

        } catch (e: Exception) {
            throw SearchException("Error al cargar resultados, intentelo de nuevo más tarde.")
        }
    }


    override suspend fun getAll(filters: Map<String, Any>): List<Garment> {
        val db = FirebaseFirestore.getInstance()

        return try {

            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

            var query = db.collection(CLOTHES_COLLECTION_NAME).whereEqualTo("userId", userId)

            query = applyTagFilters(query, filters["tag"])

            val snapshot: QuerySnapshot = query.get().await()

            // converts documents fetched into "POJO"s
            return snapshot.documents.mapNotNull { doc ->
                doc.toObject(Garment::class.java)?.apply {
                    id = doc.id
                }
            }

        } catch (e: Exception) {
            //throw e
            throw SearchException("Error al cargar resultados, intentelo de nuevo más tarde.")
        }
    }

    private fun applyTagFilters(query: Query, tag: Any?): Query {

        if (tag is String) {
            return query.whereEqualTo("tag", tag.toString())
        }

        return query
    }

    override suspend fun getByName(name: String): Garment? {
        val db = FirebaseFirestore.getInstance()

        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

            val snapshot = db.collection(CLOTHES_COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("name", name)
                .get()
                .await()

            val doc = snapshot.documents.firstOrNull()

            // converts result from Object to Garment object instance.
            doc?.toObject(Garment::class.java)?.apply {
                id = doc.id
            }

        } catch (e: Exception) {
            throw SearchException("Error al cargar resultados, intentelo de nuevo más tarde.")
        }
    }

    override suspend fun insert(item: Garment): String {
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

        val id = UUID.randomUUID().toString()
        val garment = item.copy(id = id, userId = userId)

        return try {
            db.collection(CLOTHES_COLLECTION_NAME)
                .document(id)
                .set(garment)
                .await()

            return id
        } catch (e: Exception) {
            throw Exception("No se pudo insertar la prenda, intentelo de nuevo más tarde.")
        }
    }


    override suspend fun update(item: Garment): String {
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

        var garmentId = item.id
        if (garmentId.isBlank()) {
            garmentId = UUID.randomUUID().toString()
        }

        val updatedGarment = item.copy(userId = userId)

        return try {
            db.collection(CLOTHES_COLLECTION_NAME)
                .document(garmentId)
                .set(updatedGarment)
                .await()

            return garmentId
        } catch (e: Exception) {
            throw Exception("No se pudo actualizar la prenda, intentelo de nuevo más tarde.")
        }
    }


    override suspend fun delete(id: String): String {
        val db = FirebaseFirestore.getInstance()

        return try {
            db.collection(CLOTHES_COLLECTION_NAME)
                .document(id)
                .delete()
                .await()

            id
        } catch (e: Exception) {
            throw Exception("No se pudo eliminar la prenda debido a un error, intentelo de nuevo más tarde.")
        }
    }

    override suspend fun getById(id: String): Garment? {
        val db = FirebaseFirestore.getInstance()

        return try {
            val doc = db.collection(CLOTHES_COLLECTION_NAME)
                .document(id)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(Garment::class.java)?.apply { this.id = doc.id }
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("No se pudo encontrar la prenda debido a un error, intentelo de nuevo más tarde.")
        }
    }
}