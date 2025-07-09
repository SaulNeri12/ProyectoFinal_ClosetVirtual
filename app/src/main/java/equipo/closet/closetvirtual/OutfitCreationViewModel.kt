package equipo.closet.closetvirtual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

/// En tu clase OutfitCreationViewModel.kt

class OutfitCreationViewModel(
    private val outfitRepository: FirebaseOutfitRepository
) : ViewModel() {


    private val _currentOutfit = MutableLiveData(Outfit())
    val currentOutfit: LiveData<Outfit> = _currentOutfit

    private val _saveResult = MutableLiveData<Result<String>>()
    val saveResult: LiveData<Result<String>> = _saveResult

    fun addGarmentToOutfit(garment: Garment) {
        _currentOutfit.value?.let { outfit ->
            // La lógica para añadir o reemplazar una prenda por categoría está en la clase Outfit
            outfit.addGarment(garment)
            _currentOutfit.postValue(outfit)
        }
    }

    fun saveOutfit(name: String, tags: List<String>) {
        _currentOutfit.value?.let { outfit ->
            // Valida que el outfit no esté vacío
            if (outfit.getClothes().isEmpty()) {
                _saveResult.value = Result.failure(Exception("Debes seleccionar al menos una prenda."))
                return
            }

            // Asigna los datos finales al objeto
            outfit.name = name
            outfit.tags = tags.toMutableList()

            viewModelScope.launch {
                try {
                    // Llama al repositorio para insertar el outfit en Firebase
                    val newOutfitId = outfitRepository.insert(outfit)
                    _saveResult.value = Result.success(newOutfitId)
                } catch (e: Exception) {
                    _saveResult.value = Result.failure(e)
                }
            }
        }
    }
}