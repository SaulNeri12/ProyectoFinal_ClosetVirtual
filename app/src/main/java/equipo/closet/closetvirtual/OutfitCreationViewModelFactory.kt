package equipo.closet.closetvirtual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository

class OutfitCreationViewModelFactory(
    private val outfitRepository: FirebaseOutfitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitCreationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitCreationViewModel(outfitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}