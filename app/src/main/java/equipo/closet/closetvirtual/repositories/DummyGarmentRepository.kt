package equipo.closet.closetvirtual.repositories

import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.entities.Garment

object DummyGarmentRepository : Repository<Garment, String> {
    private val garments: MutableList<Garment> = mutableListOf()
    private var idCounter: Int = 1

    override fun getAll(): List<Garment> {
        return garments.toList()
    }

    override fun getAll(filters: Map<String, Any>): List<Garment> {
        return this.getAll()
    }

    override fun getByName(name: String): Garment? {
        for (garment in garments) {
            if (garment.name.equals(name, ignoreCase = true)) {
                return garment
            }
        }
        return null
    }

    override fun getById(id: String): Garment? {
        return garments.find { it.id == id }
    }

    override fun insert(item: Garment): String {
        val newItem = item.copy(id = "")
        garments.add(newItem)
        return newItem.id
    }

    override fun update(item: Garment): String {
        val index = garments.indexOfFirst { it.id == item.id }
        if (index != -1) {
            garments[index] = item
            return item.id
        } else {
            return ""
        }
    }

    override fun delete(id: String): String {
        val index = garments.indexOfFirst { it.id == id }
        if (index != -1) {
            garments.removeAt(index)
            return id
        } else {
            return ""
        }
    }
}