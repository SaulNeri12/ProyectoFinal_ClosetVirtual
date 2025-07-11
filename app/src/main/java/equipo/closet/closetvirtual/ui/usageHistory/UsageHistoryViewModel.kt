package equipo.closet.closetvirtual.ui.usageHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.GarmentUsageTracker
import kotlinx.coroutines.launch
import java.util.Date

class UsageHistoryViewModel : ViewModel() {

    private val _usedGarments = MutableLiveData<List<Garment>>()
    val usedGarments: LiveData<List<Garment>> = _usedGarments

    fun fetchGarmentsForDate(date: Date) {
        viewModelScope.launch {
            try {
                val garmentIds = GarmentUsageTracker.getUsagesForDate(date)
                val garments = garmentIds.mapNotNull { id ->
                    FirebaseGarmentRepository.getById(id)
                }
                _usedGarments.postValue(garments)
            } catch (e: Exception) {
                // Manejar el error
                _usedGarments.postValue(emptyList())
            }
        }
    }
}