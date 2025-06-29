package equipo.closet.closetvirtual.ui.clothesCategoryFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryFilterBinding

class ClothesCategoryFilterFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentClothesCategoryFilterBinding
    private lateinit var viewModel: ClothesCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClothesCategoryFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        viewModel = ViewModelProvider(activity)[ClothesCategoryViewModel::class.java]

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