package equipo.closet.closetvirtual.ui.clothesCategoryFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryFilterBinding
import equipo.closet.closetvirtual.utils.ChipGroupStyler

class ClothesCategoryFilterFragment : BottomSheetDialogFragment() {

    val etiquetas = listOf(
        "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
        "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
    )

    private lateinit var binding: FragmentClothesCategoryFilterBinding
    private lateinit var viewModel: ClothesCategoryFilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClothesCategoryFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClothesCategoryFilterViewModel::class.java]

        setChipGroupData()
        setSelectedChips()
        setSaveButtonBehavior()
    }

    private fun setChipGroupData() {
        val chipGroup = binding.chipGroupTags

        etiquetas.forEach { etiqueta ->
            ChipGroupStyler.addStyledChip(requireContext(), chipGroup, etiqueta, ChipGroupStyler.ChipStyle.SOFT_GRAY, true)
        }
        ChipGroupStyler.animateChipsStaggered(chipGroup)
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
            //shoot the event
            viewModel.shootSearchEvent()
            //close the fragment
            dismiss()
        }
    }

}
