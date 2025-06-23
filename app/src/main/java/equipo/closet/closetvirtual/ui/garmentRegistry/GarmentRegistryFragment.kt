package equipo.closet.closetvirtual.ui.garmentRegistry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import equipo.closet.closetvirtual.R

/**
 * A simple [Fragment] subclass.
 * Use the [GarmentRegistryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GarmentRegistryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_garment_registry, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GarmentRegistryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GarmentRegistryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}