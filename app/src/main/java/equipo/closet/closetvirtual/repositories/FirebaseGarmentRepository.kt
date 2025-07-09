package equipo.closet.closetvirtual.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.exceptions.SearchException
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseGarmentRepository : Repository<Garment, String> {

    private const val CLOTHES_COLLECTION_NAME = "clothes"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun getAll(): List<Garment> {
        return getAll(emptyMap())
    }

    override suspend fun getAll(filters: Map<String, Any>): List<Garment> {
        val db = FirebaseFirestore.getInstance()
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

            var query: Query = db.collection(CLOTHES_COLLECTION_NAME).whereEqualTo("userId", userId)

            /*
            filters.forEach { (key, value) ->
                query = query.whereEqualTo(key, value)
            }*/

            val tagsFilter = filters["tags"]
            if (tagsFilter is List<*>) {
                val tagsList = tagsFilter.filterIsInstance<String>()
                if (tagsList.isNotEmpty()) {
                    query = query.whereArrayContainsAny("tags", tagsList)
                }
            }

            val nameFilter = filters["name"]
            if (nameFilter is String && nameFilter.isNotBlank()) {
                val capitalized = nameFilter.lowercase()
                query = query
                    .orderBy("nameLowerCase")
                    .startAt(capitalized)
                    .endAt(capitalized + '\uf8ff')
            }

            val snapshot: QuerySnapshot = query.get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Garment::class.java)?.apply {
                    id = doc.id
                }
            }
        } catch (e: Exception) {
            //throw e.message?.let { SearchException(it) }!!
            throw SearchException("Error al cargar resultados, inténtelo de nuevo más tarde.")
        }
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
            doc?.toObject(Garment::class.java)?.apply {
                id = doc.id
            }
        } catch (e: Exception) {
            throw SearchException("Error al cargar resultados, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun insert(item: Garment): String {
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        val id = UUID.randomUUID().toString()
        val garment = item.copy(id = id, userId = userId, nameLowerCase = item.name.lowercase())
        return try {
            db.collection(CLOTHES_COLLECTION_NAME)
                .document(id)
                .set(
                    mapOf(
                        "name" to garment.name,
                        "nameLowerCase" to garment.nameLowerCase,
                        "color" to garment.color,
                        "category" to garment.category,
                        "tags" to garment.tags,
                        "print" to garment.print,
                        "imageUri" to garment.imageUri,
                        "userId" to garment.userId
                    )
                )
                .await()
            id
        } catch (e: Exception) {
            throw Exception("No se pudo insertar la prenda, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun update(item: Garment): String {
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        var garmentId = item.id
        if (garmentId.isBlank()) {
            garmentId = UUID.randomUUID().toString()
        }
        val updatedGarment = item.copy(userId = userId, nameLowerCase = item.name.lowercase())
        return try {
            db.collection(CLOTHES_COLLECTION_NAME)
                .document(garmentId)
                .update(
                    "name", updatedGarment.name,
                    "nameLowerCase", updatedGarment.nameLowerCase,
                    "color", updatedGarment.color,
                    "category", updatedGarment.category,
                    "tags", updatedGarment.tags,
                    "print", updatedGarment.print,
                    "imageUri", updatedGarment.imageUri
                )
                .await()
            garmentId
        } catch (e: Exception) {
            throw Exception("No se pudo actualizar la prenda, inténtelo de nuevo más tarde.")
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
            throw Exception("No se pudo eliminar la prenda debido a un error, inténtelo de nuevo más tarde.")
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
            throw Exception("No se pudo encontrar la prenda debido a un error, inténtelo de nuevo más tarde.")
        }
    }
}