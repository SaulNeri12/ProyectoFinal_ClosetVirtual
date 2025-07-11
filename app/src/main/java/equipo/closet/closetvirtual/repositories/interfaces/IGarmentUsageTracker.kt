package equipo.closet.closetvirtual.repositories.interfaces

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.GarmentUsage

interface IGarmentUsageTracker {
    suspend fun getUsedClothes(userId: String): List<GarmentUsage>
    suspend fun countGarmentUsages(garmentId: String): Int
    suspend fun countGarmentUsageMonthly(garmentId: String): Int
    suspend fun registerUsage(userId: String, garmentId: String): String
    suspend fun deleteUsage(garmentId: String): String
}