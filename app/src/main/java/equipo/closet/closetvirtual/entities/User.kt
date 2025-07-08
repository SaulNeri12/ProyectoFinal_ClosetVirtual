package equipo.closet.closetvirtual.entities

import java.util.Date

data class User(
    var uid: String = "",
    var name: String? = "",
    var email: String = "",
    var gender: String? = "",
    var birthdate: Date? = Date(),
    var password: String = "",
    var fireAuthUID: String = "" // used for data-linking
)
