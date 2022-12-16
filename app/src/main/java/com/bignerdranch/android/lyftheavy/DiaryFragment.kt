package com.bignerdranch.android.lyftheavy

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.bignerdranch.android.lyftheavy.DatePickerFragment.*
import java.util.*


private const val ARG_DIARY_ID = "diary_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class DiaryFragment : Fragment(), DatePickerFragment.Callbacks {


    private lateinit var diary: Diary
    private lateinit var addFood: Button
    private lateinit var datePick: Button
    private lateinit var totalCalories: TextView
    private lateinit var totalProtein: TextView
    private lateinit var totalFats: TextView
    private lateinit var totalCarbs: TextView
    private lateinit var delete_IC: Drawable

    private var swipeDelete: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))


    private lateinit var diaryFoodRecyclerView: RecyclerView
    private var adapter: FoodAdapter? = FoodAdapter(emptyList())

    private val diaryDetailViewModel: DiaryDetailModel by lazy {
        ViewModelProviders.of(this).get(DiaryDetailModel::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diaryId: UUID = arguments?.getSerializable(ARG_DIARY_ID) as UUID
        diary = Diary()
        diaryDetailViewModel.loadDiary(diaryId)

        setFragmentResultListener("food_selected") {requestKey, bundle ->

            val result = bundle.getSerializable("food")
            val food = result as Food
            var duplicate = false
            diaryDetailViewModel.loadDiary(diaryId)
            for(item in diaryDetailViewModel.diaryLiveData.value?.foodList!!)
            {
                if(item.id == food.id)
                {
                    val text = "You already have this item in your diary!"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                    duplicate = true
                    break
                }
            }
            if(!duplicate) {
                diaryDetailViewModel.diaryLiveData.value!!.foodList.add(food)
                val text = food.name + " logged successfully"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
            diaryDetailViewModel.saveDiary(diary)
            Log.i("TAG", diaryDetailViewModel.diaryLiveData.value?.foodList?.size.toString())

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_day, container, false)

        addFood = view.findViewById(R.id.addFood) as Button
        datePick = view.findViewById(R.id.food_date) as Button
        totalCalories = view.findViewById(R.id.totalCalVal) as TextView
        totalProtein = view.findViewById(R.id.totalProteinVal) as TextView
        totalFats = view.findViewById(R.id.totalFatVal2) as TextView
        totalCarbs = view.findViewById(R.id.totalCarbVal) as TextView

        diaryFoodRecyclerView = view.findViewById(R.id.add_food_recycler)
        diaryFoodRecyclerView.layoutManager = LinearLayoutManager(context)
        diaryFoodRecyclerView.adapter = adapter

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
                    diaryDetailViewModel.diaryLiveData.value?.foodList?.remove(food)
                }

                diaryDetailViewModel.diaryLiveData.observe(
                    viewLifecycleOwner,
                    Observer{foods ->
                        foods?.let {
                            updateUI()
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

        }).attachToRecyclerView(diaryFoodRecyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryDetailViewModel.diaryLiveData.observe(
            viewLifecycleOwner, Observer { diary->
                diary?.let{
                    this.diary = diary
                    updateUI()
                }
            }
        )

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun updateUI() {
        datePick.setText(DateFormat.format("EE, LLL d, yyyy", diary.date))

        var calories = 0
        var protein = 0
        var fats = 0
        var carbs = 0
        for(item in diary.foodList){
            calories += item.calories * item.quantity
            protein += item.protein * item.quantity
            fats += item.fats * item.quantity
            carbs += item.carbs * item.quantity
        }
        Log.i("TAG", "LOOP")
        totalProtein.text = (protein.toString())
        totalFats.text = (fats.toString())
        totalCarbs.text =(carbs.toString())
        diary.totalCalories = calories

        totalCalories.setText(diary.totalCalories.toString())

        adapter = FoodAdapter(diary.foodList)
        diaryFoodRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()


        val fragment = FoodChooseFragment.newInstance()
        addFood.setOnClickListener{
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.day_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }


        datePick.setOnClickListener{
            DatePickerFragment.newInstance(diary.date).apply {
                setTargetFragment(this@DiaryFragment, REQUEST_DATE)
                show(this@DiaryFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }


    }

    override fun onResume() {
        super.onResume()


    }

    override fun onStop() {
        super.onStop()
        diaryDetailViewModel.saveDiary(diary)
    }

    private inner class FoodHolder(view: View)
        :RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var food: Food

        private val nameTextView: TextView = itemView.findViewById(R.id.foodName)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesVal)
        private val proteinTextView: TextView = itemView.findViewById(R.id.proteinVal)
        private val fatTextview: TextView = itemView.findViewById(R.id.fatsVal)
        private val carbTextView: TextView = itemView.findViewById(R.id.carbsVal)
        private val quantityView: EditText = itemView.findViewById(R.id.quantityVal)

        init {
            itemView.setOnClickListener(this)
            val quantityWatcher = object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(sequence.toString() == "")
                    {
                        food.quantity == 0
                    }
                    else
                    {
                        food.quantity = sequence.toString().toInt()
                    }
                }

                override fun afterTextChanged(sequence: Editable?) {
                }
            }

            quantityView.setSingleLine(true)
            quantityView.addTextChangedListener(quantityWatcher)
            quantityView.setOnFocusChangeListener { _, hasFocus ->
                var a = true
                if(hasFocus) {

                } else {
                    var calories = 0
                    var protein = 0
                    var fats = 0
                    var carbs = 0
                    for(item in diary.foodList){
                        calories += item.calories * item.quantity
                        protein += item.protein * item.quantity
                        fats += item.fats * item.quantity
                        carbs += item.carbs * item.quantity

                    }
                    Log.i("TAG", "LOOP")
                    diary.totalCalories = calories
                    totalCalories.setText(diary.totalCalories.toString())
                    totalFats.text = (fats.toString())
                    totalCarbs.text =(carbs.toString())
                    totalProtein.text = (protein.toString())
                }
            }
        }

        fun bind(food: Food) {
            this.food = food

            nameTextView.text = food.name
            caloriesTextView.text = food.calories.toString()
            proteinTextView.text = food.protein.toString()
            fatTextview.text = food.fats.toString()
            carbTextView.text = food.carbs.toString()
            quantityView.setText(food.quantity.toString())
        }


        override fun onClick(v: View) {
        }
    }

    private inner class FoodAdapter(var foods: List<Food>)
        :RecyclerView.Adapter<FoodHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
            val view = layoutInflater.inflate(R.layout.list_item_foodday, parent, false)
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
        fun newInstance(diaryId: UUID): DiaryFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DIARY_ID, diaryId)
            }
            return DiaryFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        diary.date = date
        updateUI()
    }
}
