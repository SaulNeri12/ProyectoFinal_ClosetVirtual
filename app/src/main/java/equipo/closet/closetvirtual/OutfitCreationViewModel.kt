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
            outfit.addGarment(garment)
            _currentOutfit.postValue(outfit)
        }
    }

    // FUNCIÓN AÑADIDA
    fun removeGarmentFromOutfit(garmentId: String) {
        _currentOutfit.value?.let { outfit ->
            // Asume que tu clase Outfit tiene un método para remover por ID
            outfit.removeGarmentById(garmentId)
            _currentOutfit.postValue(outfit)
        }
    }

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