package equipo.closet.closetvirtual


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding // Importa el ViewBinding
import equipo.closet.closetvirtual.ui.searchOutfit.SearchOutfitViewModel

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

        // Observar la lista de outfits filtrados
        viewModel.filteredOutfits.observe(viewLifecycleOwner) { outfits ->
            // También necesitamos el mapa de prendas para que el adapter funcione
            val garmentsMap = viewModel.allGarmentsMap.value
            if (outfits != null && garmentsMap != null) {
                adapter = OutfitsAdapter(requireContext(), outfits, garmentsMap)
                binding.outfitCardsListview.adapter = adapter
            }
        }

        // Configurar el listener para la búsqueda
        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            viewModel.search(text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evitar fugas de memoria
    }
}