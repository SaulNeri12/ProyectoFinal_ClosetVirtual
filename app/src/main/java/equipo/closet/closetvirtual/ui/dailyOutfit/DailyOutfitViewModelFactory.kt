package equipo.closet.closetvirtual.ui.dailyOutfit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository

class DailyOutfitViewModelFactory(
    private val outfitRepository: FirebaseOutfitRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyOutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyOutfitViewModel(outfitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}