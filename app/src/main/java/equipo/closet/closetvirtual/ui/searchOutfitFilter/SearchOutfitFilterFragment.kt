package equipo.closet.closetvirtual.ui.searchOutfitFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionFilterBinding

class SearchOutfitFilterFragment : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentClothesSelectionFilterBinding
    private lateinit var viewModel: SearchOutfitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SearchOutfitViewModel::class.java]
        binding = FragmentClothesSelectionFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        viewModel = ViewModelProvider(activity)[SearchOutfitViewModel::class.java]

        setTag()
        setSaveButtonBehavior()
    }

    private fun setSaveButtonBehavior(){
        binding.btnSave.setOnClickListener {
            viewModel.tag.value = binding.etTag.text?.toString() ?: ""
            dismiss()
        }
    }

    private fun setTag(){
        binding.etTag.setText(viewModel.tag.value ?: "")
    }
}