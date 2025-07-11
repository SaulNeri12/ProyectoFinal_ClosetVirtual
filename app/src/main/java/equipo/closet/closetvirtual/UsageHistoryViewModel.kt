package equipo.closet.closetvirtual.ui.usageHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.objects.SessionManager
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.GarmentUsageTracker
import kotlinx.coroutines.launch
import java.util.Date

class UsageHistoryViewModel : ViewModel() {

    private val _usedGarments = MutableLiveData<List<Garment>>()
    val usedGarments: LiveData<List<Garment>> = _usedGarments

    /**
     * Obtiene los registros de uso para una fecha, los trae a la app y luego
     * los filtra para mostrar solo los del usuario actual.
     * @param date La fecha seleccionada por el usuario.
     */
    fun fetchGarmentsForDate(date: Date) {
        viewModelScope.launch {
            try {
                // 1. Obtiene los IDs de TODAS las prendas usadas en la fecha desde Firebase.
                val allGarmentIdsOnDate = GarmentUsageTracker.getUsagesForDate(date)

                // 2. Busca la información completa de cada prenda por su ID.
                val allGarmentsUsedOnDate = allGarmentIdsOnDate.mapNotNull { id ->
                    FirebaseGarmentRepository.getById(id)
                }

                // 3. Filtra la lista para quedarse solo con las del usuario actual.
                val currentUserGarments = allGarmentsUsedOnDate.filter { garment ->
                    garment.userId == SessionManager.user.uid
                }

                // 4. Publica la lista final y filtrada para que la UI la muestre.
                _usedGarments.postValue(currentUserGarments)

            } catch (e: Exception) {
                // En caso de cualquier error, publica una lista vacía para evitar un crash.
                _usedGarments.postValue(emptyList())
            }
        }
    }
}
