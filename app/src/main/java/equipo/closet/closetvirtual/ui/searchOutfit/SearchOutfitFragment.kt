package equipo.closet.closetvirtual.ui.searchOutfit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import equipo.closet.closetvirtual.OutfitCreationActivity
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding
import equipo.closet.closetvirtual.entities.Outfit
import equipo.closet.closetvirtual.repositories.factories.OutfitRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.searchOutfit.adapters.OutfitSearchListAdapter
import equipo.closet.closetvirtual.ui.searchOutfitFilter.SearchOutfitFilterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchOutfitFragment : Fragment() {

    private lateinit var binding: FragmentSearchOutfitBinding
    private lateinit var viewModel: SearchOutfitFilterViewModel

    private val outfitRepository: Repository<Outfit, String> = OutfitRepositoryFactory.create()

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tags: List<String>

    //Job for the search
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[SearchOutfitFilterViewModel::class.java]
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        binding = FragmentSearchOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadOutfits(view)
        setBackBehavior()
        setProfileBehavior()
        showFilterFragment()
        setViewModelObserver()
        setSearchButtonBehavior()
        openNewOutfitActivity()
        setRealTimeSearchByOutfitName()
        setSearchEventObserver()

    }

    private fun loadOutfits(view: View): Unit {
        lifecycleScope.launch {
            val outfits = outfitRepository.getAll()

            val outfitList: ListView = view.findViewById(R.id.outfit_cards_listview)
            outfitList.adapter = OutfitSearchListAdapter(requireContext(), outfits.toMutableList())
        }
    }

    private fun showFilterFragment(): Unit {
        binding.btnFilter.setOnClickListener {
            ClothesCategoryFilterFragment().show(
                childFragmentManager,
                "NewClothesSelectionFilterFragment"
            )
        }
    }

    private fun setViewModelObserver(): Unit {
        viewModel.tags.observe(viewLifecycleOwner) {
            this.tags = viewModel.tags.value as MutableList<String>
        }
    }

    private fun setSearchButtonBehavior(): Unit {
        binding.btnSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Search button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNewOutfitActivity(): Unit {
        binding.btnNewOutfit.setOnClickListener {
            val intent = Intent(requireContext(), OutfitCreationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBackBehavior(): Unit {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setProfileBehavior(): Unit {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setRealTimeSearchByOutfitName() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500L)
                    //this is where we search for the outfits
                }
            }
        })
    }

    private fun setSearchEventObserver(): Unit {
        viewModel.searchEvent.observe(viewLifecycleOwner) {
            //Here we trigger the search
            Log.d("ClothesCategoryFragment", "Search event triggered")
            searchOutfitEvent()
        }
    }

    private fun searchOutfitEvent() : Unit{
        lifecycleScope.launch {
            val searchText = binding.etSearch.toString().trim()
            // Add the function when ready
        }
    }

}
