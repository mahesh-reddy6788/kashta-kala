package com.kashta.kala.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.databinding.FragmentProfileBinding
import com.kashta.kala.databinding.BottomSheetChangePasswordBinding
import com.kashta.kala.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}
                selectedImageUri = uri
                Glide.with(this).load(uri).circleCrop()
                    .placeholder(R.drawable.ic_person).into(binding.ivProfileImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_profile_to_login)
            return
        }

        loadProfile()
        setupClickListeners()
    }

    private fun loadProfile() {
        binding.etProfileName.setText(sessionManager.getUserName())
        binding.tvProfileEmail.text = sessionManager.getUserEmail()
        binding.tvMemberSince.text  = "Member since Jan 2025"

        // Admin button visibility
        if (sessionManager.isAdmin()) {
            binding.btnAdminDashboard.visibility = View.VISIBLE
        } else {
            binding.btnAdminDashboard.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        // Profile image picker
        binding.cvProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pickImage.launch(intent)
        }

        // Save profile
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etProfileName.text.toString().trim()
            if (name.length < 2) {
                binding.tilProfileName.error = "Name must be at least 2 characters"
                return@setOnClickListener
            }
            binding.tilProfileName.error = null
            sessionManager.updateUserName(name)
            Snackbar.make(binding.root, "Profile updated ✓", Snackbar.LENGTH_SHORT).show()
        }

        // Change password
        binding.btnChangePassword.setOnClickListener { showChangePasswordSheet() }

        // Quick links
        binding.rowWishlist.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_wishlist)
        }
        binding.rowOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_orders)
        }
        binding.rowSavedQuotes.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_saved_quotes)
        }
        binding.rowHelp.setOnClickListener {
            Snackbar.make(binding.root, "Contact us: support@kashtakala.com", Snackbar.LENGTH_LONG).show()
        }
        binding.rowAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Kashta Kala")
                .setMessage("Version 1.0\n\nCrafted for You. Built to Last.\n\nKashta Kala is a premium custom woodwork platform connecting artisans with customers who value quality craftsmanship.")
                .setPositiveButton("Close", null)
                .show()
        }

        // Admin dashboard
        binding.btnAdminDashboard.setOnClickListener {
            findNavController().navigate(R.id.navigation_admin)
        }

        // Sign out
        binding.btnSignOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Sign Out?")
                .setMessage("You will be redirected to the login screen.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Sign Out") { _, _ ->
                    sessionManager.logout()
                    findNavController().navigate(R.id.action_profile_to_login)
                }
                .show()
        }
    }

    private fun showChangePasswordSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetChangePasswordBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.btnUpdatePassword.setOnClickListener {
            val current = sheetBinding.etCurrentPassword.text.toString()
            val newPass  = sheetBinding.etNewPassword.text.toString()
            val confirm  = sheetBinding.etConfirmNewPassword.text.toString()

            if (current.isEmpty() || newPass.length < 6 || newPass != confirm) {
                Snackbar.make(sheetBinding.root, "Check your inputs and try again", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            Snackbar.make(binding.root, "Password updated ✓", Snackbar.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
