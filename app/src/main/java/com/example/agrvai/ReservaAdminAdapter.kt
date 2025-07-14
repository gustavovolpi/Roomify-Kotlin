package com.example.agrvai

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ReservaAdminAdapter(
    private val reservas: List<Reserva>,
    private val onExcluirClick: (Reserva) -> Unit,
    private val onAlterarClick: (Reserva) -> Unit = {},
    private val showAlterarButton: Boolean = true
) : RecyclerView.Adapter<ReservaAdminAdapter.ReservaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount(): Int = reservas.size

    inner class ReservaViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        private val tipoSalaTextView: TextView = itemView.findViewById(R.id.text_tipo_sala)
        private val dataDisplayTextView: TextView = itemView.findViewById(R.id.text_data_display)
        private val nomeEventoTextView: TextView = itemView.findViewById(R.id.text_nome_evento)
        private val usuarioTextView: TextView = itemView.findViewById(R.id.text_usuario)
        private val btnExcluir: Button = itemView.findViewById(R.id.btn_excluir)
        private val btnAlterar: Button = itemView.findViewById(R.id.btn_alterar)

        fun bind(reserva: Reserva) {
            tipoSalaTextView.text = "Sala: ${reserva.tipoSala}"
            dataDisplayTextView.text = "Data: ${reserva.dataDisplay}"
            nomeEventoTextView.text = "Evento: ${reserva.nomeEvento}"
            usuarioTextView.text = "Usuário: ${reserva.usuario}"

            btnAlterar.visibility = if (showAlterarButton) View.VISIBLE else View.GONE
            btnAlterar.setOnClickListener {
                onAlterarClick(reserva)
            }

            btnExcluir.setOnClickListener {
                showDeleteConfirmationDialog(reserva)
            }
        }

        private fun showDeleteConfirmationDialog(reserva: Reserva) {
            AlertDialog.Builder(context)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir '${reserva.nomeEvento}'?")
                .setPositiveButton("Sim") { _, _ ->
                    onExcluirClick(reserva) // Chama a função passada pela Activity
                }
                .setNegativeButton("Não", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
}