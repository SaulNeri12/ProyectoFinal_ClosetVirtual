package equipo.closet.closetvirtual.ui.dialogLogout

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import equipo.closet.closetvirtual.databinding.DialogConfirmLogoutBinding

class ConfirmLogoutDialog : DialogFragment() {

    private lateinit var binding: DialogConfirmLogoutBinding
    private lateinit var viewModel: ConfirmLogoutViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogConfirmLogoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[ConfirmLogoutViewModel::class.java]

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


