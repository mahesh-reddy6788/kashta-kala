package com.kashta.kala.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sequenced entry animations (matching the premium login screen feel)
        binding.ivRegisterLogo.scaleX = 0f
        binding.ivRegisterLogo.scaleY = 0f
        binding.ivRegisterLogo.alpha = 0f
        
        binding.tvRegisterBrand.translationY = -50f
        binding.tvRegisterBrand.alpha = 0f
        
        binding.tvRegisterHeading.translationY = -50f
        binding.tvRegisterHeading.alpha = 0f
        
        binding.tvRegisterSubtitle.translationY = -30f
        binding.tvRegisterSubtitle.alpha = 0f

        binding.registerCard.translationY = 400f
        binding.registerCard.alpha = 0f

        binding.ivRegisterLogo.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500).setStartDelay(100).start()
        binding.tvRegisterBrand.animate().translationY(0f).alpha(1f).setDuration(400).setStartDelay(250).start()
        binding.tvRegisterHeading.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(350).start()
        binding.tvRegisterSubtitle.animate().translationY(0f).alpha(1f).setDuration(400).setStartDelay(450).start()
        binding.registerCard.animate().translationY(0f).alpha(1f).setDuration(600).setStartDelay(500).start()

        binding.btnRegister.setOnClickListener { attemptRegister() }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun attemptRegister() {
        val name     = binding.etName.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val phone    = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm  = binding.etConfirmPassword.text.toString()

        // Clear errors
        binding.tilName.error            = null
        binding.tilEmail.error           = null
        binding.tilPhone.error           = null
        binding.tilPassword.error        = null
        binding.tilConfirmPassword.error = null

        var valid = true
        if (name.length < 2) {
            binding.tilName.error = "Enter your full name"
            binding.etName.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email address"
            valid = false
        }
        if (phone.length != 10) {
            binding.tilPhone.error = "Phone must be 10 digits"
            valid = false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            valid = false
        }
        if (confirm != password) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            valid = false
        }
        if (!valid) return

        // Show loading
        binding.btnRegister.text = ""
        binding.registerProgress.visibility = View.VISIBLE

        binding.root.postDelayed({
            if (_binding == null) return@postDelayed
            
            val success = com.kashta.kala.data.DataRepository.registerNewUser(name, email, phone, password)
            if (success) {
                binding.registerProgress.visibility = View.GONE
                binding.btnRegister.text = "Create Account"
                Snackbar.make(binding.root, "Account created! Please sign in.", Snackbar.LENGTH_LONG).show()
                binding.root.postDelayed({
                    findNavController().navigate(R.id.action_register_to_login)
                }, 1200)
            } else {
                binding.registerProgress.visibility = View.GONE
                binding.btnRegister.text = "Create Account"
                binding.tilEmail.error = "Email address already registered"
                binding.etEmail.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            }
        }, 700)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
