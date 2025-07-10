package equipo.closet.closetvirtual.ui.clothesSelectionFilter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClothesSelectionViewModel : ViewModel()  {

    var tags = MutableLiveData<List<String>>()

    fun setTags(selectedTags: List<String>) {
        tags.value = selectedTags
    }

    fun clearTags() {
        tags.value = emptyList()
    }

}