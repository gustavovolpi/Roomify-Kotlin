package com.example.agrvai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agrvai.databinding.ActivityCadastrarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class CadastrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        aplicarMascaraCPF()
        aplicarMascaraTelefone()

        binding.registerButton.setOnClickListener {
            val nome = binding.name.text.toString().trim()
            val cpf = binding.cpf.text.toString().filter { it.isDigit() }
            val telefone = binding.phone.text.toString().filter { it.isDigit() }
            val email = binding.email.text.toString().trim()
            val senha = binding.password.text.toString().trim()
            val confirmaSenha = binding.passwordConfirm.text.toString().trim()

            if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() ||
                email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isCPFValido(cpf)) {
                Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isTelefoneValido(telefone)) {
                Toast.makeText(this, "Telefone inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmaSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid

                        if (uid == null) {
                            Toast.makeText(this, "Erro ao obter UID do usuário", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        val user = auth.currentUser // Obtenha o usuário logado

                        // Atualiza o nome de exibição do usuário
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nome)  // Defina o nome desejado aqui
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Log.d("CadastrarActivity", "Nome de usuário atualizado com sucesso!")
                                } else {
                                    Log.e("CadastrarActivity", "Erro ao atualizar o nome de usuário")
                                }
                            }

                        val cpfFormatado = formatarCPF(cpf)
                        val telefoneFormatado = formatarTelefone(telefone)

                        val userMap = hashMapOf(
                            "nome" to nome,
                            "cpf" to cpfFormatado,
                            "telefone" to telefoneFormatado,
                            "email" to email
                        )

                        db.collection("usuarios").document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Conta criada e dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao salvar no Firestore", Toast.LENGTH_SHORT).show()
                                Log.e("FIREBASE", "Erro ao salvar no Firestore", it)
                            }
                    } else {
                        Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("FIREBASE", "Erro ao criar usuário", task.exception)
                    }
                }
        }
    }

    private fun aplicarMascaraCPF() {
        binding.cpf.addTextChangedListener(object : TextWatcher {
            var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().filter { it.isDigit() }

                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var formatted = ""
                var i = 0

                for (m in "###.###.###-##") {
                    if (m == '#') {
                        if (i < str.length) formatted += str[i++] else break
                    } else {
                        if (i < str.length) formatted += m
                    }
                }

                isUpdating = true
                binding.cpf.setText(formatted)
                binding.cpf.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun aplicarMascaraTelefone() {
        binding.phone.addTextChangedListener(object : TextWatcher {
            var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().filter { it.isDigit() }

                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var formatted = ""
                var i = 0
                val mask = if (str.length > 10) "(##) #####-####" else "(##) ####-####"

                for (m in mask) {
                    if (m == '#') {
                        if (i < str.length) formatted += str[i++] else break
                    } else {
                        if (i < str.length) formatted += m
                    }
                }

                isUpdating = true
                binding.phone.setText(formatted)
                binding.phone.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isCPFValido(cpf: String): Boolean {
        val cpfClean = cpf.replace(Regex("[^\\d]"), "")
        if (cpfClean.length != 11 || cpfClean.all { it == cpfClean[0] }) return false

        try {
            val numbers = cpfClean.map { it.toString().toInt() }

            val dv1 = (0..8).map { (10 - it) * numbers[it] }.sum() % 11
            val check1 = if (dv1 < 2) 0 else 11 - dv1
            if (check1 != numbers[9]) return false

            val dv2 = (0..9).map { (11 - it) * numbers[it] }.sum() % 11
            val check2 = if (dv2 < 2) 0 else 11 - dv2
            return check2 == numbers[10]
        } catch (e: Exception) {
            return false
        }
    }

    private fun isTelefoneValido(telefone: String): Boolean {
        val telefoneClean = telefone.replace(Regex("[^\\d]"), "")
        return telefoneClean.length in 10..11
    }

    private fun formatarCPF(cpf: String): String {
        val cpfClean = cpf.replace(Regex("[^\\d]"), "")
        return "${cpfClean.substring(0,3)}.${cpfClean.substring(3,6)}.${cpfClean.substring(6,9)}-${cpfClean.substring(9)}"
    }

    private fun formatarTelefone(telefone: String): String {
        val t = telefone.replace(Regex("[^\\d]"), "")
        return if (t.length == 11)
            "(${t.substring(0,2)}) ${t.substring(2,7)}-${t.substring(7)}"
        else
            "(${t.substring(0,2)}) ${t.substring(2,6)}-${t.substring(6)}"
    }
}