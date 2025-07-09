package equipo.closet.closetvirtual.clothes

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FirebaseGarmentRepositoryInstrumentedTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var repository: Repository<Garment, String>

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

            repository = FirebaseGarmentRepository
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldReturnListOfGarments() {

        runBlocking {

            // Arrange
            val garment = Garment(
                id = UUID.randomUUID().toString(),
                name = "Camisa Roja",
                color = "Rojo",
                category = "top",
                userId = currentUserId
            )

            db.collection("clothes").document(garment.id).set(garment).await()

            // Act
            val result = repository.getAll()

            println("Resultado: ${result}")

            // Assert
            assertTrue(result.any { it.name == "Camisa Roja" })

            // Cleanup
            db.collection("clothes").document(garment.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldFilterByTag() {

        runBlocking {

            // Arrange
            val garment = Garment(
                id = UUID.randomUUID().toString(),
                name = "Pantalón Azul",
                color = "Azul",
                category = "bottom",
                userId = currentUserId
            ).apply {
                addTag("casual")
            }

            val garment2 = Garment(
                id = UUID.randomUUID().toString(),
                name = "Pantalón Verde",
                color = "Azul",
                category = "bottom",
                userId = currentUserId
            ).apply {
                addTag("sport")
            }

            db.collection("clothes").document(garment.id).set(garment).await()
            db.collection("clothes").document(garment2.id).set(garment2).await()

            // Act
            val result = repository.getAll(mapOf("tags" to listOf("casual")))

            println("Resultado: ${result}")

            // Assert
            assertTrue(result.any { it.name == "Pantalón Azul" })
            assertFalse(result.any { it.name == "Pantalón Verde" })


            // Cleanup
            db.collection("clothes").document(garment.id).delete().await()
            db.collection("clothes").document(garment2.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun getAll_shouldFilterByNamePrefix() {
        runBlocking {

            // Arrange
            val garment1 = Garment(
                id = UUID.randomUUID().toString(),
                name = "Camisa Blanca",
                nameLowerCase = "camisa blanca",
                color = "Blanco",
                category = "top",
                userId = currentUserId
            )

            val garment2 = Garment(
                id = UUID.randomUUID().toString(),
                name = "Camiseta Negra",
                nameLowerCase = "camiseta negra",
                color = "Negro",
                category = "top",
                userId = currentUserId
            )

            val garment3 = Garment(
                id = UUID.randomUUID().toString(),
                name = "Pantaln Verde",
                nameLowerCase = "pantalón verde",
                color = "Verde",
                category = "bottom",
                userId = currentUserId
            )

            db.collection("clothes").document(garment1.id).set(garment1).await()
            db.collection("clothes").document(garment2.id).set(garment2).await()
            db.collection("clothes").document(garment3.id).set(garment3).await()

            // Act
            val result = repository.getAll(mapOf("name" to "Cam"))

            println("Resultado: $result")

            // Assert
            assertTrue(result.any { it.name == "Camisa Blanca" })
            assertTrue(result.any { it.name == "Camiseta Negra" })
            assertFalse(result.any { it.name == "Pantalón Verde" })

            // Cleanup
            db.collection("clothes").document(garment1.id).delete().await()
            db.collection("clothes").document(garment2.id).delete().await()
            db.collection("clothes").document(garment3.id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun insert_shouldSaveGarmentAndGetById() {

        runBlocking {

            // Arrange
            val garment = Garment(
                name = "Camisa Blanca",
                color = "Blanco",
                category = "top",
                userId = currentUserId,
                tags = mutableListOf("formal")
            )

            // Act
            val id = repository.insert(garment)
            val retrieved = repository.getById(id)

            // Assert
            assertNotNull(retrieved)
            assertEquals("Camisa Blanca", retrieved?.name)

            // Cleanup
            db.collection("clothes").document(id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun getByName_shouldReturnGarmentIfExists() {

        runBlocking {

            // Arrange
            val garment = Garment(
                name = "Falda Negra",
                color = "Negro",
                category = "bottom",
                userId = currentUserId,
                tags = mutableListOf("elegante")
            )

            // Act
            val id = repository.insert(garment)
            val found = repository.getByName("Falda Negra")

            // Assert
            assertNotNull(found)
            assertEquals("Falda Negra", found?.name)

            // Cleanup
            db.collection("clothes").document(id).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun update_shouldModifyExistingGarment() {

        runBlocking {

            // Arrange
            val garment = Garment(
                name = "Chamarra Roja",
                color = "Rojo",
                category = "outer",
                userId = currentUserId,
                tags = mutableListOf("invierno")
            )

            // Act
            val id = repository.insert(garment)

            val updated = garment.copy(id = id, name = "Chamarra Negra", color = "negro")
            val updatedId = repository.update(updated)

            val result = repository.getById(updatedId)

            // Assert
            assertEquals("Chamarra Negra", result?.name)
            assertEquals(id, updatedId)

            // Cleanup
            db.collection("clothes").document(id).delete().await()
        }
    }



    @Test(timeout = 10_000)
    fun delete_shouldRemoveGarment() {

        runBlocking {

            // Arrange
            val garment = Garment(
                name = "Polo Azul",
                color = "Azul",
                category = "top",
                userId = currentUserId,
                tags = mutableListOf("casual")
            )

            // Act
            val id = repository.insert(garment)
            val deletedId = repository.delete(id)
            val result = repository.getById(deletedId)

            // Assert
            assertNull(result)
        }
    }



    @Test(timeout = 10_000)
    fun getById_shouldReturnNullIfNotExists() {
        runBlocking {
            // Arrange
            var result: Garment?

            // Act
            result = repository.getById("id_inexistente")

            // Assert
            assertNull(result)
        }
    }
}
