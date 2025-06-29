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
import com.example.kopipedia.databinding.FragmentFavoritesBinding
import com.example.kopipedia.presentation.adapter.CoffeeAdapter
import com.example.kopipedia.presentation.viewmodel.CoffeeViewModel
import com.example.kopipedia.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoffeeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var coffeeAdapter: CoffeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        coffeeAdapter = CoffeeAdapter { coffee ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(coffee)
            findNavController().navigate(action)
        }
        binding.rvFavoriteCoffees.apply {
            adapter = coffeeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFavoriteCoffeesUseCase().collectLatest { favoriteList ->
                binding.tvEmptyFavorites.isVisible = favoriteList.isEmpty()
                coffeeAdapter.submitList(favoriteList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
