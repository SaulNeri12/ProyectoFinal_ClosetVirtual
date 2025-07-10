package equipo.closet.closetvirtual

import OutfitsAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import equipo.closet.closetvirtual.databinding.FragmentSearchOutfitBinding
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

        // CORRECTO: Los listeners para los botones se configuran aquí,
        // una sola vez, cuando la vista del fragmento ya está creada.
        setupListeners()

        // CORRECTO: El observador de datos solo se encarga de actualizar la lista.
        setupObservers()
    }

    private fun setupListeners() {
        // Listener para la barra de búsqueda
        binding.filteredGarmentSearchInput.addTextChangedListener { text ->
            viewModel.search(text.toString())
        }

        // Listener para el botón de crear outfit (+)
        binding.btnNewOutfit.setOnClickListener {
            val intent = Intent(requireContext(), OutfitCreationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.filteredOutfits.observe(viewLifecycleOwner) { outfits ->
            val garmentsMap = viewModel.allGarmentsMap.value
            if (outfits != null && garmentsMap != null) {
                adapter = OutfitsAdapter(requireContext(), outfits, garmentsMap)
                binding.outfitCardsListview.adapter = adapter
            }
        }
    }

    // CORRECTO: onDestroyView es un método de la clase, debe estar fuera de otros métodos.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}