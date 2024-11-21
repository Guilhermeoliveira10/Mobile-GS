package com.example.apprural_gs

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class EditDataActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var dataListView: ListView
    private lateinit var editContainer: LinearLayout
    private lateinit var editConsumption: EditText
    private lateinit var editArea: EditText
    private lateinit var editHours: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var selectedDataId: String? = null
    private val dataList = mutableListOf<Pair<String, Map<String, String>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)

        database = FirebaseDatabase.getInstance().getReference("Calculations")
        dataListView = findViewById(R.id.data_list)
        editContainer = findViewById(R.id.edit_container)
        editConsumption = findViewById(R.id.edit_consumption)
        editArea = findViewById(R.id.edit_area)
        editHours = findViewById(R.id.edit_hours)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)

        loadData()

        saveButton.setOnClickListener { saveChanges() }
        cancelButton.setOnClickListener { cancelEdit() }

        dataListView.setOnItemClickListener { _, _, position, _ ->
            try {
                val selectedData = dataList[position]
                selectedDataId = selectedData.first
                populateEditFields(selectedData.second)
                toggleEditContainer(true)
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao selecionar o item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                for (dataSnapshot in snapshot.children) {
                    val id = dataSnapshot.key ?: continue
                    val data = dataSnapshot.value as? Map<String, String> ?: continue
                    dataList.add(id to data)
                }

                val adapter = ArrayAdapter(
                    this@EditDataActivity,
                    android.R.layout.simple_list_item_1,
                    dataList.map { "Consumo: ${it.second["consumption"]}, Área: ${it.second["area"]}, Horas: ${it.second["hours"]}" }
                )
                dataListView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditDataActivity, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateEditFields(data: Map<String, String>) {
        editConsumption.setText(data["consumption"] ?: "")
        editArea.setText(data["area"] ?: "")
        editHours.setText(data["hours"] ?: "")
    }

    private fun saveChanges() {
        val updatedData = mapOf(
            "consumption" to editConsumption.text.toString(),
            "area" to editArea.text.toString(),
            "hours" to editHours.text.toString()
        )

        selectedDataId?.let { id ->
            database.child(id).updateChildren(updatedData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                    toggleEditContainer(false)
                } else {
                    Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cancelEdit() {
        toggleEditContainer(false)
    }

    private fun toggleEditContainer(show: Boolean) {
        editContainer.visibility = if (show) View.VISIBLE else View.GONE
    }
}
