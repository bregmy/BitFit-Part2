package com.example.bitfit

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset



class LogsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: FoodAdapter
    private val foodList = ArrayList<ItemClass>()
    private lateinit var dataFile: File
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = FoodAdapter(foodList)

        dataFile = File(requireContext().filesDir, "data.txt")
        loadItems()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = FoodAdapter(foodList)
        recyclerView.adapter = userAdapter

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(requireContext(), NewActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val food = data.getStringExtra("FOOD")
            val calorie = data.getStringExtra("CALORIE")

            val newItem = ItemClass(food!!, calorie!!)
            foodList.add(newItem)
            userAdapter.notifyDataSetChanged()
            saveItems()
        }
    }

    private fun loadItems() {
        try {
            foodList.clear()
            val lines = FileUtils.readLines(dataFile, Charset.defaultCharset())
            for (line in lines) {
                val parts = line.split(":")
                val food = parts[0]
                val calorie = parts[1]
                val item = ItemClass(food, calorie)
                foodList.add(item)
            }
            userAdapter.notifyDataSetChanged()
            calculateCalories()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun saveItems() {
        val lines = mutableListOf<String>()
        for (food in foodList) {
            val line = "${food.foodName}:${food.caloriesCount}"
            lines.add(line)
        }

        try {
            FileUtils.writeLines(dataFile, lines)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun calculateCalories() {
        var totalCalories = 0
        var maxCalories = 0
        var minCalories = Int.MAX_VALUE

        for (food in foodList) {
            val calories = food.caloriesCount.trim().toInt() // trim the input string before parsing
            totalCalories += calories

            if (calories > maxCalories) {
                maxCalories = calories
            }

            if (calories < minCalories) {
                minCalories = calories
            }
        }

        val averageCalories = if (foodList.isEmpty()) {
            0
        } else {
            totalCalories / foodList.size
        }

        val maxCaloriesTextView = view?.findViewById<TextView>(R.id.maximum_calories_value_textview)
        val minCaloriesTextView = view?.findViewById<TextView>(R.id.minimum_calories_value_textview)
        val avgCaloriesTextView = view?.findViewById<TextView>(R.id.average_calories_value_textview)

        maxCaloriesTextView?.text = maxCalories.toString()
        minCaloriesTextView?.text = minCalories.toString()
        avgCaloriesTextView?.text = averageCalories.toString()
    }
}


