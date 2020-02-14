package com.example.pokerassist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardModel(val number: Int, val suit: SuitEnum, var selected: Boolean) : Parcelable