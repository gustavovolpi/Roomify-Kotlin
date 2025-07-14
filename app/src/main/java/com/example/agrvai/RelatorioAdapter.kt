package com.example.agrvai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RelatorioAdapter(
    private val lista: List<RelatorioItem>,
    private val isAdmin: Boolean,
    private val onCancelarClick: (RelatorioItem) -> Unit
) : RecyclerView.Adapter<RelatorioAdapter.RelatorioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatorioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relatorio, parent, false)
        return RelatorioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RelatorioViewHolder, position: Int) {
        val item = lista[position]

        holder.nomeUsuario.text = item.nome
        holder.emailUsuario.text = item.email
        holder.telefoneUsuario.text = item.telefone
        holder.sala.text = item.sala
        holder.data.text = item.data
        holder.evento.text = item.evento

        if (isAdmin) {
            holder.btnCancelar.visibility = View.GONE
        } else {
            holder.btnCancelar.visibility = View.VISIBLE
            holder.btnCancelar.setOnClickListener {
                onCancelarClick(item)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    class RelatorioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeUsuario: TextView = itemView.findViewById(R.id.nome_usuario)
        val emailUsuario: TextView = itemView.findViewById(R.id.email_usuario)
        val telefoneUsuario: TextView = itemView.findViewById(R.id.telefone_usuario)
        val sala: TextView = itemView.findViewById(R.id.sala)
        val data: TextView = itemView.findViewById(R.id.data)
        val evento: TextView = itemView.findViewById(R.id.evento)
        val btnCancelar: Button = itemView.findViewById(R.id.btn_cancelar)
    }
}