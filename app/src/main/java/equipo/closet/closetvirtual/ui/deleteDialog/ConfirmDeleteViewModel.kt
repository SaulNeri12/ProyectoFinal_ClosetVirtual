package equipo.closet.closetvirtual.ui.deleteDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmDeleteViewModel : ViewModel() {

    val delete = MutableLiveData<Unit>()

    fun triggerDelete() {
        delete.value = Unit
    }

}