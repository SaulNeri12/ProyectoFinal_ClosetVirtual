package equipo.closet.closetvirtual.repositories.interfaces

interface Repository<T, U> {
    suspend fun getAll(): List<T>
    suspend fun getAll(filters: Map<String, Any>): List<T>
    suspend fun getById(id: U): T?
    suspend fun getByName(name: String): T?
    suspend fun insert(item: T): U
    suspend fun update(item: T): U
    suspend fun delete(id: U): U
}