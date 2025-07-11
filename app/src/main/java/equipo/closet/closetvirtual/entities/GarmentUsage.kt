package equipo.closet.closetvirtual.entities

import java.util.Date

data class GarmentUsage(
    var id: String = "",
    var garmentId: String = "",
    var date: Date = Date(),
    var userId: String = "" // optional, used for data-linking
)
