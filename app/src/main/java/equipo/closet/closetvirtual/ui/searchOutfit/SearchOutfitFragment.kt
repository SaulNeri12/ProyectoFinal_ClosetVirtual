package equipo.closet.closetvirtual.ui.searchOutfit

import OutfitsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding

class SearchOutfitFragment : Fragment() {

    private var _binding: FragmentSearchOutfitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchOutfitViewModel by viewModels()
    private var adapter: OutfitsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.filteredOutfits.observe(viewLifecycleOwner) { outfits ->
            val garmentsMap = viewModel.allGarmentsMap.value
            if (outfits != null && garmentsMap != null) {
                adapter = OutfitsAdapter(requireContext(), outfits, garmentsMap)
                binding.outfitCardsListview.adapter = adapter
            }
        }


        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            viewModel.search(text.toString())
        }

        binding.btnNewOutfit.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_outfit_finder_to_outfit_creation_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
