@file:Suppress("DEPRECATION")

package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.exceptions.RegistrationException
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.entities.User
import java.util.Date

object DummyUserRepository : UserRepository {

    private val users = mutableListOf<User>()

    private var initializedUsers: Boolean = false

    fun initialize() {
        if (initializedUsers) {
            return
        }

        val user1 = User(
            uid = "user_001",
            name = "Alice López",
            email = "alice@example.com",
            gender = "Femenino",
            birthdate = Date(95, 5, 24), // 24 junio 1995
            password = "password123",
            profileImgUrl = null,
            fireAuthUID = "auth_001"
        )

        val user2 = User(
            uid = "user_002",
            name = "Bruno Martínez",
            email = "bruno@example.com",
            gender = "Masculino",
            birthdate = Date(98, 10, 3), // 3 noviembre 1998
            password = "brunopass456",
            profileImgUrl = null,
            fireAuthUID = "auth_002"
        )

        val user3 = User(
            uid = "user_003",
            name = "Camila Rivera",
            email = "camila@example.com",
            gender = "Femenino",
            birthdate = Date(2000 - 1900, 1, 14), // 14 febrero 2000
            password = "cami_secure",
            profileImgUrl = null,
            fireAuthUID = "auth_003"
        )

        users.addAll(listOf(user1, user2, user3))

        initializedUsers = true
    }

    override suspend fun login(email: String, password: String): User {
        val user = users.find { it.email == email }

        if (user == null || user.password != password) {
            throw AuthException("Correo o contraseña incorrectos.")
        }

        return user
    }

    override suspend fun signUp(user: User) {
        if (users.any { it.email == user.email }) {
            throw RegistrationException("Ya existe una cuenta con ese correo.")
        }

        users.add(user)
    }

    /**
     * AÑADIDO: Implementación de updatePassword para cumplir con la interfaz.
     * Esto soluciona el error de compilación.
     */
    override suspend fun updatePassword(currentPassword: String, newPassword: String) {
        // En este repositorio de prueba, asumimos que el usuario que cambia la contraseña
        // es el que coincide con la contraseña actual.
        val userToUpdate = users.find { it.password == currentPassword }

        if (userToUpdate != null) {
            userToUpdate.password = newPassword
        } else {
            // Si no se encuentra ningún usuario con esa contraseña, lanzamos un error,
            // simulando el comportamiento del repositorio real.
            throw AuthException("La contraseña actual es incorrecta.")
        }
    }

    override fun getAll(): List<User> = users
    override fun getAll(filters: Map<String, Any>): List<User> {
        return this.getAll()
    }

    override fun getById(id: String): User? = users.find { it.uid == id }

    override fun getByName(name: String): User? = users.find { it.name == name }

    override fun insert(item: User): String {
        users.add(item)
        return item.uid
    }

    override fun update(item: User): String {
        val index = users.indexOfFirst { it.uid == item.uid }
        if (index != -1) {
            users[index] = item
            return item.uid
        } else {
            throw IllegalArgumentException("Usuario con UID ${item.uid} no encontrado.")
        }
    }

    override fun delete(id: String): String {
        val iterator = users.iterator()
        while (iterator.hasNext()) {
            val user = iterator.next()
            if (user.uid == id) {
                iterator.remove()
                return id
            }
        }

        throw IllegalArgumentException("Usuario con UID $id no encontrado.")
    }
}