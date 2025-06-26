package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Outfit

object DummyOutfitRepository : Repository<Outfit, Int> {

    private val outfits: MutableList<Outfit> = mutableListOf()
    private var idCounter: Int = 1

    override fun getAll(): List<Outfit> {
        return outfits.toList()
    }

    override fun getByName(name: String): Outfit? {
        for (outfit in outfits) {
            if (outfit.name.equals(name, ignoreCase = true)) {
                return outfit
            }
        }
        return null
    }

    override fun getById(id: Int): Outfit? {
        return outfits.find { it.id == id }
    }

    override fun insert(item: Outfit): Int {
        val newItem = item.copy(id = idCounter++)
        outfits.add(newItem)
        return newItem.id
    }

    override fun update(item: Outfit): Int {
        val index = outfits.indexOfFirst { it.id == item.id }
        if (index != -1) {
            outfits[index] = item
            return 1
        } else {
            return 0
        }
    }

    override fun delete(id: Int): Int {
        val index = outfits.indexOfFirst { it.id == id }
        if (index != -1) {
            outfits.removeAt(index)
            return 1
        } else {
            return 0
        }
    }
}
