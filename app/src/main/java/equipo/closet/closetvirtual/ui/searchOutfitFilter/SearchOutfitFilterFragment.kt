package equipo.closet.closetvirtual.ui.searchOutfitFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionFilterBinding

class SearchOutfitFilterFragment : BottomSheetDialogFragment(){

    val etiquetas = listOf(
        "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
        "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
    )

    private lateinit var binding: FragmentClothesSelectionFilterBinding
    private lateinit var viewModel: SearchOutfitFilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SearchOutfitFilterViewModel::class.java]
        binding = FragmentClothesSelectionFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        viewModel = ViewModelProvider(activity)[SearchOutfitFilterViewModel::class.java]

        setChipGroupData()
        setSelectedChips()
        setSaveButtonBehavior()
    }

    private fun setChipGroupData() {

        val chipGroup = binding.chipGroupTags

        etiquetas.forEach { etiqueta ->
            val chip = Chip(requireContext()).apply {
                text = etiqueta
                isCheckable = true
                isClickable = true
            }
            chipGroup.addView(chip)
        }
    }

    private fun setSelectedChips(){
        val selectedTags = viewModel.tags.value

        //make a loop to get the selected tags
        for (i in 0 until  binding.chipGroupTags.childCount) {
            val chip =  binding.chipGroupTags.getChildAt(i) as Chip
            //verify if the tag is in the selected tags
            if (selectedTags?.contains(chip.text.toString()) == true) {
                chip.isChecked = true
            }
        }
    }

    private fun setSaveButtonBehavior(){
        binding.btnSave.setOnClickListener {

            val selectedTags = mutableListOf<String>()

            //make a loop to get the selected tags
            for (i in 0 until  binding.chipGroupTags.childCount) {
                val chip =  binding.chipGroupTags.getChildAt(i) as Chip
                if (chip.isChecked) {
                    selectedTags.add(chip.text.toString())
                }
            }

            //save it on the view model
            viewModel.setTags(selectedTags)
            viewModel.triggerSearchEvent()
            dismiss()
        }
    }

}