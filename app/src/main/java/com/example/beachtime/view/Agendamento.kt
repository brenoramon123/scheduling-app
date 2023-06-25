package com.example.beachtime.view

import com.example.beachtime.R
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi

import com.example.beachtime.databinding.ActivityAgendamentoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val nome = intent.extras?.getString("nome").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->

            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10){
                dia = "o $dayOfMonth"
            }
            if (monthOfYear < 10){
                mes = ""+(monthOfYear+1)
            }else{
                mes = (monthOfYear + 1).toString()
            }

            data = "$dia / $mes / $year"
        }
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val minuto: String

            if(minute < 10){
                minuto = "0$minute"
            }else{
                minuto = minute.toString()
            }

            hora = "$hourOfDay: $minuto" //19:00
        }

        binding.timePicker.setIs24HourView(true) //formato de 24 horas

        binding.btAgendar.setOnClickListener {
            val modalidade1 = binding.modalidade1
            val modalidade2 = binding.modalidade2
            val modalidade3 = binding.modalidade3
            val modalidade4 = binding.modalidade4

            when {
                hora.isEmpty() -> {
                    mensagem(it, "Preencha o horário!", "#FF0000")
                }
                hora < "8:00" || hora > "22:00" -> {
                    mensagem(
                        it,
                        "Arena 1505 está fechada - horário de atendimento das 08:00 as 22:00 ",
                        "#FF0000"
                    )
                }
                data.isEmpty() -> {

                    mensagem(it, "Selecione a data! ", "#FF0000")
                }
                binding.radioGroup.checkedRadioButtonId == -1 -> {
                    mensagem(it, "Escolha uma modalidade! ", "#00FF00")
                }
                else -> {
                    val modalidadeSelecionada = when (binding.radioGroup.checkedRadioButtonId) {
                        R.id.modalidade1 -> "Beach Tennis"
                        R.id.modalidade2 -> "Volei"
                        R.id.modalidade3 -> "FutVolei"
                        R.id.modalidade4 -> "FutMesa"
                        else -> ""
                    }


                            if (data.isNotEmpty() && hora.isNotEmpty()) {
                                salvarAgendamento(it, nome, modalidadeSelecionada, data, hora)
                            }
                }
            }
        }
    }

    private fun mensagem(view: View, mensagem: String, cor: String){
        val snackbar = Snackbar.make(view,mensagem,Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun salvarAgendamento(view: View,cliente: String,modalidade: String,data: String,hora:String) {

        val db = FirebaseFirestore.getInstance()
        val dadosUsuario = hashMapOf(
            "cliente" to cliente,
            "Modalidade" to modalidade,
            "Data" to data,
            "Hora" to hora
        )
        db.collection("agendamento").document(cliente).set(dadosUsuario).addOnCompleteListener {
            mensagem(view,"Agendamento realizado com sucesso!","#FF03DAC5")
        }.addOnFailureListener{
            mensagem(view,"Erro no servidor","#FF0000")
        }
    }
}