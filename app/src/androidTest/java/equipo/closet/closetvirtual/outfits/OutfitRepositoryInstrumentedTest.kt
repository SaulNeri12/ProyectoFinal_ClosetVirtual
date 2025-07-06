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
                        "userId" to outfit.userId
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
            //db.collection("outfits").document(outfit.id).delete().await()
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
                        "tags" to outfit.tags,
                        "userId" to outfit.userId
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