package equipo.closet.closetvirtual.ui.clothes_category_cards

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import equipo.closet.closetvirtual.R

class ClothesCategoryCardsFragment : Fragment() {

    companion object {
        fun newInstance() = ClothesCategoryCardsFragment()
    }

    private val viewModel: ClothesCategoryCardsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_clothes_category_cards, container, false)
    }
}