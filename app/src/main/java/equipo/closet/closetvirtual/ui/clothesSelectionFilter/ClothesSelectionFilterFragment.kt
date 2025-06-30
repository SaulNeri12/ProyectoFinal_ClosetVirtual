package equipo.closet.closetvirtual.ui.clothesSelectionFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionFilterBinding

class ClothesSelectionFilterFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentClothesSelectionFilterBinding
    private lateinit var viewModel: ClothesSelectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentClothesSelectionFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        viewModel = ViewModelProvider(activity)[ClothesSelectionViewModel::class.java]

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