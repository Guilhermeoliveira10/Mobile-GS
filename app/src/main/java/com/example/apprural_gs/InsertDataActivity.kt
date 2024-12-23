package com.example.apprural_gs

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class InsertDataActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_data)

        val consumptionInput: EditText = findViewById(R.id.average_consumption)
        val areaInput: EditText = findViewById(R.id.available_area)
        val hoursInput: EditText = findViewById(R.id.insolation_hours)
        val calculateButton: Button = findViewById(R.id.calculate)

        val database = FirebaseDatabase.getInstance().getReference("Calculations")

        calculateButton.setOnClickListener {
            val consumptionText = consumptionInput.text.toString()
            val areaText = areaInput.text.toString()
            val hoursText = hoursInput.text.toString()

            if (consumptionText.isEmpty() || areaText.isEmpty() || hoursText.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val consumption = consumptionText.toDouble()
            val area = areaText.toDouble()
            val hours = hoursText.toDouble()

            val efficiency = 0.15
            val energyGenerated = area * hours * 30 * efficiency

            val calculationId = database.push().key
            val calculation = mapOf(
                "consumption" to consumption.toString(),
                "area" to area.toString(),
                "hours" to hours.toString(),
                "energyGenerated" to energyGenerated.toString()
            )

            if (calculationId != null) {
                database.child(calculationId).setValue(calculation).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Cálculo salvo com sucesso!\nEnergia Gerada: ${"%.2f".format(energyGenerated)} kWh/mês",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Erro ao salvar os dados no Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
