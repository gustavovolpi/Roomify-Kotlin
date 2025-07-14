package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CadastroSalasActivity : AppCompatActivity() {

    private lateinit var roomTypeSpinner: Spinner
    private lateinit var roomPriceEditText: EditText
    private lateinit var saveRoomButton: MaterialButton
    private lateinit var homeIcon: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_salas)

        // Inicializando os componentes da tela
        roomTypeSpinner = findViewById(R.id.room_type_spinner)
        roomPriceEditText = findViewById(R.id.room_price)
        saveRoomButton = findViewById(R.id.save_room_button)
        homeIcon = findViewById(R.id.home_icon)

        // Configurando o Spinner com os tipos de salas
        val tiposDeSala = listOf("Sala de Reunião", "Auditório", "Coworking", "Salão")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDeSala)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roomTypeSpinner.adapter = adapter

        // Configurando o botão de salvar
        saveRoomButton.setOnClickListener {
            // Pegar o tipo de sala do Spinner
            val tipoSala = roomTypeSpinner.selectedItem.toString()

            // Pegar o preço do EditText e validar
            val precoStr = roomPriceEditText.text.toString().trim()
            if (precoStr.isEmpty()) {
                // Se o preço estiver vazio, mostrar erro
                Toast.makeText(this, "Por favor, insira o valor do aluguel", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertendo o preço para Double
            val preco = precoStr.toDoubleOrNull()
            if (preco == null) {
                // Se a conversão falhar (preço inválido)
                Toast.makeText(this, "Por favor, insira um valor válido para o preço", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criando um mapa para salvar no Firestore
            val sala = hashMapOf(
                "nome" to tipoSala,
                "preco" to preco
            )

            // Verificar se a sala já existe no Firestore
            db.collection("salas")
                .whereEqualTo("nome", tipoSala)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Sala existe, atualizar o preço do primeiro documento encontrado
                        val documentId = documents.documents[0].id
                        db.collection("salas")
                            .document(documentId)
                            .set(sala, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Preço da sala $tipoSala atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                // Limpar os campos após atualizar
                                roomPriceEditText.text.clear()
                                roomTypeSpinner.setSelection(0)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao atualizar sala: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Sala não existe, criar nova
                        db.collection("salas")
                            .add(sala)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Sala $tipoSala cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                                // Limpar os campos após salvar
                                roomPriceEditText.text.clear()
                                roomTypeSpinner.setSelection(0)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao cadastrar sala: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao verificar sala: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Configurando o clique do ícone Home para navegar para MainAdminActivity
        homeIcon.setOnClickListener {
            Intent(this, MainAdminActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
            }
            finish()
        }
    }
}