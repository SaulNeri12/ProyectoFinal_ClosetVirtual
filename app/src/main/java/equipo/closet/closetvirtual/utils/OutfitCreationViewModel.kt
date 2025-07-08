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
    // Inyecta tu repositorio.
    private val outfitRepository: FirebaseOutfitRepository
) : ViewModel() {

    // El outfit que se está construyendo
    private val _currentOutfit = MutableLiveData<Outfit>(Outfit())
    val currentOutfit: LiveData<Outfit> = _currentOutfit

    // LiveData para comunicar el resultado a la Activity
    private val _saveResult = MutableLiveData<Result<String>>()
    val saveResult: LiveData<Result<String>> = _saveResult

    /**
     * Intenta agregar una prenda al outfit actual.
     * La lógica de validación está en la clase Outfit.
     */
    fun addGarmentToOutfit(garment: Garment) {
        _currentOutfit.value?.let { outfit ->
            val success = outfit.addGarment(garment)
            if (success) {
                // Notifica a los observadores que el outfit ha cambiado
                _currentOutfit.value = outfit
            } else {
                // Aquí podrías notificar a la UI que la prenda no se pudo agregar (ej. conflicto de categoría)
                // Por ejemplo, usando otro LiveData para mostrar un Toast.
            }
        }
    }

    /**
     * Elimina una prenda del outfit actual.
     */
    fun removeGarmentFromOutfit(garmentId: String) {
        _currentOutfit.value?.let { outfit ->
            outfit.removeGarmentById(garmentId)
            // Notifica a los observadores que el outfit ha cambiado
            _currentOutfit.value = outfit
        }
    }

    /**
     * Guarda el outfit en Firebase.
     */
    fun saveOutfit(name: String, tags: List<String>) {
        _currentOutfit.value?.let { outfit ->
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