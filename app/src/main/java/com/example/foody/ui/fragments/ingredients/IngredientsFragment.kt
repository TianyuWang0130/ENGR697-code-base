package com.example.foody.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.R
import com.example.foody.adapters.IngredientsAdapter
import com.example.foody.databinding.FragmentIngredientsBinding
import com.example.foody.decorator.StickyHeaderDecorator
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {

    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(context)
    }
    private val stickyHeaderDecorator by lazy {
        StickyHeaderDecorator(requireContext())
    }

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setupRecyclerView()
        myBundle?.extendedIngredients?.let {
            var sorted = it.sortedBy { ingredient -> ingredient.aisle }
            mAdapter.setData(sorted)
            val list = sorted.map { ingredient -> ingredient.aisle }
            stickyHeaderDecorator.setCategoryList(list as List<String>)
        }

        setupBinner()

        return binding.root
    }

    private fun setupBinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortSpinner.adapter = adapter
        }
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.adapter = mAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientsRecyclerview.addItemDecoration(stickyHeaderDecorator)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}