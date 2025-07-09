package equipo.closet.closetvirtual.users

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.User
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseUserRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class UserRepositoryInstrumentedTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var repository: Repository<User, String>

    @Before
    fun setup() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        repository = FirebaseUserRepository
    }

    @Test(timeout = 10_000)
    fun update_shouldModifyUserFieldsCorrectly() {
        runBlocking {
            // Arrange
            val userId = UUID.randomUUID().toString()
            val originalUser = User(
                uid = userId,
                name = "Nombre Original",
                email = "correo@example.com",
                birthdate = Date(),
                gender = "Masculino",
                password = "originalpass",
                profileImgUrl = "http://url.com/original.png",
                fireAuthUID = userId
            )

            db.collection("users")
                .document(userId)
                .set(originalUser)
                .await()

            val updatedUser = originalUser.copy(
                name = "Nombre Actualizado",
                gender = "Femenino",
                profileImgUrl = "http://url.com/nueva.png"
            )

            // Act
            val resultId = repository.update(updatedUser)

            // Assert
            val snapshot = db.collection("users").document(resultId).get().await()

            assertEquals("Nombre Actualizado", snapshot.getString("name"))
            assertEquals("Femenino", snapshot.getString("gender"))
            assertEquals("http://url.com/nueva.png", snapshot.getString("profileImgUrl"))

            // Cleanup
            db.collection("users")
                .document(userId)
                .delete()
                .await()
        }
    }
}