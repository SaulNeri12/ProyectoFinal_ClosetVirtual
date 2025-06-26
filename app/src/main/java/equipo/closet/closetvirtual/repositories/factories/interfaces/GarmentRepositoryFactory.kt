package equipo.closet.closetvirtual.repositories.factories.interfaces

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.DummyGarmentRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository

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