package com.kashta.kala.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kashta.kala.R
import com.kashta.kala.data.DataRepository
import com.kashta.kala.data.Product
import com.kashta.kala.databinding.FragmentAddProductBinding

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { uri ->
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            selectedImageUri = uri
            Glide.with(this).load(uri).centerCrop().into(binding.ivProductPreview)
            binding.tvAddPhotoHint.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = DataRepository.categories
        val catAdapter = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, categories
        )
        binding.actvCategory.setAdapter(catAdapter)

        binding.ivProductPreview.setOnClickListener { launchImagePicker() }
        binding.btnSelectImage.setOnClickListener   { launchImagePicker() }

        binding.btnAddProduct.setOnClickListener { addProduct() }
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        pickImage.launch(intent)
    }

    private fun addProduct() {
        val name     = binding.etProductName.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val desc     = binding.etDescription.text.toString().trim()
        val dims     = binding.etDimensions.text.toString().trim()
        val material = binding.etMaterial.text.toString().trim()
        val warranty = binding.etWarranty.text.toString().trim()
        val featured = binding.switchFeatured.isChecked

        // Validation
        var valid = true
        if (name.isEmpty())     { binding.tilProductName.error = "Required"; valid = false }
        if (category.isEmpty()) { binding.tilCategory.error    = "Required"; valid = false }
        if (priceStr.isEmpty()) { binding.tilPrice.error       = "Required"; valid = false }
        if (desc.length < 20)   { binding.tilDescription.error = "Min 20 characters"; valid = false }
        if (!valid) return

        val price = priceStr.toDoubleOrNull() ?: run {
            binding.tilPrice.error = "Enter a valid number"; return
        }

        val newProduct = Product(
            id          = (DataRepository.products.maxOfOrNull { it.id } ?: 0) + 1,
            name        = name,
            category    = category,
            price       = price,
            description = desc,
            imageResId  = null, // URI-based images not supported in hardcoded mode
            dimensions  = dims.ifEmpty { null },
            material    = material.ifEmpty { null },
            warranty    = warranty.ifEmpty { null },
            isFeatured  = featured
        )
        DataRepository.products.add(newProduct)

        // Clear form
        binding.etProductName.text?.clear()
        binding.actvCategory.text?.clear()
        binding.etPrice.text?.clear()
        binding.etDescription.text?.clear()
        binding.etDimensions.text?.clear()
        binding.etMaterial.text?.clear()
        binding.etWarranty.text?.clear()
        binding.switchFeatured.isChecked = false
        binding.ivProductPreview.setImageResource(R.drawable.ic_photo)
        binding.tvAddPhotoHint.visibility = View.VISIBLE
        selectedImageUri = null

        android.widget.Toast.makeText(context, "\"$name\" published to catalog ✓", android.widget.Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
