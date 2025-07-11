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
import equipo.closet.closetvirtual.ProfileActivity
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
                        onResultCallback?.invoke(garment)
                        onResultCallback = null
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
        handleOnProfileButtonClicked()
        handleOnBackButtonClicked()

        if (binding.garmentRowsContainer.childCount == 0) {
            addNewGarmentRow()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.btnUseOutfit.setOnClickListener { viewModel.registerUsageOfSelectedGarments() }


        binding.btnAddField.setOnClickListener {
            if (binding.garmentRowsContainer.childCount < 10) {
                addNewGarmentRow()
            } else {
                Toast.makeText(requireContext(), "No puedes añadir más de 10 prendas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewGarmentRow() {
        val rowBinding = OutfitCreationRowDetailedBinding.inflate(layoutInflater, binding.garmentRowsContainer, false)
        val viewId = View.generateViewId()
        rowBinding.root.id = viewId
        rowBinding.tvGarmentName.text = "Seleccionar prenda"

        rowBinding.btnCleanGarment.visibility = View.VISIBLE
        rowBinding.btnAddGarment.visibility = View.VISIBLE

        rowBinding.btnAddGarment.setOnClickListener {
            launchClothesSelection { selectedGarment ->
                rowBinding.tvGarmentName.text = selectedGarment.name
                Glide.with(this).load(selectedGarment.imageUri).centerCrop().into(rowBinding.ivGarmentPreview)
                viewModel.addGarment(viewId.toString(), selectedGarment)
                it.visibility = View.GONE
            }
        }

        rowBinding.btnCleanGarment.setOnClickListener {
            viewModel.removeGarment(viewId.toString())
            binding.garmentRowsContainer.removeView(rowBinding.root)
        }

        binding.garmentRowsContainer.addView(rowBinding.root)
    }

    private fun launchClothesSelection(onResult: (Garment) -> Unit) {
        this.onResultCallback = onResult
        val intent = Intent(requireContext(), ClothesSelectionActivity::class.java).apply {
            putExtra("CATEGORY_FILTER", "all")
        }
        selectGarmentLauncher.launch(intent)
    }

    private fun setupSaveObserver() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Uso de prendas registrado", Toast.LENGTH_SHORT).show()
                binding.garmentRowsContainer.removeAllViews()
                addNewGarmentRow()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleOnProfileButtonClicked() {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleOnBackButtonClicked() {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}