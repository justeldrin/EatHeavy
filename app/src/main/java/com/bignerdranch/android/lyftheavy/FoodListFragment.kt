package com.bignerdranch.android.lyftheavy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class FoodListFragment : Fragment() {

    private lateinit var foodRecyclerView: RecyclerView
    private var adapter: FoodAdapter? = FoodAdapter(emptyList())
    private lateinit var delete_IC: Drawable
    private var swipeDelete: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))


    private val foodListViewModel: FoodListViewModel by lazy {
        ViewModelProviders.of(this).get(FoodListViewModel::class.java)
    }

    interface Callbacks {
        fun onFoodSelected(foodId: UUID)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

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
            R.id.new_food -> {
                val food = Food()
                foodListViewModel.addFood(food)
                callbacks?.onFoodSelected(food.id)
                true
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


        delete_IC = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete) }!!

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val food = adapter?.get(viewHolder.adapterPosition)

                if (food != null) {
                    foodListViewModel.deleteFood(food)
                }

                foodListViewModel.foodListLiveData.observe(
                    viewLifecycleOwner,
                    Observer{ingredients ->
                        ingredients?.let {
                            updateUI(ingredients)
                        }
                    }
                )
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - delete_IC.intrinsicHeight) / 2

                if (dX > 0) {
                    swipeDelete.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    delete_IC.setBounds(itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + delete_IC.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeDelete.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    delete_IC.setBounds(itemView.right - iconMargin - delete_IC.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }

                swipeDelete.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                delete_IC.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }).attachToRecyclerView(foodRecyclerView)

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
            callbacks?.onFoodSelected(food.id)
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

        operator fun get(position: Int): Food {
            return foods[position]
        }
    }

    companion object {
        fun newInstance(): FoodListFragment {
            return FoodListFragment()
        }
    }
}