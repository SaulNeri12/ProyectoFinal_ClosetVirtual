package equipo.closet.closetvirtual.ui.dailyOutfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import equipo.closet.closetvirtual.ClothesSelectionActivity
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.databinding.FragmentDailyOutfitBinding
import equipo.closet.closetvirtual.databinding.OutfitCreationRowDetailedBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import kotlinx.coroutines.launch

class DailyOutfitFragment : Fragment() {

    private var _binding: FragmentDailyOutfitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DailyOutfitViewModel by viewModels {
        // La fábrica necesita el repositorio de outfits, que a su vez necesita el de prendas
        DailyOutfitViewModelFactory(FirebaseOutfitRepository(FirebaseGarmentRepository))
    }
    private var onResultCallback: ((Garment) -> Unit)? = null

    private val selectGarmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val garmentId = result.data?.getStringExtra("SELECTED_GARMENT_ID")
            if (garmentId != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val garment = FirebaseGarmentRepository.getById(garmentId)
                    if (garment != null) {
                        // Ejecuta el callback que guardamos para la fila correcta
                        onResultCallback?.invoke(garment)
                        onResultCallback = null // Limpia el callback para el próximo uso
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyOutfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupSaveObserver()

        // Si la pantalla está vacía, añade la primera fila automáticamente
        if (binding.garmentRowsContainer.childCount == 0) {
            addNewGarmentRow()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.btnAddField.setOnClickListener { addNewGarmentRow() }
        binding.btnUseOutfit.setOnClickListener { viewModel.registerUsageOfSelectedGarments() }
    }

    private fun addNewGarmentRow() {
        // Infla una nueva vista de fila
        val rowBinding = OutfitCreationRowDetailedBinding.inflate(layoutInflater, binding.garmentRowsContainer, false)
        val viewId = View.generateViewId()
        rowBinding.root.id = viewId
        rowBinding.tvGarmentName.text = "Prenda" // Texto inicial

        // El botón de borrar siempre es visible, pero el de añadir no
        rowBinding.btnCleanGarment.visibility = View.VISIBLE
        rowBinding.btnAddGarment.visibility = View.VISIBLE

        // Acción para el botón '+'
        rowBinding.btnAddGarment.setOnClickListener {
            // Abre el selector de ropa y le pasa un callback
            launchClothesSelection { selectedGarment ->
                // Este código se ejecuta cuando se selecciona una prenda
                rowBinding.tvGarmentName.text = selectedGarment.name
                Glide.with(this).load(selectedGarment.imageUri).centerCrop().into(rowBinding.ivGarmentPreview)
                viewModel.addGarment(viewId.toString(), selectedGarment)
                it.visibility = View.GONE // Oculta el '+'
            }
        }

        // Acción para el botón de basura
        rowBinding.btnCleanGarment.setOnClickListener {
            viewModel.removeGarment(viewId.toString())
            binding.garmentRowsContainer.removeView(rowBinding.root)
        }

        // Añade la nueva fila al contenedor
        binding.garmentRowsContainer.addView(rowBinding.root)
    }

    private fun launchClothesSelection(onResult: (Garment) -> Unit) {
        this.onResultCallback = onResult
        val intent = Intent(requireContext(), ClothesSelectionActivity::class.java).apply {
            // Se abre permitiendo seleccionar cualquier categoría
            putExtra("CATEGORY_FILTER", "all")
        }
        selectGarmentLauncher.launch(intent)
    }

    private fun setupSaveObserver() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Uso de prendas registrado", Toast.LENGTH_SHORT).show()
                // Limpia la pantalla para un nuevo registro
                binding.garmentRowsContainer.removeAllViews()
                addNewGarmentRow()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}