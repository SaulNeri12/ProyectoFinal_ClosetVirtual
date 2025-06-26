package equipo.closet.closetvirtual.ui.searchoutfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.searchoutfit.adapters.OutfitSearchListAdapter

class SearchOutfitFragment : Fragment() {

    private val outfitRepository: Repository<Outfit, Int> = OutfitRepositoryFactory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.GONE

        return inflater.inflate(R.layout.fragment_search_outfit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnProfile: MaterialButton = view.findViewById(R.id.btn_profile)

        val outfits = outfitRepository.getAll()

        val outfitList: ListView = view.findViewById(R.id.outfit_cards_listview)
        outfitList.adapter = OutfitSearchListAdapter(requireContext(), outfits.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
    }
}
