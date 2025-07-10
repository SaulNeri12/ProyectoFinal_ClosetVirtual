package equipo.closet.closetvirtual.ui.clothesCategoryFilter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClothesCategoryFilterViewModel : ViewModel() {

    var tags = MutableLiveData<List<String>>()
    val searchEvent = MutableLiveData<Unit>()

    fun setTags(selectedTags: List<String>) {
        tags.value = selectedTags
    }

    fun shootSearchEvent() {
        searchEvent.value = Unit
    }

}