package equipo.closet.closetvirtual.entities

class Outfit(
    var id: String = "",
    var name: String = "",
) {
    private val clothesIds: MutableList<String> = mutableListOf()
    private val clothes: MutableList<Garment> = mutableListOf()
    private val tags: MutableSet<String> = mutableSetOf()

    fun addGarment(garment: Garment): Boolean {
        val category = garment.category.lowercase()

        if (clothes.size >= 5) return false
        if (clothes.any { it.id == garment.id }) return false
        if (clothes.any { it.category.equals(garment.category, ignoreCase = true) }) return false

        // verifies that bottom and bodysuit cant be combined...
        if ((category == "bodysuit" && clothes.any { it.category.equals("bottom", ignoreCase = true) }) ||
            (category == "bottom" && clothes.any { it.category.equals("bodysuit", ignoreCase = true) })
        ) {
            return false
        }

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

    fun addTag(tag: String): Boolean {
        if (tags.size >= 6) return false
        return tags.add(tag.lowercase())
    }

    fun removeTag(tag: String): Boolean {
        return this.tags.remove(tag.lowercase())
    }

    fun clearTags(): Unit {
        this.tags.clear()
    }

    fun getTagsList(): MutableList<String> {
        return this.tags.toMutableList()
    }

    fun getTagsSet(): MutableSet<String> {
        return this.tags.toMutableSet()
    }

    fun copy(
        id: String = this.id,
        name: String = this.name,
        tags: MutableSet<String> = this.tags
    ): Outfit
    {
        val newOutfit = Outfit(id, name)
        this.clothes.forEach { newOutfit.clothes.add(it.copy()) }
        this.clothesIds.forEach { newOutfit.clothesIds.add(it) }
        this.tags.forEach({ newOutfit.addTag(it) })
        return newOutfit
    }
}
