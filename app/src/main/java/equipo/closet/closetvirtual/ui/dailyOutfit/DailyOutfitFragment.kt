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
import com.google.android.material.chip.Chip
import equipo.closet.closetvirtual.ClothesSelectionActivity
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.databinding.FragmentDailyOutfitBinding
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.FirebaseOutfitRepository
import equipo.closet.closetvirtual.utils.ChipGroupStyler
import kotlinx.coroutines.launch

class DailyOutfitFragment : Fragment() {

    private var _binding: FragmentDailyOutfitBinding? = null
    private val binding get() = _binding!!

    // Usamos el ViewModel y su Factory para el "Daily Outfit"
    private val viewModel: DailyOutfitViewModel by viewModels {
        DailyOutfitViewModelFactory(FirebaseOutfitRepository(FirebaseGarmentRepository))
    }

    // Launcher para recibir la prenda seleccionada de ClothesSelectionActivity
    private val selectGarmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val garmentId = result.data?.getStringExtra("SELECTED_GARMENT_ID")
            val category = result.data?.getStringExtra("CATEGORY_FILTER")

            if (garmentId != null && category != null) {
                // Buscamos la prenda completa en la base de datos
                viewLifecycleOwner.lifecycleScope.launch {
                    val garment = FirebaseGarmentRepository.getById(garmentId)
                    if (garment != null) {
                        viewModel.addGarment(category, garment)
                        updateUiForRow(category, garment)
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
        setChipGroupData()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.btnProfile.setOnClickListener { startActivity(Intent(requireContext(), ProfileActivity::class.java)) }

        // Listeners para cada botón de selección de ropa
        binding.btnShirt.setOnClickListener { launchClothesSelection("Top") }
        binding.btnPant.setOnClickListener { launchClothesSelection("Bottom") }
        binding.btnBodySuit.setOnClickListener { launchClothesSelection("Bodysuit") }
        binding.btnShoes.setOnClickListener { launchClothesSelection("Zapato") }
        binding.btnAccesory.setOnClickListener { launchClothesSelection("Accesorio") }

        binding.btnUseOutfit.setOnClickListener { useOutfit() }
    }

    private fun launchClothesSelection(category: String) {
        val intent = Intent(requireContext(), ClothesSelectionActivity::class.java).apply {
            putExtra("CATEGORY_FILTER", category)
        }
        selectGarmentLauncher.launch(intent)
    }

    private fun updateUiForRow(category: String, garment: Garment) {
        val (imageView, textView) = when (category) {
            "Top" -> binding.ivShirtPreview to binding.tvShirtName
            "Bottom" -> binding.ivPantPreview to binding.tvPantName
            "Bodysuit" -> binding.ivBodysuitPreview to binding.tvBodysuitName
            "Zapato" -> binding.ivShoesPreview to binding.tvShoesName
            "Accesorio" -> binding.ivAccesoryPreview to binding.tvAccesoryName
            else -> null to null
        }

        imageView?.let {
            Glide.with(this).load(garment.imageUri).centerCrop().into(it)
        }
        textView?.text = garment.name
    }

    private fun useOutfit() {
        if (validateFields()) {
            val name = binding.etOutfitName.text.toString().trim()
            val tags = getSelectedTagsFromChipGroup()
            viewModel.saveDailyOutfit(name, tags)
        }
    }

    private fun setupSaveObserver() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Outfit del día guardado!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSelectedTagsFromChipGroup(): List<String> {
        return binding.chipGroupTags.checkedChipIds.mapNotNull { chipId ->
            view?.findViewById<Chip?>(chipId)?.text.toString()
        }
    }

    private fun setChipGroupData() {
        val etiquetas = listOf(
            "Casual", "Formal", "Verano", "Invierno", "Elegante", "Fiesta",
            "Trabajo", "Deportivo", "Playa", "Noche", "Vintage", "Minimalista"
        )
        etiquetas.forEach { etiqueta ->
            ChipGroupStyler.addStyledChip(requireContext(), binding.chipGroupTags, etiqueta, ChipGroupStyler.ChipStyle.VIBRANT_COLORS, true)
        }
        ChipGroupStyler.animateChipsStaggered(binding.chipGroupTags)
    }

    private fun validateFields(): Boolean {
        val name = binding.etOutfitName.text.toString().trim()
        val tags = binding.chipGroupTags.checkedChipIds

        if(name.isEmpty()){
            binding.etOutfitName.error = "El nombre no puede estar vacío"
            return false
        }
        if(tags.isEmpty()){
            Toast.makeText(requireContext(), "No has seleccionado ninguna etiqueta", Toast.LENGTH_SHORT).show()
            return false
        }
        if(tags.size > 5) {
            Toast.makeText(requireContext(), "No puedes seleccionar más de 5 etiquetas", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
