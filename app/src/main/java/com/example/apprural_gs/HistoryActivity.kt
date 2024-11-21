package com.example.apprural_gs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyList: MutableList<Pair<String, Map<String, String>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyListView = findViewById<android.widget.ListView>(R.id.history_list)
        historyList = mutableListOf()

        val database = FirebaseDatabase.getInstance().getReference("Calculations")

        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                historyList.clear()

                for (calculationSnapshot in snapshot.children) {
                    val calculationId = calculationSnapshot.key ?: continue
                    val calculation = calculationSnapshot.value
                    if (calculation is Map<*, *>) {
                        val mappedCalculation = calculation.entries
                            .filter { it.key is String && it.value is String }
                            .associate { it.key as String to it.value as String }

                        historyList.add(calculationId to mappedCalculation)
                    }
                }

                val adapter = CustomAdapter(historyList, database)
                historyListView.adapter = adapter
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@HistoryActivity, "Erro ao carregar histórico.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class CustomAdapter(
        private val data: List<Pair<String, Map<String, String>>>,
        private val database: com.google.firebase.database.DatabaseReference
    ) : BaseAdapter() {
        override fun getCount(): Int = data.size

        override fun getItem(position: Int): Pair<String, Map<String, String>> = data[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_history, parent, false)

            val calculation = getItem(position)
            val textView = view.findViewById<TextView>(R.id.history_text)
            val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)

            textView.text = "Consumo: ${calculation.second["consumption"]} kWh/mês, Área: ${calculation.second["area"]} m², Horas: ${calculation.second["hours"]} horas"

            deleteButton.setOnClickListener {
                database.child(calculation.first).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(view.context, "Item excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        (data as MutableList).removeAt(position) // Atualiza a lista local
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(view.context, "Erro ao excluir o item.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            return view
        }
    }
}
