package equipo.closet.closetvirtual.clothes

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
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

    @Before
    fun setup() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Puedes hacer login anónimo si no estás logueado
        runBlocking {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d("TEST", "Signed in anonymously for testing")
            }
            currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("No se pudo obtener el UID del usuario autenticado.")
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
            val result = FirebaseGarmentRepository.getAll()

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
            val result = FirebaseGarmentRepository.getAll(mapOf("tags" to listOf("casual")))

            println("Resultado: ${result}")

            // Assert
            assertTrue(result.any { it.name == "Pantalón Azul" })
            assertFalse(result.any { it.name == "Pantalón Verde" })


            // Cleanup
            db.collection("clothes").document(garment.id).delete().await()
            db.collection("clothes").document(garment2.id).delete().await()
        }
    }
}
