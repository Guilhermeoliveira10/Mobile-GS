package com.example.apprural_gs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnInserirDados: Button = findViewById(R.id.btnInserirDados)
        val btnHistorico: Button = findViewById(R.id.btnHistorico)
        val btnEditarDados: Button = findViewById(R.id.btnEditarDados)

        btnInserirDados.setOnClickListener {
            val intent = Intent(this, InsertDataActivity::class.java)
            startActivity(intent)
        }

        btnHistorico.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        btnEditarDados.setOnClickListener {
            val intent = Intent(this, EditDataActivity::class.java)
            startActivity(intent)
        }
    }
}
