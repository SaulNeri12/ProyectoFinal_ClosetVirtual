package equipo.closet.closetvirtual.entities

/*
data class Outfit(
    val id: Int,
    val name: String,
    val clothes_ids: MutableList<Int>,
    val clothes: MutableList<Garment>
)*/

class Outfit(
    var id: String = "",
    var name: String
) {
    private val clothesIds: MutableList<String> = mutableListOf()
    private val clothes: MutableList<Garment> = mutableListOf()

    fun addGarment(garment: Garment): Boolean {
        val category = garment.category.lowercase()

        if (clothes.size >= 5) return false
        if (clothes.any { it.id == garment.id }) return false
        if (clothes.any { it.category.equals(garment.category, ignoreCase = true) }) return false

        clothes.add(garment)
        clothesIds.add(garment.id)
        return true
    }

    fun removeGarmentById(garmentId: String) {
        clothes.removeAll { it.id == garmentId }
        clothesIds.remove(garmentId)
    }

    fun countByCategory(category: String): Int {
        return clothes.count { it.category.equals(category, ignoreCase = true) }
    }

    fun getClothes(): List<Garment> = clothes.toList()
    fun getClothesIds(): List<String> = clothesIds.toList()

    fun copy(
        id: String = this.id,
        name: String = this.name
    ): Outfit {
        val newOutfit = Outfit(this.id, this.name)
        this.clothes.forEach { newOutfit.clothes.add(it.copy()) }
        this.clothesIds.forEach { newOutfit.clothesIds.add(it) }
        return newOutfit
    }
}
