package equipo.closet.closetvirtual.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Garment(
    var id: String = "",
    var name: String = "",
    var color: String = "",
    var category: String = "",
    var print: Boolean = false,
    var imageUri: String = "",
    var userId: String = "",
    var tags: MutableList<String> = mutableListOf()
) : Parcelable {

    fun addTag(tag: String): Boolean {
        if (tags.size >= 6) return false
        if (tags.any { it.equals(tag, ignoreCase = true) }) return false
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

    fun getTagsCount(): Int {
        return tags.size
    }

    fun hasTag(tag: String): Boolean {
        return tags.contains(tag.lowercase())
    }
}

