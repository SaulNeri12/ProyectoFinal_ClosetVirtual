package equipo.closet.closetvirtual.ui.outfitCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import equipo.closet.closetvirtual.databinding.FragmentOutfitCreationBinding
import android.widget.ArrayAdapter
import equipo.closet.closetvirtual.R

class OutfitCreationFragment : Fragment() {

    private var _binding: FragmentOutfitCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutfitCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root


val categories = resources.getStringArray(R.array.outfit_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.spinnerOutfitCategory.setAdapter(adapter)


        binding.btnAddGarmentToOutfit.setOnClickListener {
            Toast.makeText(requireContext(), "Add Garment button clicked!", Toast.LENGTH_SHORT).show()

        }


        binding.btnSaveOutfit.setOnClickListener {
            val outfitName = binding.etOutfitName.text.toString()
            val outfitTag = binding.etOutfitTag.text.toString()
            val outfitCategory = binding.spinnerOutfitCategory.text.toString()

if (outfitName.isNotBlank() && outfitCategory.isNotBlank() && outfitName.length <= 20 && outfitTag.length <= 20) {
                Toast.makeText(requireContext(), "Outfit '$outfitName' saved!", Toast.LENGTH_LONG).show()

            } else {
                var errorMessage = "Please enter outfit name and select a category."
                if (outfitName.length > 20 || outfitTag.length > 20) {
                    errorMessage = "Outfit name and tag must be less than 20 characters."
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
