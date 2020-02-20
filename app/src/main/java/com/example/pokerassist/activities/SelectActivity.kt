package com.example.pokerassist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_select.*

class SelectActivity : AppCompatActivity() {
    companion object {
        const val defaultSelectable: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        initNumSelectable()

        initNextActivity()

        cards = ArrayList()
        initCardModels()

        selected = ArrayList()

        initTitleView()

        cardViewMap = HashMap()
        initCardViews()

        submitButton.setOnClickListener {
            if (selected.size == numSelectable) {
                submit()
            } else {
                AlertDialog.Builder(it.context)
                    .setTitle(resources.getString(R.string.error))
                    .setMessage(resources.getString(R.string.error_selection_1) + " " + numSelectable + " " + resources.getString(R.string.error_selection_2))
                    .setPositiveButton(resources.getString(R.string.ok), null)
                    .show()
            }
        }
    }

    /**
     * Imports number of selectable card from intent and adds them to instance var
     */
    private fun initNumSelectable() {
        numSelectable = intent.getIntExtra(resources.getString(R.string.num_sel_tag), defaultSelectable)
    }

    /**
     * Imports whats activity to switch to next
     */
    private fun initNextActivity() {
        val testActivity = intent.getSerializableExtra(resources.getString(R.string.next_act_tag))
        nextActivity = if (testActivity is ActivityEnum) testActivity else ActivityEnum.SPLASH
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
     * Updates Title from intent
     */
    private fun initTitleView() {
        selectTextView.text = intent.getStringExtra(resources.getString(R.string.title_tag)) ?: resources.getString(R.string.select_drawn_cards)
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
                        backgroundTintList = ContextCompat.getColorStateList(this.context, android.R.color.darker_gray)
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
     * Limits card selection to numSelectable
     */
    private fun tryToggle(v: View) {
        if (selected.size < numSelectable) {
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
                v.backgroundTintList = ContextCompat.getColorStateList(v.context, android.R.color.darker_gray)
            else
                v.backgroundTintList = ContextCompat.getColorStateList(v.context, R.color.colorAccent)
            card.selected = !card.selected
        }
    }

    /**
     * Change activity upon submit
     */
    private fun submit() {
        startActivity(Intent(this, nextActivity.activityClass).apply {
            putParcelableArrayListExtra(resources.getString(R.string.selected_cards_tag), ArrayList<CardModel>().apply {
                for (v in selected) {
                    var selectedCard = cardViewMap[v]
                    if (selectedCard != null)
                        add(selectedCard)
                }
            })
            when(nextActivity) {
                ActivityEnum.RIVER -> putParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag), intent.getParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag)))
                else -> { }
            }
        })
        finish()
    }

    private lateinit var cards: MutableList<ArrayList<CardModel>>
    private lateinit var cardViewMap: MutableMap<View, CardModel>
    private lateinit var selected: MutableList<View>
    private var numSelectable = defaultSelectable
    private lateinit var nextActivity: ActivityEnum
}
