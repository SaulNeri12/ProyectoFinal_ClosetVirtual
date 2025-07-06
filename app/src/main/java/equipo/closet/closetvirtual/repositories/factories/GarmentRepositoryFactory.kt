package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.DummyGarmentRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository

object GarmentRepositoryFactory : Factory<Repository<Garment, String>> {

    private var debug: Boolean = true

    override fun create(): Repository<Garment, String> {


        if (debug) {
            return DummyGarmentRepository
        }


        return FirebaseGarmentRepository
    }
}