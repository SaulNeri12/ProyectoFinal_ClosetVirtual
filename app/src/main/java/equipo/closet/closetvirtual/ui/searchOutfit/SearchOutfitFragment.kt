package equipo.closet.closetvirtual.ui.searchOutfit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.searchOutfit.adapters.OutfitSearchListAdapter
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitViewModel
import kotlinx.coroutines.launch


class SearchOutfitFragment : Fragment() {

    private lateinit var binding: FragmentSearchOutfitBinding
    private lateinit var viewModel: SearchOutfitViewModel

    private val outfitRepository: Repository<Outfit, String> = OutfitRepositoryFactory.create()

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tags: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[SearchOutfitViewModel::class.java]
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentSearchOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Load outfits
        loadOutfits(view)
        setBackBehavior()
        setProfileBehavior()
        setFilterButtonBehavior()
        setViewModelObserver()
        setSearchButtonBehavior()
        setOpenNewOutfitButtonBehavior()

    }

    private fun loadOutfits(view : View){
        lifecycleScope.launch {
            val outfits = outfitRepository.getAll()

            val outfitList: ListView = view.findViewById(R.id.outfit_cards_listview)
            outfitList.adapter = OutfitSearchListAdapter(requireContext(), outfits.toMutableList())
        }
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

    private fun setFilterButtonBehavior() {
        binding.btnFilter.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesSelectionFilterFragment")
        }
    }

    private fun setViewModelObserver(){
        viewModel.tags.observe(viewLifecycleOwner) {
            this.tags = viewModel.tags.value as MutableList<String>
        }
    }

    private fun setSearchButtonBehavior() {
        binding.btnSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Search button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOpenNewOutfitButtonBehavior() {
        binding.btnNewOutfit.setOnClickListener {
            Toast.makeText(requireContext(), "Open new outfit button clicked", Toast.LENGTH_SHORT).show()
        }
    }

}
