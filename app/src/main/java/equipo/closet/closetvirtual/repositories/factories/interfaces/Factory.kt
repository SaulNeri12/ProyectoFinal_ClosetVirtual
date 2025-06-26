package equipo.closet.closetvirtual.repositories.factories.interfaces

interface Factory<T> {
    fun create(): T
}