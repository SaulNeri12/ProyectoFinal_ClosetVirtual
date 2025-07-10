package equipo.closet.closetvirtual.entities

// Singleton para manejar el caché de prendas
object GarmentCache {
    private val garmentsMap = mutableMapOf<String, Garment>()

    // Carga o actualiza el caché con una lista de prendas
    fun setGarments(garments: List<Garment>) {
        garmentsMap.clear()
        garments.forEach { garment ->
            garmentsMap[garment.id] = garment
        }
    }

    // Obtiene una prenda por su ID desde el caché
    fun getGarmentById(id: String): Garment? {
        return garmentsMap[id]
    }

    // Obtiene todas las prendas del caché
    fun getAllGarments(): List<Garment> {
        return garmentsMap.values.toList()
    }

    // Métodos para actualizar/eliminar prendas individuales
    fun upsertGarment(garment: Garment) {
        garmentsMap[garment.id] = garment
    }

    fun removeGarment(garmentId: String) {
        garmentsMap.remove(garmentId)
    }
}