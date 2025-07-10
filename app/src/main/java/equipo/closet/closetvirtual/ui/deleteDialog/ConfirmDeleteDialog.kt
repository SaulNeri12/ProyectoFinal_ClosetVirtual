package equipo.closet.closetvirtual.ui.deleteDialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import equipo.closet.closetvirtual.databinding.DialogConfirmDeleteBinding

class ConfirmDeleteDialog : DialogFragment() {

    private lateinit var binding: DialogConfirmDeleteBinding
    private lateinit var viewModel: ConfirmDeleteViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogConfirmDeleteBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[ConfirmDeleteViewModel::class.java]

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            viewModel.triggerDelete()
            dismiss()
        }

        return dialog
    }

}
