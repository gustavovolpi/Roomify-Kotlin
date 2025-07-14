package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agrvai.databinding.ActivityCalendarioBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class CalendarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarioBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configuração do Spinner
        val salas = listOf("Salão", "Sala de Reunião", "Auditório", "Coworking")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, salas)
        binding.roomSpinner.adapter = adapter

        // 👉 Configuração do CalendarView
        configureCalendarView()

        // 👉 Navegação para Perfil
        binding.profileIcon.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // 👉 Logout
        binding.settingsIcon.setOnClickListener {
            auth.signOut()
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
            }
            finish()
        }
    }

    private fun configureCalendarView() {
        // Obtém a data atual com horário zerado para comparação precisa
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Bloqueia datas passadas no CalendarView (com margem de 1 dia para timezone)
        binding.calendarView2.minDate = today.timeInMillis - 86400000

        // Listener para seleção de data
        binding.calendarView2.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            when {
                // Verifica se a data selecionada é anterior à data atual
                selectedDate.before(today) -> {
                    Toast.makeText(
                        this,
                        "❌ Não é possível agendar para datas passadas",
                        Toast.LENGTH_LONG
                    ).show()
                }
                // Data válida (qualquer dia)
                else -> {
                    val dataSelecionada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    val salaSelecionada = binding.roomSpinner.selectedItem.toString()

                    Intent(this, ConfirmarAgendamentoActivity::class.java).apply {
                        putExtra("data", dataSelecionada)
                        putExtra("sala", salaSelecionada)
                        startActivity(this)
                    }
                }
            }
        }
    }
}