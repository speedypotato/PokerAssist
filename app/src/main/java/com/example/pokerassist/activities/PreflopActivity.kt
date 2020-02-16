package com.example.pokerassist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pokerassist.BetEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.activities.SelectActivity.Companion.selectedCards
import kotlinx.android.synthetic.main.activity_preflop.*
import java.util.*

class PreflopActivity : AppCompatActivity() {
    companion object {
        const val random = 0.15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preflop)

        initDrawn()

        updateView(determineRFI())
    }

    /**
     * Gets from intent, sorts [0] high [1] low
     */
    private fun initDrawn() {
        val intentCards = intent.getParcelableArrayListExtra<CardModel>(selectedCards)
        if (intentCards != null) {
            drawnCards = intentCards
            if (drawnCards[0].number < drawnCards[1].number && drawnCards[0].number != 1)
                drawnCards[0] = drawnCards[1].also { drawnCards[1] = drawnCards[0] }
        }
    }

    private fun isSuited(): Boolean {
        return drawnCards[0].suit == drawnCards[1].suit
    }

    /**
     * Hardcode for now
     */
    private fun determineRFI(): BetEnum {
        return if (drawnCards[0].number == drawnCards[1].number) { //pocket pairs
            when (drawnCards[0].number) {
                1, 12, 13 -> BetEnum.RAISE
                10, 11 -> BetEnum.RAISE_CALL
                in 2..9 -> BetEnum.CALL
                else -> BetEnum.FOLD
            }
        } else {
            when (drawnCards[0].number) {    //is return needed here?
                1 -> when (drawnCards[1].number) {
                    13 -> BetEnum.RAISE
                    12 -> BetEnum.RAISE_CALL
                    11 -> if (isSuited()) BetEnum.RAISE_CALL else BetEnum.RAISE_CALL_FOLD
                    10, 9 -> if (isSuited()) BetEnum.CALL else BetEnum.FOLD
                    8, 7, 6 -> if (isSuited()) BetEnum.RAISE_FOLD else BetEnum.FOLD
                    5, 4, 3, 2 -> if (isSuited()) BetEnum.RAISE else BetEnum.FOLD
                    else -> BetEnum.FOLD
                }
                13 -> when (drawnCards[1].number) {
                    12 -> if (isSuited()) BetEnum.RAISE_CALL else BetEnum.CALL_FOLD
                    11, 10 -> if (isSuited()) BetEnum.CALL else BetEnum.FOLD
                    else -> BetEnum.FOLD
                }
                12 -> when (drawnCards[1].number) {
                    11, 10 -> if (isSuited()) BetEnum.CALL else BetEnum.FOLD
                    else -> BetEnum.FOLD
                }
                11 -> when (drawnCards[1].number) {
                    10 -> if (isSuited()) BetEnum.CALL else BetEnum.FOLD
                    9 -> if (isSuited()) BetEnum.CALL_FOLD else BetEnum.FOLD
                    else -> BetEnum.FOLD
                }
                10 -> if (drawnCards[1].number == 9 && isSuited()) BetEnum.CALL else BetEnum.FOLD
                9 -> if (drawnCards[1].number == 8 && isSuited()) BetEnum.CALL else BetEnum.FOLD
                8 -> if (drawnCards[1].number == 7 && isSuited()) BetEnum.CALL else BetEnum.FOLD
                7 -> if (drawnCards[1].number == 6 && isSuited()) BetEnum.CALL else BetEnum.FOLD
                6 -> if (drawnCards[1].number == 5 && isSuited()) BetEnum.RAISE else BetEnum.FOLD
                5 -> if (drawnCards[1].number == 4 && isSuited()) BetEnum.RAISE else BetEnum.FOLD
                else -> BetEnum.FOLD
            }
        }
    }

    private fun updateView(res: BetEnum) {
        outputTextView.text = when (res) {
            BetEnum.RAISE -> resources.getString(R.string.raise)
            BetEnum.RAISE_FOLD -> resources.getString(R.string.raise_fold)
            BetEnum.RAISE_CALL -> resources.getString(R.string.raise_call)
            BetEnum.RAISE_CALL_FOLD -> resources.getString(R.string.raise_call_fold)
            BetEnum.CALL -> resources.getString(R.string.call)
            BetEnum.CALL_FOLD -> resources.getString(R.string.call_fold)
            BetEnum.FOLD -> resources.getString(R.string.fold)
        }
    }

    private lateinit var drawnCards: ArrayList<CardModel>
}
