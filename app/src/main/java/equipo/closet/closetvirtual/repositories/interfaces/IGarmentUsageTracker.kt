package equipo.closet.closetvirtual.repositories.interfaces

interface IGarmentUsageTracker {
    suspend fun countGarmentUsages(garmentId: String): Int
    suspend fun countGarmentUsageMonthly(garmentId: String): Int
    suspend fun registerUsage(garmentId: String): String
    suspend fun deleteUsage(garmentId: String): String
}