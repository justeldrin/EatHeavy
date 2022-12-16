package com.bignerdranch.android.lyftheavy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*


private const val ARG_FOOD_ID = "food_id"

class FoodFragment : Fragment() {

    private lateinit var food: Food
    private lateinit var foodName: EditText
    private lateinit var foodCalories: EditText
    private lateinit var foodProtein: EditText
    private lateinit var foodFat: EditText
    private lateinit var foodCarb: EditText

    private val foodDetailViewModel: FoodDetailViewModel by lazy {
        ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodId: UUID = arguments?.getSerializable(ARG_FOOD_ID) as UUID
        food = Food()
        foodDetailViewModel.loadFood(foodId)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food, container, false)

        foodName = view.findViewById(R.id.editFood) as EditText
        foodCalories = view.findViewById(R.id.editCalories) as EditText
        foodProtein = view.findViewById(R.id.editProtein) as EditText
        foodFat = view.findViewById(R.id.editFat) as EditText
        foodCarb = view.findViewById(R.id.editCarb) as EditText

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodDetailViewModel.foodLivedata.observe(
            viewLifecycleOwner, Observer {food ->
                food?.let {
                    this.food = food
                    updateUI()
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


    private fun updateUI() {
        foodName.setText(food.name)
        foodCalories.setText(food.calories.toString())
        foodProtein.setText(food.protein.toString())
        foodCarb.setText(food.carbs.toString())
        foodFat.setText(food.fats.toString())
    }

    override fun onStart() {
        super.onStart()

        val nameWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                food.name = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val calorieWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(sequence.toString() == "")
                {
                    food.calories == 0
                }
                else
                {
                    food.calories = sequence.toString().toInt()
                }
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val proteinWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(sequence.toString() == "")
                {
                    food.protein == 0
                }
                else
                {
                    food.protein = sequence.toString().toInt()
                }
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val carbWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(sequence.toString() == "")
                {
                    food.carbs == 0
                }
                else
                {
                    food.carbs = sequence.toString().toInt()
                }
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val fatWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(sequence.toString() == "")
                {
                    food.fats == 0
                }
                else
                {
                    food.fats = sequence.toString().toInt()
                }
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        foodName.addTextChangedListener(nameWatcher)
        foodCalories.addTextChangedListener(calorieWatcher)
        foodProtein.addTextChangedListener(proteinWatcher)
        foodCarb.addTextChangedListener(carbWatcher)
        foodFat.addTextChangedListener(fatWatcher)

    }

    override fun onStop() {
        super.onStop()
        foodDetailViewModel.saveFood(food)
    }

    companion object {

        fun newInstance(foodId: UUID): FoodFragment {
            val args = Bundle().apply {
                putSerializable(ARG_FOOD_ID, foodId)
            }
            return FoodFragment().apply {
                arguments = args
            }
        }
    }

}