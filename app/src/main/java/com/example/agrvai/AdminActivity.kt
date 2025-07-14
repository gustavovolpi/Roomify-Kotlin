package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var reservaAdapter: ReservaAdminAdapter
    private val listaReservas = mutableListOf<Reserva>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin)

        // Firebase
        auth = Firebase.auth

        // Mostrar nome e email do admin
        mostrarDadosDoAdmin()

        // RecyclerView
        setupRecyclerView()

        // Botões
        setupClickListeners()

        // Carregar reservas
        carregarReservas()
    }

    private fun mostrarDadosDoAdmin() {
        val nomeTextView = findViewById<TextView>(R.id.username)
        val emailTextView = findViewById<TextView>(R.id.email)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val nome = user.displayName ?: "Administrador"
            val email = user.email ?: "sem e-mail"
            nomeTextView.text = nome
            emailTextView.text = email

            // --- Se quiser buscar do Firestore (caso nome esteja salvo lá) ---
            /*
            val uid = user.uid
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val nome = doc.getString("nome") ?: "Administrador"
                    val email = doc.getString("email") ?: user.email ?: "sem e-mail"
                    nomeTextView.text = nome
                    emailTextView.text = email
                }
                .addOnFailureListener {
                    nomeTextView.text = "Erro ao carregar nome"
                    emailTextView.text = user.email ?: "sem e-mail"
                }
             */
        } else {
            nomeTextView.text = "Usuário não logado"
            emailTextView.text = "sem dados"
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        reservaAdapter = ReservaAdminAdapter(
            reservas = listaReservas,
            onExcluirClick = { showDeleteConfirmation(it) },
            onAlterarClick = { abrirTelaAlteracao(it) }
        )
        recyclerView.adapter = reservaAdapter
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.home_icon).setOnClickListener {
            Intent(this, MainAdminActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
            }
            finish()
        }

        findViewById<ImageView>(R.id.logout_icon).setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Confirmar Logout")
                setMessage("Deseja realmente sair?")
                setPositiveButton("Sim") { _, _ -> performLogout() }
                setNegativeButton("Cancelar", null)
                show()
            }
        }
    }

    private fun performLogout() {
        auth.signOut()
        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }

    private fun carregarReservas() {
        db.collection("agendamentos")
            .get()
            .addOnSuccessListener { result ->
                listaReservas.clear()
                result.forEach { document ->
                    listaReservas.add(
                        Reserva(
                            id = document.id,
                            nomeEvento = document.getString("nome_evento") ?: "",
                            tipoSala = document.getString("tipo_sala") ?: "",
                            dataDisplay = document.getString("data_display") ?: "",
                            usuario = document.getString("usuario") ?: ""
                        )
                    )
                }
                reservaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar reservas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation(reserva: Reserva) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Deseja excluir a reserva '${reserva.nomeEvento}'?")
            .setPositiveButton("Excluir") { _, _ -> excluirReserva(reserva) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirReserva(reserva: Reserva) {
        db.collection("agendamentos")
            .document(reserva.id)
            .delete()
            .addOnSuccessListener {
                listaReservas.remove(reserva)
                reservaAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Reserva excluída", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirTelaAlteracao(reserva: Reserva) {
        Intent(this, AlterarReservasActivity::class.java).apply {
            putExtra("reserva", reserva)
            startActivity(this)
        }
    }
}