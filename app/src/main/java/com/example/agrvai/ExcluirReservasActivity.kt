package com.example.agrvai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExcluirReservasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservaAdminAdapter
    private val db = FirebaseFirestore.getInstance()
    private val listaReservas = mutableListOf<Reserva>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excluir_reservas)

        recyclerView = findViewById(R.id.recycler_view_reservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReservaAdminAdapter(
            reservas = listaReservas,
            onExcluirClick = { reserva -> excluirReserva(reserva) },
            onAlterarClick = { /* Ação vazia */ },
            showAlterarButton = false // Oculta o botão Alterar
        )

        recyclerView.adapter = adapter
        carregarReservas()
    }

    private fun carregarReservas() {
        db.collection("agendamentos")
            .orderBy("dataDisplay", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                listaReservas.clear()
                result.forEach { document ->
                    try {
                        listaReservas.add(Reserva(
                            id = document.id,
                            nomeEvento = document.getString("nome_evento") ?: "",
                            tipoSala = document.getString("tipo_sala") ?: "",
                            dataDisplay = document.getString("data_display") ?: "",
                            usuario = document.getString("usuario") ?: ""
                        ))
                    } catch (e: Exception) {
                        // Ignora documentos inválidos
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar reservas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirReserva(reserva: Reserva) {
        db.collection("agendamentos")
            .document(reserva.id)
            .delete()
            .addOnSuccessListener {
                val position = listaReservas.indexOf(reserva)
                if (position >= 0) {
                    listaReservas.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
                Toast.makeText(this, "Reserva excluída com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir reserva: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}