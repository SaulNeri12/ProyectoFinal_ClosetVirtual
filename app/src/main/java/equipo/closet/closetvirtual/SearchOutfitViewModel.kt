package equipo.closet.closetvirtual.ui.searchOutfit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

class SearchOutfitViewModel : ViewModel() {

    private val outfitRepository = FirebaseOutfitRepository(FirebaseGarmentRepository)
    private val garmentRepository = FirebaseGarmentRepository

    // LiveData para la lista completa de outfits
    private val _outfits = MutableLiveData<List<Outfit>>()
    val outfits: LiveData<List<Outfit>> = _outfits

    // LiveData para el mapa de todas las prendas (para buscar imágenes fácilmente)
    private val _allGarmentsMap = MutableLiveData<Map<String, Garment>>()
    val allGarmentsMap: LiveData<Map<String, Garment>> = _allGarmentsMap

    // LiveData para la lista filtrada que se mostrará en la UI
    private val _filteredOutfits = MutableLiveData<List<Outfit>>()
    val filteredOutfits: LiveData<List<Outfit>> = _filteredOutfits

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Cargar todas las prendas y todos los outfits al iniciar
                val allGarments = garmentRepository.getAll()
                _allGarmentsMap.value = allGarments.associateBy { it.id }

                val allOutfits = outfitRepository.getAll()
                _outfits.value = allOutfits
                _filteredOutfits.value = allOutfits // Al principio, la lista filtrada es la lista completa
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            // Si la búsqueda está vacía, mostrar todos los outfits
            _filteredOutfits.value = _outfits.value
        } else {
            // Filtrar la lista de outfits por el nombre
            _filteredOutfits.value = _outfits.value?.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}