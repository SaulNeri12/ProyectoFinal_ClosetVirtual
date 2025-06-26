package equipo.closet.closetvirtual.entities

data class Garment(
    var id: Int = 0,
    var name: String,
    var color: String,
    var tag: String,
    var category: String,
    var print: Boolean,
    var imageUri: String = ""
)
