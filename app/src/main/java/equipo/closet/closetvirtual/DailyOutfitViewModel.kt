package equipo.closet.closetvirtual.ui.dailyOutfit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

class DailyOutfitViewModel(
    private val outfitRepository: FirebaseOutfitRepository
) : ViewModel() {

    // Guarda las prendas seleccionadas por categoría
    private val selectedGarments = mutableMapOf<String, Garment>()

    // LiveData para notificar a la UI cuando el guardado termina
    val saveResult = MutableLiveData<Result<Unit>>()

    fun addGarment(category: String, garment: Garment) {
        selectedGarments[category] = garment
    }

    fun saveDailyOutfit(name: String, tags: List<String>) {
        if (selectedGarments.isEmpty()) {
            saveResult.value = Result.failure(Exception("Debes seleccionar al menos una prenda."))
            return
        }

        val dailyOutfit = Outfit(
            name = name,
            tags = tags.toMutableList(),
            clothesIds = selectedGarments.values.map { it.id }.toMutableList()
        )

        viewModelScope.launch {
            try {
                outfitRepository.insert(dailyOutfit) // Reutilizamos el repositorio de Outfits
                saveResult.value = Result.success(Unit)
            } catch (e: Exception) {
                saveResult.value = Result.failure(e)
            }
        }
    }
}