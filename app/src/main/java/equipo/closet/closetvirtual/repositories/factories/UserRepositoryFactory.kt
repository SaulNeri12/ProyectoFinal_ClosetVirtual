package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.repositories.DummyUserRepository
import equipo.closet.closetvirtual.repositories.FirebaseUserRepository

object UserRepositoryFactory : Factory<UserRepository> {

    private var debug = false

    override fun create(): UserRepository {
        /*
        if (debug) {
            return DummyUserRepository
        }
        */

        return FirebaseUserRepository
    }
}