package com.example.agrvai

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class RelatorioActivity : AppCompatActivity() {

    private lateinit var spinnerSala: Spinner
    private lateinit var btnGerarRelatorio: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RelatorioAdapter

    private val db = FirebaseFirestore.getInstance()
    private val listaRelatorio = mutableListOf<RelatorioItem>()
    private var isAdmin: Boolean = false

    // Lançador para SAF
    private val salvarPdfLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            gerarPDFParaUri(uri)
        } else {
            Toast.makeText(this, "Nenhum local selecionado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio)

        spinnerSala = findViewById(R.id.select_sala)
        btnGerarRelatorio = findViewById(R.id.gerar_relatorio)
        recyclerView = findViewById(R.id.lista_agendamentos)

        // Inicializa adapter com isAdmin (false por enquanto)
        adapter = RelatorioAdapter(listaRelatorio, isAdmin) { item ->
            cancelarReserva(item.id)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val salas = listOf("Auditório", "Coworking", "Salão", "Sala de Reunião")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, salas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSala.adapter = spinnerAdapter

        btnGerarRelatorio.setOnClickListener {
            gerarRelatorio()
        }

        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        homeIcon.setOnClickListener {
            finish()
        }

        val btnDownloadPdf = findViewById<Button>(R.id.btn_download_pdf)
        btnDownloadPdf.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                verificarPermissoesArmazenamento()
            } else {
                abrirSelecionadorDeArquivo()
            }
        }

        // Busca se usuário é admin no Firestore
        verificarSeUsuarioEhAdmin()
    }

    private fun verificarPermissoesArmazenamento() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        } else {
            salvarPDFNoArmazenamento()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            salvarPDFNoArmazenamento()
        } else {
            Toast.makeText(this, "Permissão de armazenamento negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verificarSeUsuarioEhAdmin() {
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("usuarios").document(usuarioAtual.uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    isAdmin = doc.getBoolean("admin") ?: false
                    adapter = RelatorioAdapter(listaRelatorio, isAdmin) { item ->
                        cancelarReserva(item.id)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this, "Documento do usuário não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar permissões", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gerarRelatorio() {
        val salaSelecionada = spinnerSala.selectedItem?.toString()

        if (salaSelecionada.isNullOrEmpty()) {
            Toast.makeText(this, "Selecione uma sala", Toast.LENGTH_SHORT).show()
            return
        }

        listaRelatorio.clear()
        adapter.notifyDataSetChanged()

        db.collection("agendamentos")
            .whereEqualTo("tipo_sala", salaSelecionada)
            .get()
            .addOnSuccessListener { agendamentos ->
                val totalAgendamentos = agendamentos.size()
                if (totalAgendamentos == 0) {
                    Toast.makeText(this, "Nenhum agendamento encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                var agendamentosProcessados = 0

                for (doc in agendamentos) {
                    val emailUsuario = doc.getString("usuario") ?: ""
                    val data = doc.getString("data_display") ?: ""
                    val nomeEvento = doc.getString("nome_evento") ?: ""

                    db.collection("usuarios")
                        .whereEqualTo("email", emailUsuario)
                        .get()
                        .addOnSuccessListener { usuarios ->
                            for (usuarioDoc in usuarios) {
                                val nome = usuarioDoc.getString("nome") ?: ""
                                val telefone = usuarioDoc.getString("telefone") ?: ""

                                val item = RelatorioItem(
                                    nome = nome,
                                    email = emailUsuario,
                                    telefone = telefone,
                                    sala = salaSelecionada,
                                    data = data,
                                    evento = nomeEvento,
                                    id = doc.id
                                )
                                listaRelatorio.add(item)
                            }
                            agendamentosProcessados++
                            if (agendamentosProcessados == totalAgendamentos) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao buscar informações do usuário", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao gerar relatório", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelarReserva(id: String) {
        db.collection("agendamentos").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Reserva cancelada com sucesso", Toast.LENGTH_SHORT).show()
                val index = listaRelatorio.indexOfFirst { it.id == id }
                if (index != -1) {
                    listaRelatorio.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cancelar reserva", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirSelecionadorDeArquivo() {
        salvarPdfLauncher.launch("Relatorio_Salas.pdf")
    }

    private fun salvarPDFNoArmazenamento() {
        val directory = getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
        if (directory == null || !directory.exists()) {
            Toast.makeText(this, "Diretório de armazenamento não disponível", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(directory, "Relatorio_Salas.pdf")
        gerarPDF(file.outputStream())

        try {
            if (file.exists() && file.length() > 0) {
                Toast.makeText(this, "PDF salvo em: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                // Tentar abrir o PDF
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(intent, "Abrir PDF"))
            } else {
                Toast.makeText(this, "PDF gerado, mas parece estar vazio", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("RelatorioActivity", "Erro ao abrir PDF", e)
            Toast.makeText(this, "Erro ao abrir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun gerarPDFParaUri(uri: Uri) {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            gerarPDF(outputStream)
            Toast.makeText(this, "PDF salvo com sucesso", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(this, "Erro ao acessar o local de armazenamento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gerarPDF(outputStream: OutputStream) {
        if (listaRelatorio.isEmpty()) {
            Toast.makeText(this, "Não há dados para gerar PDF", Toast.LENGTH_SHORT).show()
            return
        }

        val document = PdfDocument()
        try {
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint()
            paint.textSize = 16f
            paint.isFakeBoldText = true

            var y = 30f
            canvas.drawText("Relatório de Salas", 20f, y, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false
            y += 30f

            for (item in listaRelatorio) {
                val linha = "Sala: ${item.sala} | Evento: ${item.evento} | Data: ${item.data} | Usuário: ${item.nome}"
                canvas.drawText(linha, 20f, y, paint)
                y += 20f
                if (y > pageInfo.pageHeight - 30) break
            }

            document.finishPage(page)
            document.writeTo(outputStream)
        } catch (e: IOException) {
            Log.e("RelatorioActivity", "Erro ao salvar PDF", e)
            Toast.makeText(this, "Erro ao salvar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }
}