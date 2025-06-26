package equipo.closet.closetvirtual.ui.clothes_category_cards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.LoginActivity
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R

class ClothesCategoryCardsFragment : Fragment() {

    private lateinit var btnBackCategoryCards: MaterialButton
    private lateinit var btnProfileCategoryCards: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_clothes_category_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBackCategoryCards = view.findViewById(R.id.btnBackCategoryCards)
        btnProfileCategoryCards = view.findViewById(R.id.btnProfileCategoryCards)

        setBackButtonClickListener()
        setProfileButtonClickListener()

    }

    private fun setProfileButtonClickListener() {
        btnProfileCategoryCards.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBackButtonClickListener() {
        btnBackCategoryCards.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

}
