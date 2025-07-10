package equipo.closet.closetvirtual.ui.dialogLogout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmLogoutViewModel : ViewModel() {

    val delete = MutableLiveData<Unit>()

    fun triggerDelete() {
        delete.value = Unit
    }

}