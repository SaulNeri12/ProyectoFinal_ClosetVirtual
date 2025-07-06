package equipo.closet.closetvirtual.outfit

import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.DummyOutfitRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockOutfitRepository {
    private lateinit var repo: DummyOutfitRepository

    @Before
    fun setup() {
        repo = DummyOutfitRepository
        runBlocking {
            // limpiar la lista antes de cada prueba
            val outfits = repo.getAll()
            outfits.forEach { repo.delete(it.id) }
        }
    }

    @Test
    fun insert_shouldAddOutfitSuccessfully() = runBlocking {
        // Arrange
        val outfit = Outfit(id = "1", name = "Casual Look")

        // Act
        val resultId = repo.insert(outfit)

        // Assert
        val retrieved = repo.getById("1")
        assertEquals("1", resultId)
        assertNotNull(retrieved)
        assertEquals("Casual Look", retrieved?.name)
    }

    @Test
    fun getByName_shouldReturnCorrectOutfit() = runBlocking {
        // Arrange
        val outfit = Outfit(id = "2", name = "Formal Wear")
        repo.insert(outfit)

        // Act
        val retrieved = repo.getByName("formal wear")

        // Assert
        assertNotNull(retrieved)
        assertEquals("2", retrieved?.id)
    }

    @Test
    fun update_shouldModifyExistingOutfit() = runBlocking {
        // Arrange
        val outfit = Outfit(id = "3", name = "Sporty")
        repo.insert(outfit)

        val updated = outfit.copy(name = "Sporty Updated")

        // Act
        val resultId = repo.update(updated)

        // Assert
        val retrieved = repo.getById("3")
        assertEquals("3", resultId)
        assertEquals("Sporty Updated", retrieved?.name)
    }

    @Test
    fun delete_shouldRemoveOutfit() = runBlocking {
        // Arrange
        val outfit = Outfit(id = "4", name = "To Be Deleted")
        repo.insert(outfit)

        // Act
        val deletedId = repo.delete("4")
        val retrieved = repo.getById("4")

        // Assert
        assertEquals("4", deletedId)
        assertNull(retrieved)
    }

    @Test
    fun getAll_shouldReturnAllOutfits() = runBlocking {
        // Arrange
        val outfit1 = Outfit(id = "5", name = "One")
        val outfit2 = Outfit(id = "6", name = "Two")
        repo.insert(outfit1)
        repo.insert(outfit2)

        // Act
        val all = repo.getAll()

        // Assert
        assertTrue(all.any { it.id == "5" })
        assertTrue(all.any { it.id == "6" })
        assertTrue(all.size >= 2)
    }
}