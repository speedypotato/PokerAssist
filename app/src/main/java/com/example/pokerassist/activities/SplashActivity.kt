package com.example.pokerassist.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum

import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    companion object {  //splash screen timeout in 'static'
        const val SPLASH_SCREEN_TIME_OUT: Long = 2000
        val POKER_NUMBER: List<Int> = List(13) { it + 1}
    }

    /**
     * Android framework onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        cards = ArrayList()     //initialize card objects
        for (suit in SuitEnum.values())
            initCards(suit)

        Handler().postDelayed({startActivity(Intent(this, MainActivity::class.java).apply {
            for (cardList in cards) {
                if (cardList.size > 0)
                    putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
            }})
            finish()}, SPLASH_SCREEN_TIME_OUT)
    }

    /**
     * Initializes 13 cards (A - K)
     * @param suit The suit of the card to initialize
     */
    private fun initCards(suit: SuitEnum = SuitEnum.SPADE) {
        cards.add(ArrayList<CardModel>().apply {
            for (i in POKER_NUMBER)
                add(CardModel(i, suit, false))
        })
    }

    private lateinit var cards: ArrayList<ArrayList<CardModel>>
}
