package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Outfit

object DummyOutfitRepository : Repository<Outfit, String> {

    private val outfits: MutableList<Outfit> = mutableListOf()

    override suspend fun getAll(): List<Outfit> {
        return outfits.toList()
    }

    override suspend fun getAll(filters: Map<String, Any>): List<Outfit> {
        return this.getAll()
    }

    override suspend fun getByName(name: String): Outfit? {
        for (outfit in outfits) {
            if (outfit.name.equals(name, ignoreCase = true)) {
                return outfit
            }
        }
        return null
    }

    override suspend fun getById(id: String): Outfit? {
        return outfits.find { it.id == id }
    }

    override suspend fun insert(item: Outfit): String {
        val newItem = item.copy()
        outfits.add(newItem)
        return newItem.id
    }

    override suspend fun update(item: Outfit): String {
        val index = outfits.indexOfFirst { it.id == item.id }
        if (index != -1) {
            outfits[index] = item
            return item.id
        } else {
            return ""
        }
    }

    override suspend fun delete(id: String): String {
        val index = outfits.indexOfFirst { it.id == id }
        if (index != -1) {
            outfits.removeAt(index)
            return id
        } else {
            return ""
        }
    }
}
