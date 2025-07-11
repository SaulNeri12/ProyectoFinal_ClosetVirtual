package equipo.closet.closetvirtual.global

import equipo.closet.closetvirtual.entities.Garment

object ClothesCache {
    private val garmentsMap = mutableMapOf<String, Garment>()

    fun setGarments(garments: List<Garment>) {
        garmentsMap.clear()
        garments.forEach { garment ->
            garmentsMap[garment.id] = garment
        }
    }

    fun getGarmentById(id: String): Garment? {
        return garmentsMap[id]
    }

    fun getAllGarments(): List<Garment> {
        return garmentsMap.values.toList()
    }

    fun upsertGarment(garment: Garment) {
        garmentsMap[garment.id] = garment
    }

    fun removeGarment(garmentId: String) {
        garmentsMap.remove(garmentId)
    }
}