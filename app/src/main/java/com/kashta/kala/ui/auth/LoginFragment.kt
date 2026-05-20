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
import com.kashta.kala.databinding.FragmentLoginBinding
import com.kashta.kala.utils.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sequenced entry animations (glassmorphism/reference design feel)
        binding.ivLoginLogo.scaleX = 0f
        binding.ivLoginLogo.scaleY = 0f
        binding.ivLoginLogo.alpha = 0f
        
        binding.tvBrandName.translationY = -50f
        binding.tvBrandName.alpha = 0f
        
        binding.tvLoginHeading.translationY = -50f
        binding.tvLoginHeading.alpha = 0f
        
        binding.tvLoginSubtitle.translationY = -30f
        binding.tvLoginSubtitle.alpha = 0f

        binding.loginCard.translationY = 400f
        binding.loginCard.alpha = 0f

        binding.ivLoginLogo.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500).setStartDelay(100).start()
        binding.tvBrandName.animate().translationY(0f).alpha(1f).setDuration(400).setStartDelay(250).start()
        binding.tvLoginHeading.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(350).start()
        binding.tvLoginSubtitle.animate().translationY(0f).alpha(1f).setDuration(400).setStartDelay(450).start()
        binding.loginCard.animate().translationY(0f).alpha(1f).setDuration(600).setStartDelay(500).start()

        binding.btnLogin.setOnClickListener { attemptLogin() }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.tvForgotPassword.setOnClickListener {
            Snackbar.make(binding.root, "Contact support at support@kashtakala.com", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun attemptLogin() {
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Clear previous errors
        binding.tilEmail.error    = null
        binding.tilPassword.error = null

        var valid = true
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email address"
            binding.etEmail.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            valid = false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            binding.etPassword.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            valid = false
        }
        if (!valid) return

        // Show loading
        binding.btnLogin.text = ""
        binding.loginProgress.visibility = View.VISIBLE

        // Simulate async check
        binding.root.postDelayed({
            if (_binding == null) return@postDelayed
            binding.loginProgress.visibility = View.GONE
            binding.btnLogin.text = "Sign In"

            val matchedUser = com.kashta.kala.data.DataRepository.usersList.find { u -> u.email.equals(email, ignoreCase = true) && u.password == password }

            if (matchedUser != null) {
                val userId = matchedUser.id
                val name   = matchedUser.name
                sessionManager.saveSession(userId, name, email, matchedUser.isAdmin)
                if (matchedUser.isAdmin) {
                    findNavController().navigate(R.id.navigation_admin)
                } else {
                    findNavController().navigate(R.id.action_login_to_home)
                }
            } else {
                Snackbar.make(binding.root, "Invalid email or password", Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.btnLogin)
                    .show()
            }
        }, 600)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
