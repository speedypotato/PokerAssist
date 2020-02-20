package com.example.pokerassist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardModel(val number: Int, val suit: SuitEnum, var selected: Boolean) : Parcelable, Comparable<CardModel> {
    override fun compareTo(other: CardModel): Int {
        return if (number == 1)
            if (other.number != 1) 14 - other.number
            else suit.compareTo(other.suit)
        else if (number == other.number) suit.compareTo(other.suit)
        else number - other.number
    }

    override fun equals(other: Any?): Boolean {
        return if (other is CardModel)
            return compareTo(other) == 0
        else false
    }

    override fun hashCode(): Int {
        return (number.toString() + suit.suit).hashCode()
    }

    override fun toString(): String {
        return number.toString() + suit.suit
    }
}