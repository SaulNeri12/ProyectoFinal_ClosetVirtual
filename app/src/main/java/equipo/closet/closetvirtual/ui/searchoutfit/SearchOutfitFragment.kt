package equipo.closet.closetvirtual.ui.searchoutfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.R

class SearchOutfitFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.GONE


        return inflater.inflate(R.layout.search_outfit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnProfile: MaterialButton = view.findViewById(R.id.btn_profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
    }
}
