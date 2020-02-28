package com.example.pokerassist.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_river.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.max

//TODO: figure out actual probabilities
class RiverActivity : AppCompatActivity() {
    companion object {
        const val numCards = 52
        const val numDealt = 2
        const val maxVisible = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_river)

        initPlayers()

        visibleCards = ArrayList()
        initCards()

        initTitle()

        foldButton2.setOnClickListener { foldSubmit() }
        proceedButton2.setOnClickListener { proceedSubmit() }

        updateProbView(royalFlushTextView, royalFlushProb())

        updateProbView(fourKindTextView, fourKindProb())

        updateProbView(flushTextView, flushProb())
//        updateProbView(straightTextView, straightProb())
        updateProbView(threeKindTextview, threeKindProb())
        updateProbView(twoPairTextView, twoPairProb())
        updateProbView(onePairTextView, onePairProb())
        updateProbView(higherCardTextView, highCardProb())
    }

    /**
     * Changes title based on number of cards visible
     */
    private fun initTitle() {
        when(visibleCards.size) {
            5 -> riverTextView2.text = resources.getString(R.string.flop_probabilities)
            6 -> riverTextView2.text = resources.getString(R.string.turn_probabilities)
            7 -> riverTextView2.text = resources.getString(R.string.river_probabilities)
            else -> riverTextView2.text = resources.getString(R.string.error)
        }
    }

    /**
     * Get player count from settings
     */
    private fun initPlayers() {
        val sharedPref = getSharedPreferences(getString(R.string.settings_file_key), Context.MODE_PRIVATE)
        players = sharedPref.getInt(resources.getString(R.string.players), SettingsActivity.defaultPlayers)
    }

    /**
     * Get cards and card count from intent
     */
    private fun initCards() {
        visibleCards.addAll(intent.getParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag)) ?: ArrayList())
        visibleCards.addAll(intent.getParcelableArrayListExtra(resources.getString(R.string.selected_cards_tag)) ?: ArrayList())
        visibleCards.sort()
        cardsLeft = numCards - (players * numDealt) - (visibleCards.size - 1)
//        for (card in visibleCards)
//            Toast.makeText(this, visibleCards.toString(), Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "numcards:" + numCards.toString() + " players*numDealt:" + (players * numDealt).toString() + " visibleCards.size:" + (visibleCards.size - 1).toString(), Toast.LENGTH_LONG).show()
    }

    /**
     * Fold onClick resets to card select screen
     */
    private fun foldSubmit() {
        var cards = java.util.ArrayList<java.util.ArrayList<CardModel>>().apply {
            for (suit in SuitEnum.values()) {
                add(java.util.ArrayList<CardModel>().apply {
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
     * Proceed onClick moves on to the next step
     */
    private fun proceedSubmit() {
        when(visibleCards.size) {
            5 -> {
                val cards = java.util.ArrayList<java.util.ArrayList<CardModel>>().apply {
                    for (suit in SuitEnum.values()) {
                        add(java.util.ArrayList<CardModel>().apply {
                            for (i in 1..SplashActivity.POKER_NUMBER)
                                if (!visibleCards.contains(CardModel(i, suit, false)))
                                    add(CardModel(i, suit, false))
                        })
                    }
                }
                startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
                    putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_turn_cards))
                    putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.RIVER)
                    putExtra(resources.getString(R.string.num_sel_tag), 1)
                    putParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag), visibleCards)
                    for (cardList in cards) {
                        if (cardList.size > 0)
                            putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
                    }
                })
                finish()
            }
            6 -> {
                val cards = java.util.ArrayList<java.util.ArrayList<CardModel>>().apply {
                    for (suit in SuitEnum.values()) {
                        add(java.util.ArrayList<CardModel>().apply {
                            for (i in 1..SplashActivity.POKER_NUMBER)
                                if (!visibleCards.contains(CardModel(i, suit, false)))
                                    add(CardModel(i, suit, false))
                        })
                    }
                }
                startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
                    putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_river_cards))
                    putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.RIVER)
                    putExtra(resources.getString(R.string.num_sel_tag), 1)
                    putParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag), visibleCards)
                    for (cardList in cards) {
                        if (cardList.size > 0)
                            putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
                    }
                })
                finish()
            }
            else -> foldSubmit()
        }
    }

    private fun updateProbView(v: TextView, prob: Double) {
        if (prob == 1.0)
            v.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        else if (prob == 0.0)
            v.setTextColor(ContextCompat.getColor(this, R.color.colorAccentRed))
        v.text = (prob * 100).toString()
    }

    private fun royalFlushProb() : Double {
        val cardVals = mutableSetOf(10, 11, 12, 13, 1)
        val curSuit = visibleCards[0].suit
        for (i in 0 until visibleCards.size) {
            if (visibleCards[i].suit != curSuit) {
                return 0.0
            }
            cardVals.remove(visibleCards[i].number)
        }

        return when (cardVals.size) {
            0 -> 1.0
            1 -> 1.0 / (numCards.toDouble() - visibleCards.size.toDouble())
            2 -> (2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                    (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1))
            else -> 0.0
        }
    }

    private fun straightFlushProb() : Double {
        return 0.0
    }

    private fun fourKindProb() : Double {
        var freqMap = HashMap<Int, Int>()
        for (i in visibleCards) {
            freqMap[i.number] = (freqMap[i.number] ?: 0) + 1
            if ((freqMap[i.number] ?: 0) >= 4)
                return 1.0
        }

        var prob = 0.0
        for (value in freqMap.values) {
            if (value == 2 && visibleCards.size == 5) {   //pair exists
                prob += (2 / (numCards.toDouble() - visibleCards.size.toDouble())) * (1 /
                        (numCards.toDouble() - visibleCards.size.toDouble() - 1))
            } else if (value == 3) {    //triple exists
                when (visibleCards.size) {
                    5 -> prob += 1 / (numCards.toDouble() - visibleCards.size.toDouble()) +
                            1 / (numCards.toDouble() - visibleCards.size.toDouble() - 1)
                    6 -> prob += 1 / (numCards.toDouble() - visibleCards.size.toDouble())
                }
            }
        }
        return prob
    }

    private fun fullHouseProb() : Double {
        return 0.0
    }

    /**
     * Probability of a flush
     */
    private fun flushProb() : Double {
        var probability = 0.0
        val suitFreq = HashMap<SuitEnum, Int>()
        for (i in visibleCards) {
            suitFreq[i.suit] = (suitFreq[i.suit] ?: 0) + 1
            if (suitFreq[i.suit] == 5)
                probability = 1.0
        }

        if (probability != 1.0) {
            for (value in suitFreq.values) {
                when (value) {
                    3 -> probability += (13 - value) / (numCards.toDouble() - visibleCards.size.toDouble()) * (13 - value - 1) / (numCards.toDouble() - visibleCards.size.toDouble() - 1)
                    4 -> probability += (13 - value) / (numCards.toDouble() - visibleCards.size.toDouble())
                }
            }
        }
        return probability
    }

    /**
     * Probability of a straight
     */
    private fun straightProb() : Double {
        return 0.0
    }

    /**
     * Probability of 3 kind
     */
    private fun threeKindProb() : Double {
        var probability = 0.0
        val pairs = HashSet<Int>()
        val notPairs = HashSet<Int>()
        for (i in 1 until visibleCards.size) {  //determine existing pairs
            notPairs.add(visibleCards[i - 1].number)
            if (visibleCards[i - 1].number == visibleCards[i].number) {
                notPairs.remove(visibleCards[i - 1].number)
                if (pairs.contains(visibleCards[i].number)) //three of a kind found
                    probability = 1.0
                else pairs.add(visibleCards[i].number)
            }
        }
        if (probability != 1.0) {
            if (visibleCards.size == 5) {
                probability += ((2 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble())) +
                        ((2 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                if (notPairs.size > 0) {
                    probability += ((3 * notPairs.size) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                        (2 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                }
            } else if (pairs.size > 0) {    //must have at least 1 pair if you are on the turn already
                probability += ((2 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble()))
            }
        }
        return probability
    }

    /**
     * Probability of drawing two pairs
     */
    private fun twoPairProb() : Double {
        val pairs = HashSet<Int>()
        val notPairs = HashSet<Int>()
        for (i in 1 until visibleCards.size) {  //check how many pairs exists
            notPairs.add(visibleCards[i - 1].number)
            if (visibleCards[i - 1].number == visibleCards[i].number) {
                pairs.add(visibleCards[i].number)
                notPairs.remove(visibleCards[i].number)
            }
        }
        if (!pairs.contains(visibleCards[visibleCards.size - 1].number))
            notPairs.add(visibleCards[visibleCards.size - 1].number)

        return if (pairs.size == 0 && visibleCards.size == 5) {  //if no pair exists after flop, impossible
            ((3.0 * visibleCards.size) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                    ((3.0 * (visibleCards.size - 1)) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
        } else if (pairs.size == 1) {   // can't assume just two cards = pair, maybe all 4 are out
            if (visibleCards.size == 5) {
                ((3.0 * notPairs.size) / (numCards.toDouble() - visibleCards.size.toDouble())) +
                        ((3.0 * notPairs.size) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
            } else {    //assume 6 for now
                ((3.0 * notPairs.size) / (numCards.toDouble() - visibleCards.size.toDouble()))
            }
        } else if (pairs.size == 2) {
            1.0
        } else {
            0.0
        }
    }

    /**
     * Probability of drawing a pair
     */
    private fun onePairProb() : Double {
        var probability = 0.0
        for (i in 1 until visibleCards.size)    //check if pair exists
            if (visibleCards[i - 1].number == visibleCards[i].number)
                probability = 1.0

        if (probability != 1.0)
            probability += (3.0 / (numCards.toDouble() - visibleCards.size.toDouble())) * visibleCards.size.toDouble()

        return probability
    }

    /**
     * Probability drawing higher
     */
    private fun highCardProb() : Double {
        // unhelpful = unknown - helpful -> helpful / unknown?
        return if (visibleCards[visibleCards.size - 1].number == 1)
            1.0
        else
            ((14 - visibleCards[visibleCards.size - 1].number) * 4).toDouble() / (numCards.toDouble() - visibleCards.size.toDouble())
    }

    private var cardsLeft = numCards
    private var players = SettingsActivity.defaultPlayers
    private lateinit var visibleCards: ArrayList<CardModel>
}
