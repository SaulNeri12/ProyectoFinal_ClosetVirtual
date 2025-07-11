package equipo.closet.closetvirtual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

class OutfitCreationViewModel(
    private val outfitRepository: FirebaseOutfitRepository
) : ViewModel() {

    private val _currentOutfit = MutableLiveData(Outfit())
    val currentOutfit: LiveData<Outfit> = _currentOutfit

    private val _saveResult = MutableLiveData<Result<String>>()
    val saveResult: LiveData<Result<String>> = _saveResult

    fun addGarmentToOutfit(garment: Garment) {
        _currentOutfit.value?.let { outfit ->
            // Primero, se quitan las prendas existentes de la misma categoría para reemplazarlas.
            outfit.removeGarmentByCategory(garment.category)

            // VALIDACIÓN: Si se añade un Bodysuit, se quita el Bottom (pantalón).
            if (garment.category.equals("Bodysuit", ignoreCase = true)) {
                outfit.removeGarmentByCategory("Bottom")
            }
            // VALIDACIÓN: Si se añade un Bottom, se quita el Bodysuit.
            if (garment.category.equals("Bottom", ignoreCase = true)) {
                outfit.removeGarmentByCategory("Bodysuit")
            }

            // Finalmente, se añade la nueva prenda.
            outfit.addGarment(garment)
            // Se notifica a la Activity que el outfit ha cambiado para que redibuje la UI.
            _currentOutfit.postValue(outfit)
        }
    }

    fun removeGarmentFromOutfit(category: String) {
        _currentOutfit.value?.let { outfit ->
            outfit.removeGarmentByCategory(category)
            _currentOutfit.postValue(outfit)
        }
    }

    fun saveOutfit(name: String, tags: List<String>) {
        _currentOutfit.value?.let { outfit ->
            // VALIDACIÓN: Asegura que al menos una prenda ha sido seleccionada.
            if (outfit.getClothes().isEmpty()) {
                _saveResult.value = Result.failure(Exception("Debes seleccionar al menos una prenda."))
                return
            }

            outfit.name = name
            outfit.tags = tags.toMutableList()

            viewModelScope.launch {
                try {
                    val newOutfitId = outfitRepository.insert(outfit)
                    _saveResult.value = Result.success(newOutfitId)
                } catch (e: Exception) {
                    _saveResult.value = Result.failure(e)
                }
            }
        }
    }
}
