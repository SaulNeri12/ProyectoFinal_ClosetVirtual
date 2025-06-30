package equipo.closet.closetvirtual.ui.clothesSelection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentClothesSelectionBinding
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesSelectionFilter.ClothesSelectionViewModel
import equipo.closet.closetvirtual.ui.garmentRegistry.GarmentRegistryFragment

class ClothesSelectionFragment : Fragment() {


    private lateinit var binding : FragmentClothesSelectionBinding
    private lateinit var viewModel: ClothesSelectionViewModel

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tag: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ClothesSelectionViewModel::class.java]

        binding = FragmentClothesSelectionBinding.inflate(inflater, container, false)
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackBehavior()
        setProfileBehavior()
        openRegistryFragment()
        setFilterButtonBehavior()
        setViewModelObserver()

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

    private fun openRegistryFragment() {
        binding.btnOpenClothesRegistry.setOnClickListener {
            val fragment = GarmentRegistryFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null) // Permite volver atrás
                .commit()
        }
    }

    private fun setFilterButtonBehavior() {
        binding.btnFilter.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesSelectionFilterFragment")
        }
    }

    private fun setViewModelObserver(){
        viewModel.tag.observe(viewLifecycleOwner) {
            this.tag = viewModel.tag.value.toString()
        }
    }


}