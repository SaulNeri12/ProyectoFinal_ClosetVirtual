package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.interfaces.Repository

object DummyOutfitRepository : Repository<Outfit, Int> {

    private val outfits: MutableList<Outfit> = mutableListOf()
    private var idCounter: Int = 1

    override suspend fun getAll(): List<Outfit> {
        return outfits.toList() // copia inmutable
    }

    override suspend fun getById(id: Int): Outfit? {
        return outfits.find { it.id == id }
    }

    override suspend fun insert(item: Outfit): Int {
        val newItem = item.copy(id = idCounter++)
        outfits.add(newItem)
        return newItem.id
    }

    override suspend fun update(item: Outfit): Int {
        val index = outfits.indexOfFirst { it.id == item.id }
        if (index != -1) {
            outfits[index] = item
            return 1
        } else {
            return 0
        }
    }

    override suspend fun delete(id: Int): Int {
        val index = outfits.indexOfFirst { it.id == id }
        if (index != -1) {
            outfits.removeAt(index)
            return 1
        } else {
            return 0
        }
    }
}
