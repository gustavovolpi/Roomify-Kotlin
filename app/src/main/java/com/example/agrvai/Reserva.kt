package com.example.agrvai

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reserva(
    @DocumentId val id: String = "",
    var nomeEvento: String = "",
    val tipoSala: String = "",
    var dataDisplay: String = "",
    val usuario: String = ""
) : Parcelable