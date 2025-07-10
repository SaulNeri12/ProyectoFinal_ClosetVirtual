package equipo.closet.closetvirtual.entities

import com.google.firebase.firestore.Exclude


class Outfit(
    var id: String = "",
    var name: String = "",
    var nameLowerCase: String = "",
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

    /**
     * MÉTODO AÑADIDO:
     * Busca en la lista de prendas y elimina todas las que coincidan
     * con la categoría especificada.
     */
    fun removeGarmentByCategory(category: String) {
        // Usa removeAll para eliminar de la lista todos los elementos
        // que cumplan con la condición.
        clothes.removeAll { garment ->
            // La condición es que la categoría de la prenda sea igual a la buscada
            garment.category.equals(category, ignoreCase = true)
        }
        // Actualiza la lista de IDs para que coincida con la lista de prendas
        clothesIds = clothes.map { it.id }.toMutableList()
    }

    /**
     * MÉTODO AÑADIDO (necesario para la lógica de redibujado):
     * Busca y devuelve la primera prenda que coincida con una categoría.
     */
    fun getGarmentForCategory(category: String): Garment? {
        return clothes.find { it.category.equals(category, ignoreCase = true) }
    }



    override fun toString(): String {
        return "Outfit(id='$id', name='$name', clothesIds=$clothesIds, tags=$tags)"
    }
}
