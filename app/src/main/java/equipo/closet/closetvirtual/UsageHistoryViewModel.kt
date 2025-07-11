package equipo.closet.closetvirtual.ui.usageHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.global.ClothesCache
import equipo.closet.closetvirtual.objects.SessionManager
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
                val usages = GarmentUsageTracker.getUsedClothes(SessionManager.user.uid)

                val clothes = mutableListOf<Garment>()

                for (usage in usages) {
                    val garment = FirebaseGarmentRepository.getById(usage.garmentId)
                    if (garment != null) {
                        clothes.add(garment)
                    }
                }

                // SE GUARDO LA LISTA DE PRENDAS EN LA CACHE PARA QUE NO TENGAS
                // QUE VOLVER A MANDAR A LLAMAR AL REPOSITORIO DE PRENDAS
                ClothesCache.setGarments(clothes)

                /**
                 * DAMIAN: YA TU SABE QUE HACER AQUI PARA MOSTRAR LAS PRENDAS USADAS EL DIA SELECCIONADO
                 *
                 * RECUERDA QUE TE TRAE TODAS LAS PRENDAS QUE UN USUARIO TIENE, AHI TIENES QUE FILTRAR
                 * TU POR EL DIA SELECCIONADO
                 */

                _usedGarments.postValue(clothes)
            } catch (e: Exception) {
                // Manejar el error
                _usedGarments.postValue(emptyList())
            }
        }
    }
}
