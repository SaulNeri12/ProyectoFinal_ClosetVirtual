package equipo.closet.closetvirtual.outfits

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class OutfitRepositoryInstrumentedTest {

    private lateinit var repository: Repository<Outfit, String>
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var auth: FirebaseAuth

    @Before
    fun setup() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        runBlocking {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d("TEST", "Signed in anonymously for testing")
            }
            currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("No se pudo obtener el UID del usuario autenticado.")

            repository = OutfitRepositoryFactory.create()
        }
    }

    @Test(timeout = 10_000)
    fun getAll_WithFilters_shouldReturnListOfOutfitsWithTagsFilter() {

        runBlocking {

            // Arrange
            val target = "Outfit Test"

            val outfit = Outfit(
                id = UUID.randomUUID().toString(),
                name = target,
                tags = mutableListOf("Casual", "Sports"),
                userId = currentUserId
            )

            val garment = Garment(
                id = UUID.randomUUID().toString(),
                name = "Pantalón Azul",
                color = "Azul",
                category = "bottom",
                userId = currentUserId
            ).apply {
                addTag("casual")
            }

            outfit.addGarment(garment)

            db.collection("outfits")
                .document(outfit.id)
                .set(
                    mapOf(
                        "id" to outfit.id,
                        "name" to outfit.name,
                        "tags" to outfit.tags,
                        "userId" to outfit.userId,
                        "nameLowerCase" to outfit.name.lowercase(),
                        "clothesIds" to outfit.clothesIds
                    )
                )
                .await()

            // Act
            val result = repository.getAll(
                mapOf(
                    "tags" to listOf("Sports")
                )
            )

            println("Resultado: ${result}")

            // Assert
            assertTrue(result.size == 1)
            assertTrue(result.any { it.name == target })
            assertTrue(result.any { it.tags.contains("Sports") })

            // Cleanup
            db.collection("outfits").document(outfit.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldFilterByNamePrefix() {

        runBlocking {

            // Arrange
            val outfit1 = Outfit(
                id = UUID.randomUUID().toString(),
                name = "Outfit Casual",
                tags = mutableListOf("casual"),
                userId = currentUserId
            )

            val outfit2 = Outfit(
                id = UUID.randomUUID().toString(),
                name = "Outfit Deportivo",
                tags = mutableListOf("sports"),
                userId = currentUserId
            )

            val outfit3 = Outfit(
                id = UUID.randomUUID().toString(),
                name = "Formal Look",
                tags = mutableListOf("formal"),
                userId = currentUserId
            )

            db.collection("outfits").document(outfit1.id).set(
                mapOf(
                    "id" to outfit1.id,
                    "name" to outfit1.name,
                    "nameLowerCase" to outfit1.name.lowercase(),
                    "tags" to outfit1.tags,
                    "userId" to outfit1.userId,
                    "clothesIds" to outfit1.clothesIds
                )
            ).await()

            db.collection("outfits").document(outfit2.id).set(
                mapOf(
                    "id" to outfit2.id,
                    "name" to outfit2.name,
                    "nameLowerCase" to outfit2.name.lowercase(),
                    "tags" to outfit2.tags,
                    "userId" to outfit2.userId,
                    "clothesIds" to outfit1.clothesIds
                )
            ).await()

            db.collection("outfits").document(outfit3.id).set(
                mapOf(
                    "id" to outfit3.id,
                    "name" to outfit3.name,
                    "nameLowerCase" to outfit3.name.lowercase(),
                    "tags" to outfit3.tags,
                    "userId" to outfit3.userId,
                    "clothesIds" to outfit1.clothesIds
                )
            ).await()

            // Act
            val filter = mapOf("name" to "outfit")
            val result = repository.getAll(filter)

            println("Resultado búsqueda por nombre: $result")

            // Assert
            assertTrue(result.any { it.name == "Outfit Casual" })
            assertTrue(result.any { it.name == "Outfit Deportivo" })
            assertFalse(result.any { it.name == "Formal Look" })

            // Cleanup
            db.collection("outfits").document(outfit1.id).delete().await()
            db.collection("outfits").document(outfit2.id).delete().await()
            db.collection("outfits").document(outfit3.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldReturnListOfOutfits() {

        runBlocking {

            // Arrange
            val target = "Outfit Test"

            val outfit = Outfit(
                id = UUID.randomUUID().toString(),
                name = target,
                tags = mutableListOf("Casual", "Sports"),
                userId = currentUserId
            )

            val garment = Garment(
                id = UUID.randomUUID().toString(),
                name = "Pantalón Azul",
                color = "Azul",
                category = "bottom",
                userId = currentUserId
            ).apply {
                addTag("casual")
            }

            outfit.addGarment(garment)

            db.collection("outfits")
                .document(outfit.id)
                .set(
                    mapOf(
                        "id" to outfit.id,
                        "name" to outfit.name,
                        "nameLowerCase" to outfit.name.lowercase(),
                        "tags" to outfit.tags,
                        "userId" to outfit.userId,
                        "clothesIds" to outfit.clothesIds
                    )
                )
                .await()

            // Act
            val result = repository.getAll()

            println("Resultado: ${result}")

            // Assert
            assertTrue(result.any { it.name == target })

            // Cleanup
            db.collection("outfits").document(outfit.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun insert_shouldReturnSameOutfit() {

        runBlocking {

            // Arrange
            val outfit = Outfit(
                name = "Test Outfit",
                clothesIds = mutableListOf("id1", "id2"),
                tags = mutableListOf("casual", "summer"),
                userId = currentUserId
            )

            // Act
            val id = repository.insert(outfit)
            val fetched = repository.getById(id)

            // Assert
            assertNotNull(fetched)
            assertEquals(outfit.name, fetched!!.name)
            assertEquals(outfit.tags.toSet(), fetched.tags.toSet())
            assertEquals(outfit.clothesIds.toSet(), fetched.clothesIds.toSet())

            // Cleanup
            db.collection("outfits").document(id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun update_shouldModifyOutfit() {

        runBlocking {
            // Arrange
            val outfit = Outfit(
                name = "To be Updated",
                clothesIds = mutableListOf("id1"),
                tags = mutableListOf("old"),
                userId = currentUserId
            )

            // Act
            val id = repository.insert(outfit)

            val updatedOutfit = outfit.copy(
                id = id,
                name = "Updated Name",
                tags = mutableListOf("new"),
            )

            repository.update(updatedOutfit)

            val result = repository.getByName("Updated Name")

            // Assert
            assertNotNull(result)
            assertEquals("Updated Name", result!!.name)
            assertTrue(result.tags.contains("new"))

            // Cleanup
            db.collection("outfits").document(id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun delete_shouldRemoveOutfit() {

        runBlocking {
            // Arrange
            val outfit = Outfit(
                name = "To be Deleted",
                userId = currentUserId
            )

            // Act
            val id = repository.insert(outfit)
            val deletedId = repository.delete(id)
            val shouldBeNull = repository.getById(id)

            // Assert
            assertEquals(id, deletedId)
            assertNull(shouldBeNull)
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldReturnOutfits() {

        runBlocking {
            // Arrange
            val name = "Multiple Fetch"
            val outfit = Outfit(
                name = name,
                tags = mutableListOf("casual"),
                clothesIds = mutableListOf("id1"),
                userId = currentUserId
            )

            // Act
            val id = repository.insert(outfit)
            val all = repository.getAll()

            // Assert
            assertTrue(all.any { it.name == name })

            // Cleanup
            db.collection("outfits").document(id).delete().await()
        }
    }
}