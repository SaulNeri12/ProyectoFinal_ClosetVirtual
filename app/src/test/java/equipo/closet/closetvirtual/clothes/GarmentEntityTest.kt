package equipo.closet.closetvirtual.clothes

import equipo.closet.closetvirtual.entities.Garment
import org.junit.Test
import org.junit.Assert.*

class GarmentEntityTest {
    private fun createGarment(): Garment {
        return Garment(id = "g1", name = "Camisa", color = "Rojo", category = "top")
    }

    @Test
    fun addTag_shouldAddTag_whenUnderLimitAndUnique() {

        // Arrange
        val garment = createGarment()

        // Act
        val result = garment.addTag("casual")

        // Assert
        assertTrue(result)
        assertTrue(garment.getTagsList().contains("casual"))
    }

    @Test
    fun addTag_shouldNotAdd_whenTagAlreadyExistsIgnoringCase() {

        // Arrange
        val garment = createGarment()
        garment.addTag("formal")

        // Act
        val result = garment.addTag("FORMAL")

        // Assert
        assertFalse(result)
        assertEquals(1, garment.getTagsCount())
    }

    @Test
    fun addTag_shouldNotAdd_whenTagLimitReached() {

        // Arrange
        val garment = createGarment()
        listOf("a", "b", "c", "d", "e", "f").forEach { garment.addTag(it) }

        // Act
        val result = garment.addTag("extra")

        // Assert
        assertFalse(result)
        assertEquals(6, garment.getTagsCount())
    }

    @Test
    fun removeTag_shouldRemove_whenTagExistsIgnoringCase() {

        // Arrange
        val garment = createGarment()
        garment.addTag("deportivo")

        // Act
        val result = garment.removeTag("DEPORTIVO")

        // Assert
        assertTrue(result)
        assertEquals(0, garment.getTagsCount())
    }

    @Test
    fun removeTag_shouldReturnFalse_whenTagDoesNotExist() {

        // Arrange
        val garment = createGarment()
        garment.addTag("urbano")

        // Act
        val result = garment.removeTag("formal")

        // Assert
        assertFalse(result)
        assertEquals(1, garment.getTagsCount())
    }

    @Test
    fun clearTags_shouldRemoveAllTags() {

        // Arrange
        val garment = createGarment()
        garment.addTag("elegante")
        garment.addTag("verano")

        // Act
        garment.clearTags()

        // Assert
        assertEquals(0, garment.getTagsCount())
        assertTrue(garment.getTagsList().isEmpty())
    }

    @Test
    fun getTagsList_shouldReturnCorrectTags() {

        // Arrange
        val garment = createGarment()
        garment.addTag("tag1")
        garment.addTag("tag2")

        // Act
        val tagsList = garment.getTagsList()

        // Assert
        assertEquals(2, tagsList.size)
        assertTrue(tagsList.contains("tag1"))
        assertTrue(tagsList.contains("tag2"))
    }

    @Test
    fun getTagsSet_shouldReturnCorrectTagsAsSet() {

        // Arrange
        val garment = createGarment()
        garment.addTag("tag1")
        garment.addTag("tag2")

        // Act
        val tagsSet = garment.getTagsSet()

        // Assert
        assertEquals(2, tagsSet.size)
        assertTrue(tagsSet.contains("tag1"))
        assertTrue(tagsSet.contains("tag2"))
    }

    @Test
    fun hasTag_shouldReturnTrue_whenTagExistsIgnoringCase() {

        // Arrange
        val garment = createGarment()
        garment.addTag("sport")

        // Act
        val result = garment.hasTag("SPORT")

        // Assert
        assertTrue(result)
    }

    @Test
    fun hasTag_shouldReturnFalse_whenTagDoesNotExist() {

        // Arrange
        val garment = createGarment()
        garment.addTag("casual")

        // Act
        val result = garment.hasTag("elegante")

        // Assert
        assertFalse(result)
    }
}