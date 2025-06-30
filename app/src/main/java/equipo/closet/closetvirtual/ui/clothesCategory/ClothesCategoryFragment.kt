package equipo.closet.closetvirtual.ui.clothesCategory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import equipo.closet.closetvirtual.R
import equipo.closet.closetvirtual.ProfileActivity
import equipo.closet.closetvirtual.databinding.FragmentClothesCategoryBinding
import equipo.closet.closetvirtual.repositories.factories.GarmentRepositoryFactory
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import equipo.closet.closetvirtual.ui.clothesCategory.adapters.ClothesCategoryGridAdapter
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryViewModel
import equipo.closet.closetvirtual.utils.AnimationHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // ← IMPORT AGREGADO
import java.util.*
import equipo.closet.closetvirtual.entities.Garment

class ClothesCategoryFragment : Fragment() {

    private lateinit var binding: FragmentClothesCategoryBinding
    private lateinit var viewModel : ClothesCategoryViewModel

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tag: String

    private val clothesRepository: Repository<Garment, Int> = GarmentRepositoryFactory.create()

    private lateinit var headerCardView: MaterialCardView
    private lateinit var searchFilterCardView: MaterialCardView
    private lateinit var topClothesCounter: MaterialTextView
    private lateinit var bottomClothesCounter: MaterialTextView
    private lateinit var bodysuitClothesCounter: MaterialTextView
    private lateinit var shoesClothesCounter: MaterialTextView
    private lateinit var accessoriesClothesCounter: MaterialTextView
    private lateinit var btnBack: MaterialButton
    private lateinit var btnProfile: MaterialButton

    // Lists para almacenar las cards de categorías
    private val categoryCards = mutableListOf<MaterialCardView>()
    private val gridViews = mutableListOf<GridView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ClothesCategoryViewModel::class.java]

        binding = FragmentClothesCategoryBinding.inflate(inflater, container, false)
        activity?.findViewById<View>(R.id.bottom_nav_card)?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setViewModelObserver()
        setupClickListeners()
        startAnimations()

        lifecycleScope.launch {
            val clothes = clothesRepository.getAll().toMutableList()

            // === Top ===
            val topClothes = clothes.filter { it.category.lowercase(Locale.getDefault()) == "top" }
            binding.topClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), topClothes.toMutableList())
            binding.topClothesCounterLabel.text = formatCategoryClothesCount(topClothes.size)

            // === Bottom ===
            val bottomClothes = clothes.filter { it.category.lowercase(Locale.getDefault()) == "bottom" }
            binding.bottomClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), bottomClothes.toMutableList())
            binding.bottomClothesCounterLabel.text = formatCategoryClothesCount(bottomClothes.size)

            // === Zapatos ===
            val shoesClothes = clothes.filter { it.category.lowercase(Locale.getDefault()) == "zapatos" }
            binding.shoesClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), shoesClothes.toMutableList())
            binding.shoesClothesCounterLabel.text = formatCategoryClothesCount(shoesClothes.size)

            // === Bodysuit ===
            val bodysuitClothes = clothes.filter { it.category.lowercase(Locale.getDefault()) == "bodysuit" }
            binding.bodysuitClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), bodysuitClothes.toMutableList())
            binding.bodysuitClothesCounterLabel.text = formatCategoryClothesCount(bodysuitClothes.size)

            // === Accesorios ===
            val accessoriesClothes = clothes.filter { it.category.lowercase(Locale.getDefault()) == "accesorios" }
            binding.accessoriesClothesCardsGrid.adapter =
                ClothesCategoryGridAdapter(requireContext(), accessoriesClothes.toMutableList())
            binding.accessoriesClothesCounterLabel.text = formatCategoryClothesCount(accessoriesClothes.size)

        }

        animateGridItems()
    }

    private fun initializeViews(view: View) {
        headerCardView = view.findViewById(R.id.header_card_view)
        searchFilterCardView = view.findViewById(R.id.search_filter_card_view)
        topClothesCounter = view.findViewById(R.id.top_clothes_counter_label)
        bottomClothesCounter = view.findViewById(R.id.bottom_clothes_counter_label)
        bodysuitClothesCounter = view.findViewById(R.id.bodysuit_clothes_counter_label)
        shoesClothesCounter = view.findViewById(R.id.shoes_clothes_counter_label)
        accessoriesClothesCounter = view.findViewById(R.id.accessories_clothes_counter_label)
        btnBack = view.findViewById(R.id.btnBackCategoryCards)
        btnProfile = view.findViewById(R.id.btnProfileCategoryCards)

        // Agregar las cards de categorías a la lista para animaciones
        categoryCards.addAll(listOf(
            view.findViewById(R.id.header_card_view),
            view.findViewById(R.id.search_filter_card_view),

        ))

        // Agregar los GridViews para animaciones individuales de items
        gridViews.addAll(listOf(
            view.findViewById(R.id.top_clothes_cards_grid),
            view.findViewById(R.id.bottom_clothes_cards_grid),
            view.findViewById(R.id.bodysuit_clothes_cards_grid),
            view.findViewById(R.id.shoes_clothes_cards_grid),
            view.findViewById(R.id.accessories_clothes_cards_grid)
        ))
    }

    private fun setViewModelObserver(){
        viewModel.tag.observe(viewLifecycleOwner) {
            this.tag = viewModel.tag.value.toString()
        }
    }

    private fun setupClickListeners() {
        // Animación en botones con feedback táctil
        btnBack.setOnClickListener {
            AnimationHelper.animateButtonPress(it) {
                // Acción del botón después de la animación
                @Suppress("DEPRECATION")
                requireActivity().onBackPressed()
            }
        }

        btnProfile.setOnClickListener {
            AnimationHelper.animateButtonPress(it) {
                // Navegar al perfil
                navigateToProfile()
            }
        }

        binding.btnSearchClothesCategory.setOnClickListener {
            // TODO: Implement search behavior
        }

        binding.btnFilterClothesCategory.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesCategoryFilterFragment")
        }

        // Animaciones hover para las cards de categorías
        setupCardHoverAnimations()
    }

    private fun setupCardHoverAnimations() {
        val categoryCards = categoryCards // Use the initialized list

        categoryCards.forEach { cardView ->
            cardView?.let { card ->
                val originalElevation = card.cardElevation

                card.setOnTouchListener { v, event ->
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> {
                            AnimationHelper.animateCardHover(v)
                        }
                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            AnimationHelper.animateCardUnhover(v, originalElevation)
                        }
                    }
                    false
                }
            }
        }
    }

    private fun startAnimations() {
        lifecycleScope.launch {
            // Animar header primero
            AnimationHelper.animateHeaderSlideIn(headerCardView)

            delay(200)

            // Animar barra de búsqueda
            AnimationHelper.animateSearchBarExpand(searchFilterCardView)

            delay(300)

            // Animar entrada de cards de categorías
            animateCategoryCards()
        }
    }

    private fun animateCategoryCards() {
        AnimationHelper.animateGridItemsSequentially(categoryCards)
    }

    private fun animateCounters() {
        // Simular conteo animado de prendas
        AnimationHelper.animateCounterUpdate(topClothesCounter, 0, topClothesCounter.text.toString().toIntOrNull() ?: 0)
        lifecycleScope.launch {
            delay(100)
            AnimationHelper.animateCounterUpdate(bottomClothesCounter, 0, bottomClothesCounter.text.toString().toIntOrNull() ?: 0)
            delay(100)
            AnimationHelper.animateCounterUpdate(bodysuitClothesCounter, 0, bodysuitClothesCounter.text.toString().toIntOrNull() ?: 0)
            delay(100)
            AnimationHelper.animateCounterUpdate(shoesClothesCounter, 0, shoesClothesCounter.text.toString().toIntOrNull() ?: 0)
            delay(100)
            AnimationHelper.animateCounterUpdate(accessoriesClothesCounter, 0, accessoriesClothesCounter.text.toString().toIntOrNull() ?: 0)
        }
    }

    private fun animateGridItems() {
        // Animar items individuales de cada grid
        gridViews.forEach { gridView ->
            // Aquí implementarías la animación de los items del grid
            // cuando se cargan desde el adapter
            animateGridViewItems(gridView)
        }
    }

    private fun animateGridViewItems(gridView: GridView) {
        // Esta función se llamaría cuando el adapter tenga datos
        lifecycleScope.launch {
            delay(100)
            for (i in 0 until gridView.childCount) {
                val child = gridView.getChildAt(i)
                child?.let { view ->
                    AnimationHelper.animateCardEntrance(view, i * 50L)
                }
            }
        }
    }

    // Función para cuando se actualiza el número de prendas
    fun updateClothesCount(category: String, newCount: Int) {
        when (category) {
            "top" -> {
                val currentCount = topClothesCounter.text.toString().toIntOrNull() ?: 0
                AnimationHelper.animateCounterUpdate(topClothesCounter, currentCount, newCount)
            }
            "bottom" -> {
                val currentCount = bottomClothesCounter.text.toString().toIntOrNull() ?: 0
                AnimationHelper.animateCounterUpdate(bottomClothesCounter, currentCount, newCount)
            }
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        startActivity(intent)
    }

    // Función para animar la transición cuando se sale del fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Opcional: Animar salida si es necesario
    }

    // Función para animar nuevos items cuando se agregan
    fun addNewItemWithAnimation(view: View) {
        AnimationHelper.animateCardEntrance(view)
    }

    // Función para mostrar animación de pulso en elementos importantes
    fun highlightImportantElement(view: View) {
        AnimationHelper.animatePulse(view, 2000)
    }

    private fun formatCategoryClothesCount(count: Int): String {
        return when {
            count <= 0 -> "0"
            count > 100 -> "100+"
            else -> count.toString()
        }
    }
}
