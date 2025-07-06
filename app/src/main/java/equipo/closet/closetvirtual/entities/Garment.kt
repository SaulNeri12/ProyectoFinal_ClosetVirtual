package equipo.closet.closetvirtual.entities

data class Garment(
    var id: String = "",
    var name: String = "",
    var color: String = "",
    var category: String = "",
    var print: Boolean = false,
    var imageUri: String = "",
    var userId: String = ""
) {
    private val tags: MutableSet<String> = mutableSetOf()

    fun addTag(tag: String): Boolean {
        if (tags.size >= 6) return false
        return tags.add(tag.lowercase())
    }

    fun removeTag(tag: String): Boolean {
        return tags.remove(tag.lowercase())
    }

    fun clearTags() {
        tags.clear()
    }

    fun getTagsList(): List<String> {
        return tags.toList()
    }

    fun getTagsSet(): Set<String> {
        return tags.toSet()
    }

    fun getTagsCount(): Int {
        return tags.size
    }

    fun hasTag(tag: String): Boolean {
        return tags.contains(tag.lowercase())
    }
}
