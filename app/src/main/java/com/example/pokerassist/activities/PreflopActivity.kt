package com.example.pokerassist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pokerassist.*
import kotlinx.android.synthetic.main.activity_preflop.*
import java.util.*

class PreflopActivity : AppCompatActivity() {
    companion object {
        const val randomPerc = 15
        const val straightDiff = 3  //allowed card diff + 1 to try for straight
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preflop)

        initDrawn()

        determineRFI().let {
            updateView(it)
            determineFudge(it)
        }

        foldButton.setOnClickListener {
            foldSubmit()
        }

        proceedButton.setOnClickListener {
            proceedSubmit()
        }
    }

    /**
     * Gets from intent, sorts [0] high [1] low
     */
    private fun initDrawn() {
        val intentCards = intent.getParcelableArrayListExtra<CardModel>(resources.getString(R.string.selected_cards_tag))
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
     * Hardcoded table
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

    /**
     * Randomizer suggestion
     */
    private fun determineFudge(res: BetEnum) {
        val randomNum = (1..100).random()
        if (res == BetEnum.FOLD && randomPerc >= randomNum) {
            if (drawnCards[0].number != 1 && drawnCards[0].number - drawnCards[1].number <= straightDiff)  //straight
                fudgeTextView.text = resources.getString(R.string.fudge_straight)
            else if (drawnCards[0].number == 1 && drawnCards[1].number - drawnCards[0].number <= straightDiff)
                fudgeTextView.text = resources.getString(R.string.fudge_straight)
            else if (isSuited()) //flush
                    fudgeTextView.text = resources.getString(R.string.fudge_flush)
        }
    }

    /**
     * Main user suggestion view
     */
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

    /**
     * Fold onClick puts user back to select screen
     */
    private fun foldSubmit() {
        var cards = ArrayList<ArrayList<CardModel>>().apply {
            for (suit in SuitEnum.values()) {
                add(ArrayList<CardModel>().apply {
                    for (i in 1..SplashActivity.POKER_NUMBER)
                        add(CardModel(i, suit, false))
                })
            }
        }
        startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
            putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_drawn_cards))
            putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.PREFLOP)
            putExtra(resources.getString(R.string.num_sel_tag), 2)
            for (cardList in cards) {
                if (cardList.size > 0)
                    putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
            }
        })
        finish()
    }

    /**
     * Proceed onClick moves on to SelectActivity -> RiverActivity
     */
    private fun proceedSubmit() {
        var cards = ArrayList<ArrayList<CardModel>>().apply {
            for (suit in SuitEnum.values()) {
                add(ArrayList<CardModel>().apply {
                    for (i in 1..SplashActivity.POKER_NUMBER)
                        if (!drawnCards.contains(CardModel(i, suit, false)))
                            add(CardModel(i, suit, false))
                })
            }
        }
        startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
            putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_river_rards))
            putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.RIVER)
            putExtra(resources.getString(R.string.num_sel_tag), 3)
            putParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag), drawnCards)
            for (cardList in cards) {
                if (cardList.size > 0)
                    putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
            }
        })
        finish()
    }

    private lateinit var drawnCards: ArrayList<CardModel>
}
