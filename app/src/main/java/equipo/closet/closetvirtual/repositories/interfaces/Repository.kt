package equipo.closet.closetvirtual.repositories.interfaces

interface Repository<T, U> {
    fun getAll(): List<T>
    fun getAll(filters: Map<String, Any>): List<T>
    fun getById(id: U): T?
    fun getByName(name: String): T?
    fun insert(item: T): U
    fun update(item: T): U
    fun delete(id: U): U
}