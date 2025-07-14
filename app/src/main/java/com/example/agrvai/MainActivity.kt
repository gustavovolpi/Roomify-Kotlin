package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agrvai.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val senha = binding.password.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            db.collection("usuarios").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val isAdmin = document.getBoolean("admin") ?: false

                                    if (isAdmin) {
                                        // Vai para a tela de admin
                                        val intent = Intent(this, MainAdminActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Vai para a tela comum
                                        val intent = Intent(this, CalendarioActivity::class.java)
                                        startActivity(intent)
                                    }

                                    finish() // Fecha a tela de login
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.email.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu email para redefinir a senha", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email de redefinição enviado para $email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}