package equipo.closet.closetvirtual.outfit

import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import org.junit.Test
import org.junit.Assert.*

/*
data class Garment(
    var id: String = "",
    var name: String = "",
    var color: String = "",
    var tag: String = "",
    var category: String = "",
    var print: Boolean = false,
    var imageUri: String = "",
    var userId: String = ""
)
 */

class OutfitEntityTest {

    private fun createGarment(id: String, category: String): Garment {
        return Garment(
            id = id,
            name = "Garment $id",
            color = "color",
            category = category,
            print = false,
            imageUri = "image://uri",
            userId = "user"
        )
    }

    @Test
    fun addGarment_shouldSucceed_whenUniqueAndUnderLimit() {
        // Arrange
        val outfit = Outfit("1", "Outfit")
        val garment = createGarment("g1", "top")


        // Act
        val result = outfit.addGarment(garment)

        // Assert
        assertTrue(result)
        assertEquals(1, outfit.getClothes().size)
        assertEquals("g1", outfit.getClothesIds().first())
    }

    @Test
    fun addGarment_shouldFail_whenMoreThanFive() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        for (i in 1..5) {
            outfit.addGarment(createGarment("g$i", "cat$i"))
        }

        // Act
        val extraGarment = createGarment("g6", "cat6")
        val result = outfit.addGarment(extraGarment)

        // Assert
        assertFalse(result)
        assertEquals(5, outfit.getClothes().size)
    }

    @Test
    fun addGarment_shouldFail_whenDuplicateCategory() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        outfit.addGarment(createGarment("g1", "top"))


        // Act
        val duplicate = createGarment("g2", "top")
        val result = outfit.addGarment(duplicate)


        // Assert
        assertFalse(result)
        assertEquals(1, outfit.getClothes().size)
    }

    @Test
    fun addGarment_shouldFail_whenGarmentAlreadyExists() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        val g1 = createGarment("g1", "top")
        outfit.addGarment(g1)

        // Act
        val duplicate = createGarment("g1", "bottom")
        val result = outfit.addGarment(duplicate)

        // Assert
        assertFalse(result)
        assertEquals(1, outfit.getClothes().size)
    }

    @Test
    fun removeGarmentById_shouldRemoveCorrectly() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        outfit.addGarment(createGarment("g1", "top"))
        outfit.addGarment(createGarment("g2", "bottom"))

        // Act
        outfit.removeGarmentById("g1")

        // Assert
        assertEquals(1, outfit.getClothes().size)
        assertEquals("g2", outfit.getClothes().first().id)
    }

    @Test
    fun countByCategory_shouldReturnCorrectCount() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        outfit.addGarment(createGarment("g1", "top"))
        outfit.addGarment(createGarment("g2", "bottom"))
        outfit.addGarment(createGarment("g3", "top")) // should be rejected

        // Act
        val count = outfit.countByCategory("top")

        // Assert
        assertEquals(1, count)
    }

    @Test
    fun copy_shouldDuplicateOutfitWithClothes() {

        // Arrange
        val outfit = Outfit("1", "Outfit")
        outfit.addGarment(createGarment("g1", "top"))

        // Act
        val copy = outfit.copy(id = "2", name = "Copy")

        // Assert
        assertEquals(1, copy.getClothes().size)
        assertNotEquals(outfit.id, copy.id)
        assertEquals("Copy", copy.name)
    }

    @Test
    fun addGarment_failsWhenAdding6thItem() {

        // Arrange
        val outfit = Outfit("test", "Maxed Outfit")
        val categories = listOf("top1", "top2", "top3", "top4", "top5")

        // Act
        categories.forEachIndexed { i, category ->
            outfit.addGarment(Garment(id = i.toString(), name = "Item $i", category = category))
        }
        val result = outfit.addGarment(Garment(id = "6", name = "Overflow", category = "top6"))


        print("Clothes adding result: ${result}\n")

        // Assert
        assertFalse(result)
        assertEquals(5, outfit.getClothes().size)
    }

    @Test
    fun addGarment_bodysuitAndBottomCombinationFails() {

        // Arrange
        val outfit = Outfit()
        val bodysuit = Garment(id = "1", name = "Bodysuit", category = "bodysuit")
        val bottom = Garment(id = "2", name = "Jeans", category = "bottom")

        // Act
        val addedBodysuit = outfit.addGarment(bodysuit)
        val addedBottom = outfit.addGarment(bottom)

        // Assert
        assertTrue(addedBodysuit)
        assertFalse(addedBottom)
        assertEquals(1, outfit.getClothes().size)
    }

    @Test
    fun addGarment_bottomAndBodysuitCombinationFails() {

        // Arrange
        val outfit = Outfit()
        val bodysuit = Garment(id = "1", name = "Bodysuit", category = "bodysuit")
        val bottom = Garment(id = "2", name = "Jeans", category = "bottom")

        // Act
        val addedBottomFirst = outfit.addGarment(bottom)
        val addedBodysuitSecond = outfit.addGarment(bodysuit)

        // Assert
        assertTrue(addedBottomFirst)
        assertFalse(addedBodysuitSecond)
        assertEquals(1, outfit.getClothes().size)
    }

    @Test
    fun addTag_shouldAddTag_whenUnderLimitAndTagNotExists() {
        // Arrange
        val outfit = Outfit()
        outfit.clearTags()

        // Act
        val result = outfit.addTag("Casual")

        // Assert
        assertTrue(result)
        assertTrue(outfit.getTagsList().any { it == "casual" })
    }

    @Test
    fun addTag_shouldNotAddTag_whenTagAlreadyExistsIgnoringCase() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        outfit.addTag("Formal")

        // Act
        val result = outfit.addTag("formal") // "formal" lowercase duplicate

        // Assert
        assertFalse(result)
        assertEquals(1, outfit.getTagsList().size)
    }

    @Test
    fun addTag_shouldNotAddTag_whenTagLimitReached() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        val tagsToAdd = listOf("A", "B", "C", "D", "E", "F")
        tagsToAdd.forEach { outfit.addTag(it) }

        // Act
        val result = outfit.addTag("NewTag")

        // Assert
        assertFalse(result)
        assertEquals(6, outfit.getTagsList().size)
    }

    @Test
    fun removeTag_shouldRemoveTag_whenTagExistsIgnoringCase() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        outfit.addTag("Casual")
        outfit.addTag("Formal")

        // Act
        val result = outfit.removeTag("CASUAL") // uppercase, should remove "casual"

        // Assert
        assertTrue(result)
        assertFalse(outfit.getTagsList().any { it == "casual" })
        assertEquals(1, outfit.getTagsList().size)
    }

    @Test
    fun removeTag_shouldReturnFalse_whenTagDoesNotExist() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        outfit.addTag("Casual")
        outfit.addTag("Formal")

        // Act
        val result = outfit.removeTag("Sport")

        // Assert
        assertFalse(result)
        assertEquals(2, outfit.getTagsList().size)
    }

    @Test
    fun clearTags_shouldRemoveAllTags() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        outfit.addTag("Tag1")
        outfit.addTag("Tag2")

        // Act
        outfit.clearTags()

        // Assert
        assertTrue(outfit.getTagsList().isEmpty())
    }

    @Test
    fun getTagsList_shouldReturnAllTagsAsList() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        val tags = listOf("one", "two", "three")
        tags.forEach { outfit.addTag(it) }

        // Act
        val tagsList = outfit.getTagsList()

        // Assert
        assertEquals(tags.size, tagsList.size)
        tags.forEach { tag ->
            assertTrue(tagsList.any { it == tag.lowercase() })
        }
    }

    @Test
    fun getTagsSet_shouldReturnAllTagsAsSet() {

        // Arrange
        val outfit = Outfit()
        outfit.clearTags()
        val tags = listOf("one", "two", "three")
        tags.forEach { outfit.addTag(it) }

        // Act
        val tagsSet = outfit.getTagsSet()

        // Assert
        assertEquals(tags.size, tagsSet.size)
        tags.forEach { tag ->
            assertTrue(tagsSet.contains(tag.lowercase()))
        }
    }
}