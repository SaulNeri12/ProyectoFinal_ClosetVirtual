package equipo.closet.closetvirtual.ui.clothes_category_cards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import equipo.closet.closetvirtual.LoginActivity
import equipo.closet.closetvirtual.R

class ClothesCategoryCardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE


        return inflater.inflate(R.layout.fragment_clothes_category_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnProfile: ImageView = view.findViewById(R.id.btnProfile)
        val btnLogout: ImageView = view.findViewById(R.id.btnLogout)


        btnProfile.setOnClickListener {

            findNavController().navigate(R.id.action_global_to_profileFragment)
        }


        btnLogout.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}