package com.example.pokerassist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_select.*

class SelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        cards = ArrayList()
        initCardModels()

        selected = ArrayList()

        cardViewMap = HashMap()
        initCardViews()
    }

    /**
     * Imports cards from intent and adds them to the instance var
     */
    private fun initCardModels() {
        for (suit in SuitEnum.values()) {
            val cardList = intent.getParcelableArrayListExtra<CardModel>(suit.suit)
            if (cardList != null)
                cards.add(cardList)
        }
    }

    /**
     * Creates ImageButtons and maps back to cards
     */
    private fun initCardViews() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        for (suitList in cards) {
            cardLinearLayout.addView(LinearLayout(this).apply{
                orientation = LinearLayout.VERTICAL
                for (card in suitList) {
                    ImageButton(this.context).apply{
                        setBackgroundResource(android.R.drawable.btn_default)
                        setImageResource(resources.getIdentifier(resources.getString(R.string.card_prefix) + card.suit.suit + card.number.toString(), "drawable", packageName))
                        layoutParams = ViewGroup.LayoutParams(width / cards.size, (1.4 * width / cards.size).toInt())
                        scaleType = ImageView.ScaleType.FIT_END
                        setOnClickListener{tryToggle(it)}
                    }.let {
                        cardViewMap[it] = card
                        addView(it)
                    }
                }
            })
        }
    }

    /**
     * Limits card selection to 2
     */
    private fun tryToggle(v: View) {
        if (selected.size < 2) {
            if (selected.contains(v))
                selected.remove(v)
            else
                selected.add(v)
            toggleCard(v)
        } else if (selected.contains(v)) {
            selected.remove(v)
            toggleCard(v)
        }
    }

    /**
     * Toggle the state of the selected card
     * Recommended to be called by tryToggle
     */
    private fun toggleCard(v: View) {
        val card = cardViewMap[v]
        if (card != null) {
            if (card.selected)
                v.setBackgroundResource(android.R.drawable.btn_default)
            else
                v.setBackgroundColor(ContextCompat.getColor(v.context, R.color.colorAccent))
            card.selected = !card.selected
        }
    }

    private lateinit var cards: MutableList<ArrayList<CardModel>>
    private lateinit var cardViewMap: MutableMap<View, CardModel>
    private lateinit var selected: MutableList<View>
}
