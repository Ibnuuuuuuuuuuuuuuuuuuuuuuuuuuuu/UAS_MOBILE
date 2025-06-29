package com.example.kopipedia.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.kopipedia.R
import com.example.kopipedia.databinding.FragmentDetailBinding
import com.example.kopipedia.presentation.viewmodel.CoffeeViewModel
import com.example.kopipedia.presentation.viewmodel.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // Mengambil argumen yang dikirim dari HomeFragment
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel: CoffeeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        val coffee = args.coffee
        binding.collapsingToolbar.title = coffee.title
        binding.tvDetailDescription.text = coffee.description
        Glide.with(this)
            .load(coffee.imageUrl)
            .into(binding.ivDetailImage)

        // Set status FAB berdasarkan data
        updateFabIcon(coffee.isFavorite)

        // Aksi saat FAB diklik
        binding.fabFavorite.setOnClickListener {
            val newStatus = !coffee.isFavorite
            viewModel.setFavorite(coffee, newStatus)
            // Update UI secara langsung untuk respons cepat
            updateFabIcon(newStatus)
            // Kita perlu "refresh" data di argumen, cara mudahnya adalah navigasi ulang
            // ke diri sendiri dengan data baru, tapi untuk simpelnya kita biarkan
            // atau bisa juga observe dari database. Untuk sekarang kita biarkan simpel.
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateFabIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite)
            )
        } else {
            binding.fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
