package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.DummyGarmentRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Garment

object GarmentRepositoryFactory : Factory<Repository<Garment, Int>> {

    private var debug: Boolean = true

    override fun create(): Repository<Garment, Int> {

        /*
        if (debug) {
            return DummyGarmentRepository
        }
         */

        return DummyGarmentRepository
    }
}