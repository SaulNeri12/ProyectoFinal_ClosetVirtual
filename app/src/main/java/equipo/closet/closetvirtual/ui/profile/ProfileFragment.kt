package equipo.closet.closetvirtual.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import equipo.closet.closetvirtual.LoginActivity
import equipo.closet.closetvirtual.R
import java.util.Calendar


class ProfileFragment : Fragment() {

    private lateinit var btnBack: MaterialButton
    private lateinit var tvEmail: TextView
    private lateinit var etName: EditText
    private lateinit var spGender: Spinner
    private lateinit var etBirthDate: EditText
    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var scLightMode: SwitchCompat
    private lateinit var loyoutLogout: LinearLayout

    /**
     * Inflate the layout for this fragment do not change
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    /**
     * Initialize the fragment initialized variables here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById<MaterialButton>(R.id.btn_back)
        tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        etName = view.findViewById<EditText>(R.id.etName)
        spGender = view.findViewById<Spinner>(R.id.spGender)
        etBirthDate = view.findViewById<EditText>(R.id.etBirthDate)
        etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        btnEditProfile = view.findViewById<MaterialButton>(R.id.btnEditProfile)
        scLightMode = view.findViewById<SwitchCompat>(R.id.scLightMode)
        loyoutLogout = view.findViewById<LinearLayout>(R.id.logout_layout)

        // Set the user information
        setUserInformation()
        // Fill the gender spinner
        fillGenderSpinner()
        // Set the behavior of the birth date field
        setBirthDateFieldBehavior()
        // Switch light mode
        switchLightMode()
        // Logout
        logout()
        // Edit profile
        editProfile()

    }

    /**
     * Set the user information in the fragment except the password fields
     */
    private fun setUserInformation() : Unit{
        // Set the user email
        tvEmail.text = "example.com"
        // Set the user name
        etName.setText("John Doe")
        // Set the user gender
        spGender.setSelection(1)
        // Set the user birth date
        etBirthDate.setText("01/01/2000")
    }

    /**
     * Fill the gender spinner with the gender options
     */
    private fun fillGenderSpinner() : Unit {
        // List of gender options
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        // Create an ArrayAdapter using the genderOptions list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            genderOptions)
        // Set the layout resource for the dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the genderSpinner
        spGender.adapter = adapter
    }

    /**
     * Set the behavior of the birth date field
     */
    private fun setBirthDateFieldBehavior() : Unit {
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)

                    // Get current date
                    val currentDate = Calendar.getInstance()

                    if (selectedDate.after(currentDate)) {
                        Toast.makeText(requireContext(), "Birth date cannot be after current date",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                        etBirthDate.setText(dat)
                    }
                }, year, month, day)
            datePickerDialog.show()
        }
    }

    /**
     * Switch light mode
     */
    private fun switchLightMode(): Unit {
        scLightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to light mode
            } else {
                // Switch to dark mode
            }
        }
    }

    /**
     * Set the behavior of the logout button
     */
    private fun logout(): Unit {
        loyoutLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesion")
                .setMessage("¿Estás seguro de que quiere salir de la sesión?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Go to the login activity
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    /**
     * Validate the input fields fot the user to make sure each
     * field has the right input before editing the profile information
     *
     * @return true if the input fields are valid, false otherwise
     */
    private fun validateInputFields(): Boolean {
        //get the input fields
        val name = etName.text.toString()
        val birthDate = etBirthDate.text.toString()
        val gender = spGender.selectedItem.toString()
        val currentPassword = etCurrentPassword.text.toString()

        //validate empty fields
        if (name.isEmpty()) {
            etName.error = "Name is required"
            return false
        }
        if (birthDate.isEmpty()) {
            etBirthDate.error = "Birth date is required"
            return false
        }
        if (gender.isEmpty()) {
            Toast.makeText(requireContext(), "Gender is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (currentPassword.isNotEmpty()) {
            return if (validatePasswordChange()) false else true
        }
        //default case
        return true
    }

    /**
     * Validate the password change fields
     */
    private fun validatePasswordChange(): Boolean {
        //get the input fields
        val currentPassword = etCurrentPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        //if the thre password fields are so we infer that there is no
        //password change
        if (currentPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            return true
        }

        //validate empty fields
        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "Current password is required"
            Toast.makeText(requireContext(), "Current password is required",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword.isEmpty()) {
            etNewPassword.error = "New password is required"
            Toast.makeText(requireContext(), "New password is required",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirm password is required"
            Toast.makeText(requireContext(), "Confirm password is required",
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            Toast.makeText(requireContext(), "Passwords do not match",
                Toast.LENGTH_SHORT).show()
            return false
        }
        //default case
        return true
    }

    /**
     * Define the behavior of the edit profile button
     */
    private fun editProfile() : Unit {
        btnEditProfile.setOnClickListener {
            if (validateInputFields()) {
                val name = etName.text.toString()
                val birthDate = etBirthDate.text.toString()
                val gender = spGender.selectedItem.toString()
                val currentPassword = if (etCurrentPassword.toString().isEmpty() &&
                    etNewPassword.toString().isEmpty() &&
                    etConfirmPassword.toString().isEmpty()) etCurrentPassword.text.toString()
                else etNewPassword.text.toString()

                //persist the user new information

                Toast.makeText(
                    requireContext(), "Profile edited successfully",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


}