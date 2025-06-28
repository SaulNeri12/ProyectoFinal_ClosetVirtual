package equipo.closet.closetvirtual.repositories

import com.google.firebase.auth.FirebaseAuth
import equipo.closet.closetvirtual.entities.User
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.repositories.exceptions.RegistrationException
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import kotlinx.coroutines.tasks.await

object FirebaseUserRepository : UserRepository {

    private var firebaseInitialized: Boolean = false
    private lateinit var auth: FirebaseAuth

    private fun initializeFirebase() {
        if (!firebaseInitialized) {
            auth = FirebaseAuth.getInstance()
            firebaseInitialized = true
        }
    }

    override suspend fun login(email: String, password: String): User {
        initializeFirebase()

        try {
            auth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = auth.currentUser
                ?: throw AuthException("El correo o contraseña son invalidos, ingrese los datos correctos.")

            return User(
                uid = firebaseUser.uid,
                name = "not-provided",
                email = firebaseUser.email ?: ""
                // TODO: must get the remaning values
            )

        } catch (e: Exception) {
            throw AuthException(
                e.message ?: "Error desconocido al registrar el usuario"
            )
        }
    }

    override suspend fun signUp(user: User) {
        initializeFirebase()

        try {
            auth.createUserWithEmailAndPassword(user.email, user.password).await()

            val firebaseUser = auth.currentUser
                ?: throw RegistrationException("No se pudo registrar al usuario. Intente nuevamente.")

            // TODO: Load that information in a User database collection

        } catch (e: Exception) {
            throw RegistrationException(
                e.message ?: "Error desconocido al registrar el usuario"
            )
        }
    }

    override fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun getById(id: String): User? {
        TODO("Not yet implemented")
    }

    override fun getByName(name: String): User? {
        TODO("Not yet implemented")
    }

    /**
     * Use signUp method instead.
     * @see signUp(user: User)
     */
    override fun insert(item: User): String {
        throw UnsupportedOperationException("Use 'signUp' suspend method instead of 'insert'")
    }

    override fun update(item: User): String {
        TODO("Not yet implemented")
    }

    override fun delete(id: String): String {
        TODO("Not yet implemented")
    }
}