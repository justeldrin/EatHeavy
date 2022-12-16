package com.bignerdranch.android.lyftheavy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper

class DiaryListFragment : Fragment(){

    private lateinit var diaryRecyclerView: RecyclerView
    private var adapter: DiaryAdapter? = DiaryAdapter(emptyList())
    private lateinit var manageFood: Button
    private lateinit var delete_IC: Drawable
    private var swipeDelete: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    private val diaryListViewModel: DiaryListViewModel by lazy {
        ViewModelProviders.of(this).get(DiaryListViewModel::class.java)

    }

    interface Callbacks {
        fun onDiarySelected(diaryId: UUID)
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
        inflater.inflate(R.menu.fragment_day_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_diary -> {
                val diary = Diary()
                diaryListViewModel.addDiary(diary)
                callbacks?.onDiarySelected(diary.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater:LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.day_list, container, false)

        diaryRecyclerView = view.findViewById(R.id.day_recycler_view) as RecyclerView
        diaryRecyclerView.layoutManager = LinearLayoutManager(context)
        diaryRecyclerView.adapter = adapter
        manageFood = view.findViewById(R.id.food_manage)

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
                val diary = adapter?.get(viewHolder.adapterPosition)

                if (diary != null) {
                    diaryListViewModel.deleteDiary(diary)
                }

                diaryListViewModel.diaryListLiveData.observe(
                    viewLifecycleOwner,
                    Observer{diaries ->
                        diaries?.let {
                            updateUI(diaries)
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

        }).attachToRecyclerView(diaryRecyclerView)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryListViewModel.diaryListLiveData.observe(
            viewLifecycleOwner,
            Observer{diaries ->
                diaries?.let{
                    updateUI(diaries)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        val fragment = FoodListFragment.newInstance()


        manageFood.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .add(R.id.day_list, fragment)
                .addToBackStack("yes")
                .commit()
        }
    }

    private fun updateUI(diaries: List<Diary>) {

        val sorted = diaries.sortedByDescending{it.date}
        adapter = DiaryAdapter(sorted)
        diaryRecyclerView.adapter = adapter
    }


    private inner class DiaryHolder(view: View)
        :RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var diary: Diary

        private val dateView: TextView = itemView.findViewById(R.id.dateView)
        private val totalCaloriesView: TextView = itemView.findViewById(R.id.totalCalListVal)

        init{
            itemView.setOnClickListener(this)
        }

        fun bind(diary: Diary) {

            this.diary = diary
            dateView.text = DateFormat.format("EE, LLL dd, yyyy", diary.date)
            totalCaloriesView.text = diary.totalCalories.toString()
        }

        override fun onClick(v: View) {
            callbacks?.onDiarySelected(diary.id)
        }
    }

    private inner class DiaryAdapter(var diaries: List<Diary>)
        :RecyclerView.Adapter<DiaryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
            val view = layoutInflater.inflate(R.layout.list_item_day, parent, false)
            return DiaryHolder(view)
        }

        override fun onBindViewHolder(holder: DiaryHolder, position: Int) {
            val diary = diaries[position]
            holder.bind(diary)
        }

        override fun getItemCount() = diaries.size

        operator fun get(position: Int): Diary {
            return diaries[position]
        }
        }



    companion object {
        fun newInstance(): DiaryListFragment {
            return DiaryListFragment()
        }
    }

}