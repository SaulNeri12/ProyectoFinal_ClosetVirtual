package equipo.closet.closetvirtual.ui.clothesCategoryFilter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClothesCategoryViewModel : ViewModel() {

    var tags = MutableLiveData<List<String>>()

    fun setTags(selectedTags: List<String>) {
        tags.value = selectedTags
    }

    fun clearTags() {
        tags.value = emptyList()
    }

}