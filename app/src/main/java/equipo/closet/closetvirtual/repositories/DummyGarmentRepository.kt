package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.interfaces.Repository

object DummyGarmentRepository : Repository<Garment, Int> {
    private val garments: MutableList<Garment> = mutableListOf()
    private var idCounter: Int = 1

    override fun getAll(): List<Garment> {
        return garments.toList()
    }

    override fun getByName(name: String): Garment? {
        for (garment in garments) {
            if (garment.name.equals(name, ignoreCase = true)) {
                return garment
            }
        }
        return null
    }

    override fun getById(id: Int): Garment? {
        return garments.find { it.id == id }
    }

    override fun insert(item: Garment): Int {
        val newItem = item.copy(id = idCounter++)
        garments.add(newItem)
        return newItem.id
    }

    override fun update(item: Garment): Int {
        val index = garments.indexOfFirst { it.id == item.id }
        if (index != -1) {
            garments[index] = item
            return 1
        } else {
            return 0
        }
    }

    override fun delete(id: Int): Int {
        val index = garments.indexOfFirst { it.id == id }
        if (index != -1) {
            garments.removeAt(index)
            return 1
        } else {
            return 0
        }
    }
}