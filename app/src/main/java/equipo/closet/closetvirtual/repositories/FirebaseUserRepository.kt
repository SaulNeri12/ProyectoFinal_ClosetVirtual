package equipo.closet.closetvirtual.repositories

import android.annotation.SuppressLint
import com.google.firebase.auth.EmailAuthProvider
import equipo.closet.closetvirtual.repositories.exceptions.RegistrationException
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.User
import com.google.firebase.auth.FirebaseAuth
import equipo.closet.closetvirtual.objects.SessionManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object FirebaseUserRepository : UserRepository {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val USER_COLLECTION_NAME = "users"

    fun hashSHA256(text: String): String {
        val bytes = text.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    override suspend fun login(email: String, password: String): User {

        val db = FirebaseFirestore.getInstance()

        try {
            auth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = auth.currentUser
                ?: throw AuthException("El correo o contraseña son invalidos, ingrese los datos correctos.")

            val queryResult = db.collection(USER_COLLECTION_NAME)
                .document(firebaseUser.uid)
                .get().await() ?: throw AuthException("No se pudo obtener la información necesaria del usuario.")

            if (!queryResult.exists()) {
                throw AuthException("No se encontró información del usuario en la base de datos.")
            }

            val userInfo = queryResult

            val user = User(
                uid = firebaseUser.uid,
                name = userInfo.getString("name"),
                email = firebaseUser.email ?: "",
                birthdate = userInfo.getDate("birthdate"),
                gender = userInfo.getString("gender"),
                profileImgUrl = userInfo.getString("profileImgUrl"),
                fireAuthUID = firebaseUser.uid
            )

            return user

        } catch (e: Exception) {
            throw AuthException(
                e.message ?: "Error desconocido al registrar el usuario"
            )
        }
    }

    override suspend fun signUp(user: User) {

        val db = FirebaseFirestore.getInstance()

        try {
            auth.createUserWithEmailAndPassword(user.email, user.password).await()

            val firebaseUser = auth.currentUser
                ?: throw RegistrationException("No se pudo registrar al usuario. Intente nuevamente.")

            user.fireAuthUID = firebaseUser.uid
            user.password = hashSHA256(user.password)

            val userDocRef = db.collection(USER_COLLECTION_NAME).document(firebaseUser.uid)
            userDocRef.set(user).await()

            val snapshot = userDocRef.get().await()
            val userData = snapshot.toObject(User::class.java)
                ?: throw RegistrationException("No se pudo cargar la información del usuario. Intente mas tarde.")

            user.name = userData.name
            user.birthdate = userData.birthdate
            user.gender = userData.gender
            user.profileImgUrl = userData.profileImgUrl
            user.uid = firebaseUser.uid

        } catch (e: Exception) {
            throw RegistrationException(
                e.message ?: "Error desconocido al registrar el usuario"
            )
        }
    }
    /**
     * Updates the user's password in FirebaseAuth and Firestore.
     * It re-authenticates the user first for security reasons.
     */
    override suspend fun updatePassword(currentPassword: String, newPassword: String) {
        val firebaseUser = auth.currentUser
            ?: throw AuthException("No hay un usuario con sesión activa.")

        val db = FirebaseFirestore.getInstance()

        try {
            // Paso 1: Re-autenticar al usuario para confirmar su identidad
            val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, currentPassword)
            auth.currentUser!!.reauthenticate(credential).await()

            // Paso 2: Si la re-autenticación es exitosa, actualizar la contraseña en FirebaseAuth
            auth.currentUser!!.updatePassword(newPassword).await()

            // Paso 3: Actualizar el hash de la contraseña en Firestore
            val userDocRef = db.collection(USER_COLLECTION_NAME).document(firebaseUser.uid)
            userDocRef.update("password", hashSHA256(newPassword)).await()

        } catch (e: Exception) {
            // Captura errores de re-autenticación (contraseña incorrecta) o de actualización
            throw AuthException("La contraseña actual es incorrecta o ocurrió un error al actualizarla.")
        }
    }

    override suspend fun sendPasswordResetMail(email: String) {
        val auth = FirebaseAuth.getInstance()

        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw AuthException("No se pudo enviar el mensaje para recuperar la contraseña, por favor intenta más tarde.")
        }
    }

    override suspend fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(filters: Map<String, Any>): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getByName(name: String): User? {
        TODO("Not yet implemented")
    }

    /**
     * Use signUp method instead.
     * @see signUp(user: User)
     */
    override suspend fun insert(item: User): String {
        throw UnsupportedOperationException("Use 'signUp' suspend method instead of 'insert'")
    }

    override suspend fun update(item: User): String {
        TODO("Not yet implemented")
    }


    override suspend fun delete(id: String): String {
        TODO("Not yet implemented")
    }
}