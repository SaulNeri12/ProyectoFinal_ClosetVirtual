package equipo.closet.closetvirtual.entities

import com.google.firebase.firestore.Exclude


class Outfit(
    var id: String = "",
    var name: String = "",
    var clothesIds: MutableList<String> = mutableListOf(),
    var tags: MutableList<String> = mutableListOf(),
    var userId: String = ""
) {

    @Exclude
    private var clothes: MutableList<Garment> = mutableListOf()

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

    fun getClothes(): List<Garment> = clothes.toList()

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


    fun copy(
        id: String = this.id,
        name: String = this.name,
        userId: String = this.userId,
        tags: MutableList<String> = this.tags
    ): Outfit
    {
        val newOutfit = Outfit(id=id, name=name, userId=userId, tags = tags)
        this.clothes.forEach { newOutfit.clothes.add(it.copy()) }
        this.clothesIds.forEach { newOutfit.clothesIds.add(it) }
        return newOutfit
    }

    override fun toString(): String {
        return "Outfit(id='$id', name='$name', clothesIds=$clothesIds, tags=$tags)"
    }
}
