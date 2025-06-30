package equipo.closet.closetvirtual.ui.dailyOutfit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionBinding
import equipo.closet.closetvirtual.databinding.FragmentDailyOutfitBinding

class DailyOutfitFragment : Fragment() {

    private lateinit var binding: FragmentDailyOutfitBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentDailyOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initialize the fragment initialized variables here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        val tag = binding.etOutfitTag.text.toString()

        if(name.isEmpty()){
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }
        if(tag.isEmpty()){
            binding.etOutfitTag.error = "La etiqueta no puede estar vacía"
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


}