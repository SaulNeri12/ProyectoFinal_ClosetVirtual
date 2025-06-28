package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.exceptions.RegistrationException
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.repositories.exceptions.AuthException
import equipo.closet.closetvirtual.entities.User

object DummyUserRepository : UserRepository {

    private val users = mutableListOf<User>()

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

    override fun getAll(): List<User> = users

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