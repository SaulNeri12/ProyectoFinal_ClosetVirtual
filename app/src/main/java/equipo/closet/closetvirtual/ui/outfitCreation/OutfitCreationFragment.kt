package equipo.closet.closetvirtual.ui.outfitCreation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentOutfitCreationBinding

class OutfitCreationFragment : Fragment() {

    private lateinit var binding: FragmentOutfitCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentOutfitCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initialize the fragment initialized variables here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setChipGroupData()
        setBackBehavior()
        setProfileBehavior()
        useOutfit()

    }

    private fun useOutfit(){
        binding.btnUseOutfit.setOnClickListener {
            if(validateFields()){
                Toast.makeText(requireContext(), "Prenda seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        val name = binding.etOutfitName.text.toString()
        val tags = binding.chipGroupTags.checkedChipIds

        if(name.isEmpty()){
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }
        if(tags.isEmpty()){
            Toast.makeText(requireContext(), "No has seleccionado ninguna etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        if(tags.size > 5) {
            Toast.makeText(requireContext(), "No puedes seleccionar más de 5 etiquetas", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setBackBehavior() : Unit {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setProfileBehavior() : Unit {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )

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

    private fun getTags(): List<String> {
        val selectedTags = mutableListOf<String>()

        //make a loop to get the selected tags
        for (i in 0 until  binding.chipGroupTags.childCount) {
            val chip =  binding.chipGroupTags.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedTags.add(chip.text.toString())
            }
        }

        return selectedTags
    }

}
