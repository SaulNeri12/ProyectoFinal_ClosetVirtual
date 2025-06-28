package equipo.closet.closetvirtual.repositories.interfaces

import equipo.closet.closetvirtual.entities.User

/**
 * Interface that defines user-related data operations including both
 * persistence (CRUD) and authentication mechanisms (login and registration).
 *
 * Repository<..., String> part is used for database IDs
 */
interface UserRepository : Repository<User, String> {

    /**
     * Authenticates a user using the provided email and password credentials.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @throws AuthenticationException if the credentials are invalid or the login fails.
     * @return User The logged-in user
     */
    suspend fun login(email: String, password: String): User

    /**
     * Registers a new user in the system.
     * @throws RegistrationException if the user could not be created or already exists.
     */
    suspend fun signUp(user: User)
}
