package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainAdminActivity : AppCompatActivity() {

    private lateinit var logoutIcon: ImageView
    private lateinit var gerenciarSalasButton: MaterialButton
    private lateinit var alterarReservaButton: MaterialButton
    private lateinit var gerarRelatButton: MaterialButton
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_adm)

        auth = FirebaseAuth.getInstance()


        // Mostrar nome e email
        mostrarDadosDoAdmin()

        // Views
        logoutIcon = findViewById(R.id.logout_icon)
        gerenciarSalasButton = findViewById(R.id.gerenciar_salas)
        alterarReservaButton = findViewById(R.id.alterar)
        gerarRelatButton = findViewById(R.id.gerar_relat)

        // Logout
        logoutIcon.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        }

        // Gerenciar Salas
        gerenciarSalasButton.setOnClickListener {
            startActivity(Intent(this, CadastroSalasActivity::class.java))
        }

        // Alterar Reserva
        alterarReservaButton.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        // Gerar Relatórios
        gerarRelatButton.setOnClickListener {
            startActivity(Intent(this, RelatorioActivity::class.java))
        }
    }

    private fun mostrarDadosDoAdmin() {
        val nomeTextView = findViewById<TextView>(R.id.username)
        val emailTextView = findViewById<TextView>(R.id.email)

        val user = auth.currentUser
        if (user != null) {
            val nome = user.displayName ?: "Administrador"
            val email = user.email ?: "sem e-mail"
            nomeTextView.text = nome
            emailTextView.text = email

            // --- Opcional: Buscar nome do Firestore se necessário ---
            /*
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(user.uid)
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

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}