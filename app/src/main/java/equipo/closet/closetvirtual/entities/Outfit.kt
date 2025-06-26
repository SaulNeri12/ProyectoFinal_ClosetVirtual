package equipo.closet.closetvirtual.entities

data class Outfit(
    val id: Long,
    val name: String,
    val clothes_ids: MutableList<Long>,
    val clothes: MutableList<Garment>
)
