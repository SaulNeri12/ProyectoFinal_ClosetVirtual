package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.interfaces.UserRepository
import equipo.closet.closetvirtual.repositories.DummyUserRepository

object UserRepositoryFactory : Factory<UserRepository> {

    private var debug = true

    override fun create(): UserRepository {
        /*
        if (debug) {
            return DummyUserRepository
        }
        */

        return DummyUserRepository
    }
}