package equipo.closet.closetvirtual.ui.usageHistory

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import equipo.closet.closetvirtual.databinding.FragmentUsageHistoryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UsageHistoryFragment : Fragment() {

    private var _binding: FragmentUsageHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsageHistoryViewModel by viewModels()
    private lateinit var historyAdapter: UsageHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsageHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        historyAdapter = UsageHistoryAdapter(emptyList())
        binding.rvUsedGarments.adapter = historyAdapter
    }

    private fun setupListeners() {
        binding.btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupObservers() {
        viewModel.usedGarments.observe(viewLifecycleOwner) { garments ->
            val hasGarments = garments.isNotEmpty()
            binding.rvUsedGarments.isVisible = hasGarments
            binding.tvNoGarments.isVisible = !hasGarments

            if (hasGarments) {
                historyAdapter.updateData(garments)
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            val selectedDate = selectedCalendar.time

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvSelectedDate.text = "Prendas usadas el: ${sdf.format(selectedDate)}"
            binding.tvSelectedDate.isVisible = true

            viewModel.fetchGarmentsForDate(selectedDate)

        }, year, month, day)

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}