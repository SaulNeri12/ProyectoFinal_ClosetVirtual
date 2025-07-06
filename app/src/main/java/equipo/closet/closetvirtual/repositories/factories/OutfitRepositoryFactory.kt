package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.repositories.DummyOutfitRepository
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Outfit


object OutfitRepositoryFactory : Factory<Repository<Outfit, String>> {

    private lateinit var instance: Repository<Outfit, String>
    private var initialized: Boolean = false
    private var debug: Boolean = false

    override fun create(): Repository<Outfit, String> {

        if (debug) {
            instance = DummyOutfitRepository
            initialized = true
        }

        if (!initialized) {
            instance = FirebaseOutfitRepository(GarmentRepositoryFactory.create())
            initialized = true
        }

        return instance
    }
}