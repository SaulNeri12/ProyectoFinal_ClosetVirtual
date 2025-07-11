package equipo.closet.closetvirtual.ui.dailyOutfit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.repositories.GarmentUsageTracker
import kotlinx.coroutines.launch

class DailyOutfitViewModel(outfitRepository: FirebaseOutfitRepository) : ViewModel() {


    private val selectedGarments = mutableMapOf<String, Garment>()
    val saveResult = MutableLiveData<Result<Unit>>()

    fun addGarment(viewId: String, garment: Garment) {
        selectedGarments[viewId] = garment
    }

    // FUNCIÓN AÑADIDA
    fun removeGarment(viewId: String) {
        selectedGarments.remove(viewId)
    }

    fun registerUsageOfSelectedGarments() {
        if (selectedGarments.isEmpty()) {
            saveResult.value = Result.failure(Exception("Debes seleccionar al menos una prenda."))
            return
        }

        viewModelScope.launch {
            try {
                selectedGarments.values.forEach { garment ->
                    GarmentUsageTracker.registerUsage(garment.id)
                }
                saveResult.value = Result.success(Unit)
            } catch (e: Exception) {
                saveResult.value = Result.failure(e)
            }
        }
    }
}