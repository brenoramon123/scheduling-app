package com.example.beachtime.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
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

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10) {
                dia = "0$dayOfMonth"
            }
            if (monthOfYear < 9) {
                mes = "0" + (monthOfYear + 1)
            } else {
                mes = (monthOfYear + 1).toString()
            }

            data = "$dia/$mes/$year"
        }
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val minuto: String

            if (minute < 10) {
                minuto = "0$minute"
            } else {
                minuto = minute.toString()
            }

            hora = "$hourOfDay:$minuto" //19:00
        }

        binding.timePicker.setIs24HourView(true) //formato de 24 horas

        binding.btAgendar.setOnClickListener {
            val modalidade1 = binding.modalidade1
            val modalidade2 = binding.modalidade2
            val modalidade3 = binding.modalidade3
            val modalidade4 = binding.modalidade4

            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
                set(Calendar.MINUTE, binding.timePicker.minute)
            }

            val currentDate = Calendar.getInstance()

            if (selectedCalendar.before(currentDate)) {
                mensagem(it, "Não é possível agendar datas passadas!", "#FF0000")
                return@setOnClickListener
            }

            val selectedTime =
                String.format("%02d:%02d", binding.timePicker.hour, binding.timePicker.minute)

            when {
                hora.isEmpty() -> {
                    mensagem(it, "Preencha o horário!", "#FF0000")
                }

                selectedTime < "08:00" || selectedTime > "22:00" -> {
                    mensagem(
                        it,
                        "Arena 1505 está fechada - horário de atendimento das 08:00 às 22:00 ",
                        "#FF0000"
                    )
                }

                data.isEmpty() -> {
                    mensagem(it, "Selecione a data! ", "#FF0000")
                }

                modalidade1.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Beach Tennis", data, hora)
                }

                modalidade2.isChecked && data.isNotEmpty() && hora.isNotEmpty()
                -> {
                    salvarAgendamento(it, nome, "Volei", data, hora)
                }

                modalidade3.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "FutVolei", data, hora)
                }

                modalidade4.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "FutMesa", data, hora)
                }

                else -> {
                    mensagem(it, "Escolha uma modalidade!", "#FF0000")
                }
            }
        }
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun salvarAgendamento(
        view: View,
        cliente: String,
        modalidade: String,
        data: String,
        hora: String
    ) {
        val formaPagamento = getFormaPagamentoSelecionada()
        if (formaPagamento.isEmpty()) {
            mensagem(view, "Selecione uma forma de pagamento!", "#FF0000")
        } else {
            val db = FirebaseFirestore.getInstance()
            val dadosUsuario = hashMapOf(
                "cliente" to cliente,
                "modalidade" to modalidade,
                "data" to data,
                "hora" to hora,
                "formaPagamento" to formaPagamento
            )
            db.collection("agendamento").document(cliente)
                .set(dadosUsuario)
                .addOnCompleteListener {
                    mensagem(view, "Agendamento realizado com sucesso!", "#FF03DAC5")
                }
                .addOnFailureListener {
                    mensagem(view, "Erro no servidor", "#FF0000")
                }
        }
    }

    private fun getFormaPagamentoSelecionada(): String {
        val radioGroup = binding.paymentRadioGroup
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            // Nenhuma forma de pagamento selecionada
            mensagem(binding.btAgendar, "Selecione uma forma de pagamento!", "#FF0000")
            return ""
        } else {
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            return selectedRadioButton?.text.toString()
        }
    }
}


