package com.example.pokerassist.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    companion object {  //splash screen timeout in 'static'
        const val SPLASH_SCREEN_TIME_OUT: Long = 2000
        const val POKER_NUMBER: Int = 13
    }

    /**
     * Android framework onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()

        cards = ArrayList()     //initialize card objects
        for (suit in SuitEnum.values())
            initCards(suit)

        handler.postDelayed({startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
            putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_drawn_cards))
            putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.PREFLOP)
            putExtra(resources.getString(R.string.num_sel_tag), 2)
            for (cardList in cards) {
                if (cardList.size > 0)
                    putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
            }})
            finish()}, SPLASH_SCREEN_TIME_OUT)

        splashCoordinatorLayout.setOnClickListener { settingsOnClick() }

        if (!hasSettings()) noSettingsRoutine()
    }

    /**
     * Initializes 13 cards (A - K)
     * @param suit The suit of the card to initialize
     */
    private fun initCards(suit: SuitEnum = SuitEnum.SPADE) {
        cards.add(ArrayList<CardModel>().apply {
            for (i in 1..POKER_NUMBER)
                add(CardModel(i, suit, false))
        })
    }

    /**
     * Move to settings screen
     */
    private fun settingsOnClick() {
        handler.removeCallbacksAndMessages(null)     //cancels startActivity for SELECT
        startActivity(Intent(this, ActivityEnum.SETTINGS.activityClass))
        finish()
    }

    /**
     * Checks if settings are stored in SharedPreferences
     */
    private fun hasSettings(): Boolean {
        val sharedPref = getSharedPreferences(getString(R.string.settings_file_key), Context.MODE_PRIVATE)
        return (sharedPref.getInt(resources.getString(R.string.players), -1) != -1)
    }

    private fun noSettingsRoutine() {
        handler.removeCallbacksAndMessages(null)     //cancels startActivity for SELECT
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.error))
            .setMessage(resources.getString(R.string.error_settings))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ -> settingsOnClick() }
            .show()
    }

    private lateinit var cards: ArrayList<ArrayList<CardModel>>
    private lateinit var handler: Handler
}
