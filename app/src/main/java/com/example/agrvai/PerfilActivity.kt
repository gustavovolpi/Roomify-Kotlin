package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var nomeTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeIcon: ImageView
    private lateinit var logoutIcon: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val listaReservas = mutableListOf<RelatorioItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil)

        nomeTextView = findViewById(R.id.username)
        emailTextView = findViewById(R.id.email)
        recyclerView = findViewById(R.id.recycler_reservas)
        homeIcon = findViewById(R.id.home_icon)
        logoutIcon = findViewById(R.id.logout_icon)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val emailUsuario = user.email ?: return
        val nomeUsuario = user.displayName ?: "Usuário"

        nomeTextView.text = nomeUsuario
        emailTextView.text = emailUsuario

        recyclerView.layoutManager = LinearLayoutManager(this)

        firestore.collection("usuarios")
            .whereEqualTo("email", emailUsuario)
            .get()
            .addOnSuccessListener { docs ->
                val isAdmin = if (!docs.isEmpty) {
                    docs.documents[0].getBoolean("admin") ?: false
                } else {
                    false
                }

                carregarReservas(emailUsuario, nomeUsuario, isAdmin)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar permissão do usuário", Toast.LENGTH_SHORT).show()
            }

        homeIcon.setOnClickListener {
            startActivity(Intent(this, CalendarioActivity::class.java))
            finish()
        }

        logoutIcon.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun carregarReservas(emailUsuario: String, nomeUsuario: String, isAdmin: Boolean) {
        firestore.collection("agendamentos")
            .whereEqualTo("usuario", emailUsuario)
            .get()
            .addOnSuccessListener { result ->
                listaReservas.clear()
                for (document in result) {
                    val id = document.id
                    val nome = nomeUsuario
                    val email = emailUsuario
                    val telefone = document.getString("telefone") ?: ""
                    val sala = document.getString("tipo_sala") ?: ""
                    val data = document.getString("data_display") ?: ""
                    val evento = document.getString("nome_evento") ?: ""

                    listaReservas.add(RelatorioItem(id, nome, email, telefone, sala, data, evento))
                }

                val adapter = RelatorioAdapter(listaReservas, isAdmin) { reserva ->
                    mostrarDialogoConfirmacao(reserva)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelarReserva(reserva: RelatorioItem) {
        firestore.collection("agendamentos").document(reserva.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Reserva cancelada com sucesso", Toast.LENGTH_SHORT).show()
                listaReservas.remove(reserva)
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cancelar reserva", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoConfirmacao(reserva: RelatorioItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cancelar Reserva")
        builder.setMessage("Tem certeza que deseja cancelar esta reserva?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            cancelarReserva(reserva)
            dialog.dismiss()
        }

        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}