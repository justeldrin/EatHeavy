package com.bignerdranch.android.lyftheavy

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class FoodChooseFragment : Fragment() {

    private lateinit var foodRecyclerView: RecyclerView
    private var adapter: FoodAdapter? = FoodAdapter(emptyList())


    private val foodListViewModel: FoodListViewModel by lazy {
        ViewModelProviders.of(this).get(FoodListViewModel::class.java)
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()

        inflater.inflate(R.menu.fragment_food_list, menu)

        val searchItem: MenuItem = menu.findItem(R.id.nav_search)

        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filterQuery: String): Boolean {

                foodListViewModel.foodListLiveData.observe(
                    viewLifecycleOwner,
                    Observer{foods ->
                        foods?.let {
                            val filteredFoods = foods.filter { food -> food.name.toLowerCase().contains(filterQuery.toLowerCase())}
                            updateUI(filteredFoods)
                        }
                    }
                )
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStackImmediate()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.food_list, container, false)

        foodRecyclerView = view.findViewById(R.id.food_recycler_view) as RecyclerView
        foodRecyclerView.layoutManager = LinearLayoutManager(context)
        foodRecyclerView.adapter = adapter

        view.setBackgroundColor(Color.WHITE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodListViewModel.foodListLiveData.observe(
            viewLifecycleOwner,
            Observer{foods ->
                foods?.let{
                    updateUI(foods)
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
    }

    private fun updateUI(foods: List<Food>) {
        adapter = FoodAdapter(foods)
        foodRecyclerView.adapter = adapter
    }


    private inner class FoodHolder(view: View)
        :RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var food: Food

        private val nameTextView: TextView = itemView.findViewById(R.id.foodName)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesVal)
        private val proteinTextView: TextView = itemView.findViewById(R.id.proteinVal)
        private val fatTextview: TextView = itemView.findViewById(R.id.fatsVal)
        private val carbTextView: TextView = itemView.findViewById(R.id.carbsVal)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(food: Food) {
            this.food = food

            nameTextView.text = food.name
            caloriesTextView.text = food.calories.toString()
            proteinTextView.text = food.protein.toString()
            fatTextview.text = food.fats.toString()
            carbTextView.text = food.carbs.toString()
        }

        override fun onClick(v: View) {
            var bundle = Bundle()
  /*          bundle.putString("UUID", food.id.toString())
            bundle.putString("name", food.name)
            bundle.putInt("calories", food.calories)
            bundle.putInt("protein", food.protein)
            bundle.putInt("fats", food.fats)
            bundle.putInt("carbs", food.carbs)
*/
            bundle.putSerializable("food", food)
            setFragmentResult("food_selected", bundle)
            parentFragmentManager.popBackStackImmediate()
        }
    }

    private inner class FoodAdapter(var foods: List<Food>)
        :RecyclerView.Adapter<FoodHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
            val view = layoutInflater.inflate(R.layout.list_item_food, parent, false)
            return FoodHolder(view)
        }

        override fun onBindViewHolder(holder: FoodHolder, position: Int) {
            val food = foods[position]
            holder.bind(food)
        }

        override fun getItemCount() = foods.size
    }

    companion object {
        fun newInstance(): FoodChooseFragment {
            return FoodChooseFragment()
        }
    }
}