package equipo.closet.closetvirtual.ui.searchOutfitFilter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchOutfitFilterViewModel : ViewModel()  {

    var tags = MutableLiveData<List<String>>()
    val searchEvent = MutableLiveData<Unit>()

    fun setTags(selectedTags: List<String>) {
        tags.value = selectedTags
    }

    fun triggerSearchEvent() {
        searchEvent.value = Unit
    }

}