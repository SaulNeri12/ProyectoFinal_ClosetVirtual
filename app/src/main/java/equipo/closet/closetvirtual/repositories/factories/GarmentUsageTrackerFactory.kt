package equipo.closet.closetvirtual.repositories.factories

import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import equipo.closet.closetvirtual.repositories.factories.interfaces.Factory
import equipo.closet.closetvirtual.repositories.GarmentUsageTracker

object GarmentUsageTrackerFactory : Factory<IGarmentUsageTracker> {

    override fun create(): IGarmentUsageTracker {
        return GarmentUsageTracker
    }

}