package equipo.closet.closetvirtual.entities

data class Garment(
    var id: String = "",
    var name: String = "",
    var color: String = "",
    var tag: String = "",
    var category: String = "",
    var print: Boolean = false,
    var imageUri: String = "",
    var userId: String = ""
)
