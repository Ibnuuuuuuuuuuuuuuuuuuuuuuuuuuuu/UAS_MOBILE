package com.example.kopipedia.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kopipedia.databinding.FragmentHomeBinding
import com.example.kopipedia.presentation.adapter.CoffeeAdapter
import com.example.kopipedia.presentation.ui.util.ResultState
import com.example.kopipedia.presentation.viewmodel.CoffeeViewModel
import com.example.kopipedia.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Menggunakan delegate 'viewModels' dengan factory yang sudah kita buat
    private val viewModel: CoffeeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var coffeeAdapter: CoffeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter { coffee ->
            // Aksi saat item diklik -> navigasi ke detail
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(coffee)
            findNavController().navigate(action)
        }
        binding.rvCoffees.apply {
            adapter = coffeeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // Menggunakan lifecycleScope untuk observasi yang aman
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coffees.collectLatest { result ->
                binding.progressBar.isVisible = result is ResultState.Loading
                binding.tvError.isVisible = result is ResultState.Error

                if (result is ResultState.Success) {
                    coffeeAdapter.submitList(result.data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCoffees.adapter = null // Membersihkan adapter untuk menghindari memory leak
        _binding = null
    }
}
