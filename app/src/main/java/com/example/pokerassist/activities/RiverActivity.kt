package com.example.pokerassist.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_river.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

//TODO: Fix straight probability inaccuracies
//      implement card chance of being unavailable in every prob() instead of in view()
class RiverActivity : AppCompatActivity() {
    companion object {
        const val numCards = 52
        const val numDealt = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_river)

        initPlayers()

        visibleCards = ArrayList()
        initCards()

        initView()

        foldButton2.setOnClickListener { foldSubmit() }
        proceedButton2.setOnClickListener { proceedSubmit() }
        calcButtonRiver.setOnClickListener { calculatorSubmit() }

        updateProbView(royalFlushTextView, royalFlushProb())
        updateProbView(straightFlushTextView, straightFlushProb())
        updateProbView(fourKindTextView, fourKindProb())
        updateProbView(fullHouseTextView, fullHouseProb())
        updateProbView(flushTextView, flushProb())
        updateProbView(straightTextView, straightProb())
        updateProbView(threeKindTextview, threeKindProb())
        updateProbView(twoPairTextView, twoPairProb())
        updateProbView(onePairTextView, onePairProb())
        updateProbView(higherCardTextView, highCardProb())
    }

    /**
     * Confirm back button exit
     * TODO: Ideally would like some sort of back button functionality
     */
    override fun onBackPressed() {
        android.app.AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.closing_app))
            .setMessage(resources.getString(R.string.closing_message))
            .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { _, _ -> finish(); })
            .setNegativeButton(resources.getString(R.string.no), null)
            .show()
    }

    /**
     * Changes title based on number of cards visible
     */
    private fun initView() {
        when(visibleCards.size) {
            5 -> riverTextView2.text = resources.getString(R.string.flop_probabilities)
            6 -> riverTextView2.text = resources.getString(R.string.turn_probabilities)
            7 -> {
                riverTextView2.text = resources.getString(R.string.river_probabilities)
                proceedButton2.text = resources.getString(R.string.reset)
            }
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
            else -> foldSubmit()
        }
    }

    /**
     * Given a TextView and Double, update the view accordingly
     */
    private fun updateProbView(v: TextView, prob: Double) {
        //TODO: Do this accurately outside of this method(see class comments)
        //regular probability * ((non visible cards - (players * 2 cards - 2 of your cards - burn)) / non visible cards)  <- consider multiplying by the cards left in deck / non visible cards for a more realistic estimate
        val chance: Double = when (visibleCards.size) {
            5 -> {
                (((numCards - visibleCards.size) - (players * 2.0 - 2.0 - 1.0)) / (numCards - visibleCards.size) +     //average burn chance for more accurate estimate
                    ((numCards - visibleCards.size - 1) - (players * 2.0 - 2.0 - 2.0)) / (numCards - visibleCards.size - 1)) / 2.0
            }
            6 -> {
                ((numCards - visibleCards.size) - (players * 2.0 - 2.0 - 1.0)) / (numCards - visibleCards.size)
            }
            else -> 1.0
        }

        var probability = prob
        if (visibleCards.size == 7 && probability != 1.0)   //if @ game end, didn't get, show 0(impossible)
            probability = 0.0
        else if (probability > 1.0) //maximum 100%
            probability = 1.0

        v.text = (probability * chance * 100).toString()    //fudge draw chance
        if (probability == 1.0) {
            v.text = (probability * 100).toString()
            v.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        } else if (probability == 0.0)
            v.setTextColor(ContextCompat.getColor(this, R.color.colorAccentRed))
    }

    /**
     * Probability of a Royal Flush
     */
    private fun royalFlushProb() : Double {
        val possRF = HashMap<SuitEnum, HashSet<CardModel>>()
        for (suit in SuitEnum.values()) {
            possRF[suit] = HashSet<CardModel>().apply {
                for (i in 10..14) {
                    if (i == 14)
                        add(CardModel(1, suit, false))
                    else
                        add(CardModel(i, suit, false))
                }
            }
        }
        for ((suit, cards) in possRF) { //get cards required to complete RF for each suit
            possRF[suit] = cards.filterTo(HashSet()) { cm -> !visibleCards.contains(cm) }
        }

        var prob = 0.0
        for (cards in possRF.values) { //begin probability calculation
            when (visibleCards.size) {
                5 -> {  //can draw 2 cards
                    when (cards.size) {
                        2 -> {  //need 2 left to complete
                            prob += (2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))}
                        1 -> {
                            prob += (1.0 / (numCards.toDouble() - visibleCards.size.toDouble())) +
                                (.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))}
                        0 -> { return 1.0 } //RF found
                    }
                }
                6 -> {  //can draw 1 card
                    when (cards.size) {
                        1 -> {prob += (1.0 / (numCards.toDouble() - visibleCards.size.toDouble()))} //can only draw 1 card to complete at this point
                        0 -> { return 1.0 }
                    }
                }
                7 -> {
                    when (cards.size) {
                        0 -> { return 1.0 }
                    }
                }
            }
        }
        return prob
    }

    /**
     * Probability of a straight flush
     * NOTE: See straightProb() for probability issue
     */
    private fun straightFlushProb() : Double {
        val sorted = ArrayList<ArrayList<CardModel>>() //add aces to lowest idx
        val sortedWithLow = ArrayList<ArrayList<CardModel>>()
        for (i in 0..3) {
            sorted.add(ArrayList())
            sortedWithLow.add(ArrayList())
        }
        for (card in visibleCards) {
            val idx = when (card.suit) {
                SuitEnum.CLUB -> 0
                SuitEnum.HEART -> 1
                SuitEnum.DIAMOND -> 2
                SuitEnum.SPADE -> 3
            }
            if (card.number == 1) {   //add high As to the end first
                sorted[idx].add(CardModel(14, card.suit, false))
                sortedWithLow[idx].add(CardModel(1, card.suit, false))   //14 as high ace to determine straight
            } else {
                sorted[idx].add(card)
            }
        }
        for (i in 0..3) {
            sortedWithLow[i].addAll(sorted[i]) //1 -> 14
        }

        val possible = HashSet<List<CardModel>>()
        for (suits in sortedWithLow) {    //get possible straight flushes
            var workingList = ArrayList<CardModel>()
            for (card in suits) {
                workingList.add(card)
                if (workingList.size > 0 && card.number - workingList[0].number > 4) {  //added an invalid card
                    if (((workingList.size - 1) >= 3 && visibleCards.size == 5) ||
                        ((workingList.size - 1) >= 4 && visibleCards.size == 6)) {  //straight is possible
                        // add working list minus the most recently added invalid
                        possible.add(ArrayList<CardModel>().apply {
                            addAll(workingList)
                            removeAt(workingList.size - 1)
                        })  //working list contains a potential straight flush
                    }
                    while (workingList.size > 0 && card.number - workingList[0].number > 4) //update working
                        workingList.removeAt(0)
                }
            }
            if ((((workingList.size) >= 3 && visibleCards.size == 5) || ((workingList.size) >= 4 && visibleCards.size == 6))
                && workingList[workingList.size - 1].number - workingList[0].number < 5) { //last check fencepost
                possible.add(ArrayList<CardModel>().apply {
                    addAll(workingList)
                })  //working list contains a potential straight flush
            }
        }

        var prob = 0.0  //straight probability calculation
        for (cards in possible) {
            when (cards.size) {
                3 -> {
                    when (cards[cards.size - 1].number - cards[0].number) {
                        2 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low exists
                                ((2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            } else if (cards[0].number == 2 || cards[cards.size - 1].number == 13) {    //hit ace on first draw or go the other way
                                (((1.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))) +
                                        ((1.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (2.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))))
                            } else {
                                ((2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (2.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            }
                        }
                        3 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low
                                ((2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                    (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            } else {
                                ((3.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                    (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            }
                        }
                        4 -> {
                            prob += ((2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                    (1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                        }
                    }
                }
                4 -> {
                    when (cards[cards.size - 1].number - cards[0].number) {
                        3 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low exists
                                (1.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                            } else {
                                (2.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                            }
                        }
                        4 -> {
                            prob += (1.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                        }
                    }
                }
                5 -> prob = 1.0
            }
        }

        return prob
    }

    /**
     * Probability of four of a kind
     */
    private fun fourKindProb() : Double {
        var freqMap = HashMap<Int, Int>()
        for (i in visibleCards) {
            freqMap[i.number] = (freqMap[i.number] ?: 0) + 1
            if ((freqMap[i.number] ?: 0) >= 4)  //reached 4 of a kind
                return 1.0
        }

        var prob = 0.0
        for (value in freqMap.values) {
            if (value == 2 && visibleCards.size == 5) {   //pair exists
                prob += (2.0 / (numCards.toDouble() - visibleCards.size.toDouble())) * (1.0 /
                        (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
            } else if (value == 3) {    //triple exists
                when (visibleCards.size) {
                    5 -> prob += 1.0 / (numCards.toDouble() - visibleCards.size.toDouble()) +
                            1.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)
                    6 -> prob += 1.0 / (numCards.toDouble() - visibleCards.size.toDouble())
                }
            }
        }
        return prob
    }

    /**
     * Probability of a full house
     */
    private fun fullHouseProb() : Double {
        val noPair = HashSet<Int>()
        val pair = HashSet<Int>()
        val triple = HashSet<Int>()
        val quad = HashSet<Int>()
        for (card in visibleCards) {    //divide up into single, pair, 3 or 4 kind(triple), and keep track if there is 4-kind
            when {
                pair.contains(card.number) -> {
                    pair.remove(card.number)
                    triple.add(card.number)
                }
                noPair.contains(card.number) -> {
                    noPair.remove(card.number)
                    pair.add(card.number)
                }
                triple.contains(card.number) -> {
                    quad.add(card.number)
                }
                else -> noPair.add(card.number)//number has not been seen yet
            }
        }

        return when (triple.size) {
            0 -> {
                when (pair.size) {
                    1 -> {
                        when (visibleCards.size) {
                            5 -> {  //must draw pair, triple OR triple, pair
                                ((noPair.size * 3.0) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                                + ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        ((noPair.size * 3.0) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                            }
                            else -> 0.0 //impossible here
                        }
                    }
                    2 -> {
                        when (visibleCards.size) {
                            5 -> {  //draw at least 1 card from a pair
                                ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble())) +
                                        ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                            }
                            6 -> ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble()))
                            else -> 0.0
                        }
                    }
                    3 -> {
                        when (visibleCards.size) {  //theoretically 6-7 cards on table, so up to 1 draw
                            6 -> ((pair.size * 2.0) / (numCards.toDouble() - visibleCards.size.toDouble()))
                            else -> 0.0
                        }
                    }
                    else -> 0.0
                }
            }
            1 -> {
                when (pair.size) {
                    0 -> {  //no pairs, so fallback to noPair.  Also consider getting a pair on turn & river
                        when (visibleCards.size) {
                            5 -> {
                                ((noPair.size * 3.0) / (numCards.toDouble() - visibleCards.size.toDouble())) +
                                        ((noPair.size * 3.0) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)) +
                                        (((numCards.toDouble() - noPair.size - (pair.size * 2.0) - ((triple.size - quad.size) * 3.0) -
                                                (quad.size * 4.0)) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                                (3.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            }
                            6 -> {  //can only draw 1, so must match a card that is visible already
                                ((noPair.size * 3.0) / (numCards.toDouble() - visibleCards.size.toDouble()))
                            }
                            else -> 0.0
                        }
                    }
                    else -> 1.0 //full house exists
                }
            }
            else -> 1.0 // two sets of 3-kind
        }
    }

    /**
     * Probability of a flush
     */
    private fun flushProb() : Double {
        var probability = 0.0
        val suitFreq = HashMap<SuitEnum, Int>()
        for (i in visibleCards) {
            suitFreq[i.suit] = (suitFreq[i.suit] ?: 0) + 1
            if (suitFreq[i.suit] == 5)  //reached a flush
                probability = 1.0
        }

        if (probability != 1.0) {
            when (visibleCards.size) {
                5 -> {  //can draw 2 cards
                    for (value in suitFreq.values) {
                        when (value) {
                            3 -> probability += (13.0 - value) / (numCards.toDouble() - visibleCards.size.toDouble()) *
                                    (13.0 - value - 1.0) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)
                            4 -> probability += (13.0 - value) / (numCards.toDouble() - visibleCards.size.toDouble())
                        }
                    }
                }
                6 -> {  //can draw 1 more card
                    for (value in suitFreq.values) {
                        when (value) {
                            4 -> probability += (13.0 - value) / (numCards.toDouble() - visibleCards.size.toDouble())
                        }
                    }
                }
            }
        }
        return probability
    }

    /**
     * Probability of a straight
     * NOTE: somewhat inaccurate, e.g. A 3456 -> prob is (2) + (2 | 7) instead of (2 | 7)
     */
    private fun straightProb() : Double {
        val sorted = ArrayList<CardModel>() //add aces to lowest idx
        val sortedWithLow = ArrayList<CardModel>()
        for (card in visibleCards) {
            if (card.number == 1) {   //add high As to the end first
                sorted.add(CardModel(14, card.suit, false))
                sortedWithLow.add(CardModel(1, card.suit, false))   //14 as high ace to determine straight
            } else {
                sorted.add(card)
            }
        }
        sortedWithLow.addAll(sorted) //1 -> 14

        val possible = HashSet<List<CardModel>>()
        val workingList = ArrayList<CardModel>()
        for (card in sortedWithLow) {    //get possible straight flushes
            if (workingList.size > 0 && card.number == workingList[workingList.size - 1].number)    //skip duplicate numbers
                continue
            else
                workingList.add(card)

            if (workingList.size > 0 && card.number - workingList[0].number > 4) {  //added an invalid card
                if (((workingList.size - 1) >= 3 && visibleCards.size == 5) || ((workingList.size - 1) >= 4 && visibleCards.size == 6)) {  //straight is possible
                    possible.add(ArrayList<CardModel>().apply { //add working list minus the most recently added invalid
                        addAll(workingList)
                        removeAt(workingList.size - 1)
                    })
                }
                while (workingList.size > 0 && card.number - workingList[0].number > 4) //update working
                    workingList.removeAt(0)
            }
        }
        if ((((workingList.size) >= 3 && visibleCards.size == 5) || ((workingList.size) >= 4 && visibleCards.size == 6))
            && workingList[workingList.size - 1].number - workingList[0].number < 5) {  //if fencepost is valid
            possible.add(ArrayList<CardModel>().apply { //add working list minus the most recently added invalid
                addAll(workingList)
            })
        }

        var prob = 0.0  //straight probability calculation
        for (cards in possible) {
            when (cards.size) {
                3 -> {
                    when (cards[cards.size - 1].number - cards[0].number) {
                        2 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low exists
                                ((8.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (4.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            } else if (cards[0].number == 2 || cards[cards.size - 1].number == 13) {    //hit ace on first draw or go the other way
                                (((4.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (4.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))) +
                                        ((4.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                                (8.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))))
                            } else {
                                ((8.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (8.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            }
                        }
                        3 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low
                                ((8.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (4.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            } else {
                                ((12.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                        (4.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                            }
                        }
                        4 -> {
                            prob += ((8.0 / (numCards.toDouble() - visibleCards.size.toDouble())) *
                                    (4.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0)))
                        }
                    }
                }
                4 -> {
                    when (cards[cards.size - 1].number - cards[0].number) {
                        3 -> {
                            prob += if ((cards[0].number % 13) == 1 || (cards[cards.size - 1].number % 13) == 1) {    //ace high/low exists
                                (4.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                            } else {
                                (8.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                            }
                        }
                        4 -> {
                            prob += (4.0 / (numCards.toDouble() - visibleCards.size.toDouble()))
                        }
                    }
                }
                5 -> prob = 1.0
            }
        }

        return prob
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
                probability += ((2.0 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble())) +
                        ((2.0 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                if (notPairs.size > 0) {
                    probability += ((3.0 * notPairs.size) / (numCards.toDouble() - visibleCards.size.toDouble())) *
                        (2.0 / (numCards.toDouble() - visibleCards.size.toDouble() - 1.0))
                }
            } else if (pairs.size > 0) {    //must have at least 1 pair if you are on the turn already
                probability += ((2.0 * pairs.size) / (numCards.toDouble() - visibleCards.size.toDouble()))
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
        } else if (pairs.size >= 2) {
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
        return if (visibleCards[visibleCards.size - 1].number == 1)
            1.0
        else
            ((14 - visibleCards[visibleCards.size - 1].number) * 4).toDouble() / (numCards.toDouble() - visibleCards.size.toDouble())
    }

    /**
     * Opens calculator activity
     */
    private fun calculatorSubmit() {
        startActivity(Intent(this, ActivityEnum.CALCULATOR.activityClass))
    }

    private var cardsLeft = numCards
    private var players = SettingsActivity.defaultPlayers
    private lateinit var visibleCards: ArrayList<CardModel>
}
