package equipo.closet.closetvirtual.entities

data class Outfit(
    val id: Int,
    val name: String,
    val clothes_ids: MutableList<Int>,
    val clothes: MutableList<Garment>
)
