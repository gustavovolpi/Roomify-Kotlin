package com.example.agrvai

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AlterarReservasActivity : AppCompatActivity() {

    private lateinit var btnConfirmar: MaterialButton
    private lateinit var nomeEventoEditText: TextInputEditText
    private lateinit var dataEditText: TextInputEditText
    private lateinit var reserva: Reserva
    private val db = FirebaseFirestore.getInstance()
    private var isUpdating = false
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_reserva)

        nomeEventoEditText = findViewById(R.id.edit_nome_evento)
        dataEditText = findViewById(R.id.edit_data)
        btnConfirmar = findViewById(R.id.btn_alterar)

        reserva = intent.getParcelableExtra("reserva") ?: run {
            Toast.makeText(this, "Reserva inválida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        nomeEventoEditText.setText(reserva.nomeEvento)
        dataEditText.setText(reserva.dataDisplay)

        dataEditText.setOnClickListener {
            abrirDatePicker()
        }

        btnConfirmar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun abrirDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            val selectedDate = Calendar.getInstance().apply {
                set(y, m, d)
            }

            val dataFormatada = dateFormat.format(selectedDate.time)
            dataEditText.setText(dataFormatada)
        }, year, month, day)

        // Bloqueia datas anteriores ao dia atual
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun salvarAlteracoes() {
        val novoNomeEvento = nomeEventoEditText.text.toString().trim()
        val novaData = dataEditText.text.toString().trim()

        if (novoNomeEvento.isEmpty()) {
            Toast.makeText(this, "Nome do evento não pode estar vazio", Toast.LENGTH_SHORT).show()
            return
        }

        isUpdating = true
        val updates = hashMapOf<String, Any>(
            "nome_evento" to novoNomeEvento,
            "data_display" to novaData
        )

        db.collection("agendamentos")
            .document(reserva.id)
            .update(updates)
            .addOnSuccessListener {
                reserva.nomeEvento = novoNomeEvento
                reserva.dataDisplay = novaData
                Toast.makeText(this, "Reserva atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                nomeEventoEditText.setText(reserva.nomeEvento)
                dataEditText.setText(reserva.dataDisplay)
            }
            .addOnCompleteListener {
                isUpdating = false
            }
    }
}