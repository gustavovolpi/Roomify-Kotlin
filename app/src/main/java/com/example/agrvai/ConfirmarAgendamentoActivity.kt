package com.example.agrvai

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

class ConfirmarAgendamentoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "AgendamentoDebug"

    // Configurações do servidor de e-mail (Gmail)
    private val EMAIL_SENDER = "roomify.suporte@gmail.com" // Substitua pelo seu e-mail
    private val EMAIL_PASSWORD = "akds pfge kblp ndfz" // Substitua pela senha de aplicativo do Gmail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirmar_agendamento)

        val dataSelecionadaStr = intent.getStringExtra("data") ?: run {
            Toast.makeText(this, "Data não informada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val salaSelecionada = intent.getStringExtra("sala") ?: run {
            Toast.makeText(this, "Sala não informada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configuração dos componentes da UI
        val dataTextView = findViewById<TextView>(R.id.data_valor)
        val nomeEvento = findViewById<EditText>(R.id.input_event_name)
        val checkEmail = findViewById<CheckBox>(R.id.email_notification_checkbox)
        val btnConfirmar = findViewById<Button>(R.id.confirmar_button)
        val valorSalaTextView = findViewById<TextView>(R.id.valor_valor)

        dataTextView.text = dataSelecionadaStr
        fetchRoomValue(salaSelecionada, valorSalaTextView)

        btnConfirmar.setOnClickListener {
            val nomeEventoStr = nomeEvento.text.toString().trim()

            if (nomeEventoStr.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o nome do evento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Converter a data para Timestamp considerando o fuso horário local
            val (dataTimestamp, dataFormatada) = try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = dateFormat.parse(dataSelecionadaStr) ?: Date()

                val calendar = Calendar.getInstance().apply {
                    time = date
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                Pair(Timestamp(calendar.time), dateFormat.format(calendar.time))
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao converter data: ${e.message}")
                Toast.makeText(this, "Erro no formato da data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Data selecionada: $dataSelecionadaStr | Data convertida: ${dataFormatada} | Timestamp: $dataTimestamp")

            // Verificar disponibilidade
            verificarDisponibilidadeSala(salaSelecionada, dataTimestamp) { disponivel ->
                if (!disponivel) {
                    Toast.makeText(this, "Sala já agendada para esta data", Toast.LENGTH_LONG).show()
                    return@verificarDisponibilidadeSala
                }

                // Processar agendamento
                val valorSala = valorSalaTextView.text.toString()
                    .replace("R$", "")
                    .trim()
                    .toDoubleOrNull() ?: 0.0

                val novoAgendamento = hashMapOf(
                    "usuario" to auth.currentUser?.email,
                    "nome_evento" to nomeEventoStr,
                    "data" to dataTimestamp,
                    "tipo_sala" to salaSelecionada,
                    "valor" to valorSala,
                    "recebe_email" to checkEmail.isChecked,
                    "data_criacao" to Timestamp.now(),
                    "data_display" to dataSelecionadaStr
                )

                db.collection("agendamentos")
                    .add(novoAgendamento)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Agendamento realizado para $dataFormatada!", Toast.LENGTH_SHORT).show()

                        // Enviar e-mail se o CheckBox estiver marcado
                        if (checkEmail.isChecked) {
                            val userEmail = auth.currentUser?.email ?: ""
                            enviarEmailConfirmacao(userEmail, nomeEventoStr, salaSelecionada, dataSelecionadaStr, valorSala)
                        }

                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Falha ao agendar: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Erro ao agendar: ${e.message}")
                    }
            }
        }
    }

    private fun fetchRoomValue(sala: String, textView: TextView) {
        db.collection("salas")
            .whereEqualTo("nome", sala)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    textView.text = "R$ 0.00"
                    return@addOnSuccessListener
                }
                val preco = result.documents[0].getDouble("preco") ?: 0.0
                textView.text = "R$ %.2f".format(preco)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao buscar sala: ${e.message}")
                textView.text = "R$ 0.00"
            }
    }

    private fun verificarDisponibilidadeSala(sala: String, data: Timestamp, callback: (Boolean) -> Unit) {
        db.collection("agendamentos")
            .whereEqualTo("tipo_sala", sala)
            .whereEqualTo("data", data)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro na verificação: ${e.message}")
                callback(false)
            }
    }

    private fun enviarEmailConfirmacao(to: String, nomeEvento: String, sala: String, data: String, valor: Double) {
        thread {
            try {
                // Configurações do servidor SMTP do Gmail
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                // Criar sessão com autenticação
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD)
                    }
                })

                // Criar mensagem de e-mail
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL_SENDER, "RoomiFy"))
                    addRecipient(Message.RecipientType.TO, InternetAddress(to))
                    subject = "Confirmação de Reserva - RoomiFy"
                    setContent(
                        """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: #3F826D; text-align: center;">Reserva Confirmada!</h2>
                            <p>Olá!</p>
                            <p>Estamos felizes em confirmar sua reserva com a RoomiFy! Aqui estão os detalhes do seu agendamento:</p>
                            <ul style="list-style: none; padding: 0;">
                                <li><strong>Evento:</strong> $nomeEvento</li>
                                <li><strong>Sala:</strong> $sala</li>
                                <li><strong>Data:</strong> $data</li>
                                <li><strong>Valor:</strong> R$ ${"%.2f".format(valor)}</li>
                            </ul>
                            <p>Prepare-se para um evento incrível! Se precisar de alguma coisa, é só nos chamar.</p>
                            <p style="margin-top: 20px;">Atenciosamente,<br><strong>Equipe RoomiFy</strong></p>
                            <p style="font-size: 12px; color: #888; text-align: center;">Este é um e-mail automático, por favor, não responda diretamente.</p>
                        </div>
                        """.trimIndent(),
                        "text/html; charset=utf-8"
                    )
                }

                // Enviar e-mail
                Transport.send(message)

                // Mostrar Toast na thread principal
                runOnUiThread {
                    Toast.makeText(this, "E-mail de confirmação enviado!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar e-mail: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Erro ao enviar e-mail: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}