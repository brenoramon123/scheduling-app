package com.example.beachtime.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beachtime.R
import com.example.beachtime.adapter.ServicosAdapter
import com.example.beachtime.databinding.ActivityHomeBinding
import com.example.beachtime.model.Servicos

class Home : AppCompatActivity() {

    private lateinit var  binding: ActivityHomeBinding
    private lateinit var servicosAdapter: ServicosAdapter
    private val listaServicos: MutableList<Servicos> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val nome = intent.extras?.getString("nome")

        binding.txtNomeUsuario.text = "Bem-Vindo,$nome"
        val recyclerViewServicos = binding.recyclerViewServicos
        recyclerViewServicos.layoutManager = GridLayoutManager(this, 2)
        servicosAdapter = ServicosAdapter(this, listaServicos)
        recyclerViewServicos.setHasFixedSize(true)
        recyclerViewServicos.adapter = servicosAdapter
        getServicos()

        binding.btAgendar.setOnClickListener {
            val intent = Intent(this,Agendamento::class.java)
            intent.putExtra("nome",nome)
            startActivity(intent)
        }

    }
    private fun getServicos(){
        val servico1 = Servicos(R.drawable.quadra_1, "Quadra 1")
        listaServicos.add(servico1)

        val servico2 = Servicos(R.drawable.quadra_2,"Quadra 2")
        listaServicos.add(servico2)

        val servico3 = Servicos(R.drawable.quadra_3, "Quadra 3")
        listaServicos.add(servico3)

        val servico4 = Servicos(R.drawable.quadra_4, "Quadra 4")
        listaServicos.add(servico4)
    }
}