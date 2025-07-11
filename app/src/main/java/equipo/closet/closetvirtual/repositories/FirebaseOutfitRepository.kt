package equipo.closet.closetvirtual.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.exceptions.SearchException
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.tasks.await
import java.util.UUID


class FirebaseOutfitRepository: Repository<Outfit, String> {

    private lateinit var garmentRepository: Repository<Garment, String>
    private val garmentCacheList: MutableList<Garment> = mutableListOf()

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val OUTFIT_COLLECTION_NAME = "outfits"

    constructor(garmentRepository: Repository<Garment, String>) {
        this.garmentRepository = garmentRepository
    }

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
    }

    override suspend fun getAll(): List<Outfit> {
        return getAll(emptyMap())
    }

    override suspend fun getAll(filters: Map<String, Any>): List<Outfit> {
        val db = FirebaseFirestore.getInstance()

        return try {
            val userId = getUserId()

            var query: Query = db.collection(OUTFIT_COLLECTION_NAME).whereEqualTo("userId", userId)

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

            /*
            filters.forEach { (key, value) ->
                if (key != "tags") query = query.whereEqualTo(key, value)
            }*/

            val snapshot: QuerySnapshot = query.get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Outfit::class.java)?.apply {
                    id = doc.id
                }
            }
        } catch (e: Exception) {
            throw e.message?.let { SearchException(it) }!!
            //throw SearchException("Error al cargar resultados, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun getByName(name: String): Outfit? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val userId = getUserId()
            val snapshot = db.collection(OUTFIT_COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("name", name)
                .get()
                .await()
            val doc = snapshot.documents.firstOrNull()
            doc?.toObject(Outfit::class.java)?.apply {
                id = doc.id
            }
        } catch (e: Exception) {
            throw SearchException("Error al cargar resultados, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun delete(id: String): String {
        val db = FirebaseFirestore.getInstance()
        return try {

            db.collection(OUTFIT_COLLECTION_NAME)
                .document(id)
                .delete()
                .await()

            id
        } catch (e: Exception) {
            throw SearchException("Error al eliminar el outfit, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun update(item: Outfit): String {
        val db = FirebaseFirestore.getInstance()

        return try {
            db.collection(OUTFIT_COLLECTION_NAME)
                .document(item.id)
                .update(
                    mapOf(
                        "name" to item.name,
                        "clothesIds" to item.clothesIds,
                        "tags" to item.tags,
                        "nameLowerCase" to item.nameLowerCase
                    )
                )
                .await()

            item.id
        } catch (e: Exception) {
            throw Exception("No se pudo actualizar la informacion del outfit, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun insert(item: Outfit): String {
        val db = FirebaseFirestore.getInstance()
        val userId = getUserId()
        val id = UUID.randomUUID().toString()

        return try {
            db.collection(OUTFIT_COLLECTION_NAME)
                .document(id)
                .set(
                    mapOf(
                        "id" to id,
                        "nameLowerCase" to item.name.lowercase(),
                        "name" to item.name,
                        "clothesIds" to item.clothesIds,
                        "tags" to item.tags,
                        "userId" to userId
                    )
                )
                .await()
            id
        } catch (e: Exception) {
            throw Exception("No se pudo registrar el outfit, inténtelo de nuevo más tarde.")
        }
    }

    override suspend fun getById(id: String): Outfit? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val doc = db.collection(OUTFIT_COLLECTION_NAME)
                .document(id)
                .get()
                .await()
            if (doc.exists()) {
                doc.toObject(Outfit::class.java)?.apply { this.id = doc.id }
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("No se pudo encontrar el outfit debido a un error, inténtelo de nuevo más tarde.")
        }
    }
}