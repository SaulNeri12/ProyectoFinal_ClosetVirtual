package equipo.closet.closetvirtual.ui.clothesSelection

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.ui.garmentRegistry.GarmentRegistryFragment

class ClothesSelectionFragment : Fragment() {


    private lateinit var btnBackClothesSelection: MaterialButton
    private lateinit var btnProfileClothesSelection: MaterialButton
    private lateinit var mcOpenClothesRegistry: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_clothes_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBackClothesSelection = view.findViewById(R.id.btnBackClothesSelection)
        btnProfileClothesSelection = view.findViewById(R.id.btnProfileClothesSelection)
        mcOpenClothesRegistry = view.findViewById(R.id.mcOpenClothesRegistry)

        setBackBehavior()
        setProfileBehavior()
        openRegistryFragment()

    }


    private fun setBackBehavior() : Unit {
        btnBackClothesSelection.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setProfileBehavior() : Unit {
        btnProfileClothesSelection.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openRegistryFragment() {
        mcOpenClothesRegistry.setOnClickListener {
            val fragment = GarmentRegistryFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null) // Permite volver atrás
                .commit()
        }
    }


}