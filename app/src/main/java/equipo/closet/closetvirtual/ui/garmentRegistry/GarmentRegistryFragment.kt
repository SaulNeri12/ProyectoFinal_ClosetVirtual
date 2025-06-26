package equipo.closet.closetvirtual.ui.garmentRegistry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.R

class GarmentRegistryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_garment_registry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnProfile: MaterialButton = view.findViewById(R.id.btn_profile)


    }
}
