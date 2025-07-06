package equipo.closet.closetvirtual

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import equipo.closet.closetvirtual.databinding.ActivityClothesSelectionBinding
import equipo.closet.closetvirtual.ui.clothesCategoryFilter.ClothesCategoryFilterFragment
import equipo.closet.closetvirtual.ui.clothesSelectionFilter.ClothesSelectionViewModel

class ClothesSelectionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClothesSelectionBinding
    private lateinit var viewModel: ClothesSelectionViewModel

    //this is where we save the tag gotten from the filter fragment
    private lateinit var tags: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothesSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackBehavior()
        setProfileBehavior()
        openRegistryFragment()
        handleShowTagsFilter()
        setViewModelObserver()
    }

    private fun setBackBehavior() : Unit {
        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            this.onBackPressed()
        }
    }

    private fun setProfileBehavior() : Unit {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openRegistryFragment() : Unit {
        binding.btnOpenClothesRegistry.setOnClickListener {

        }
    }

    private fun handleShowTagsFilter() : Unit {
        binding.btnFilter.setOnClickListener {
            ClothesCategoryFilterFragment().show(childFragmentManager, "NewClothesSelectionFilterFragment")
        }
    }

    private fun setViewModelObserver() : Unit {
        viewModel.tags.observe(this) {
            this.tags = viewModel.tags.value as MutableList<String>
        }
    }

}