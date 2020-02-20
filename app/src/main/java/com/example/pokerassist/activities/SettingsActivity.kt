package com.example.pokerassist.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val defaultPlayers = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fields = listOf(resources.getString(R.string.players))  //todo: make this scalable with RiverActivity
        fieldMap = HashMap()
        initLayout()

        settingsButton.setOnClickListener{ submit() }
    }

    /**
     * Generate settings
     */
    private fun initLayout() {
        sharedPref = getSharedPreferences(getString(R.string.settings_file_key), Context.MODE_PRIVATE)
        for (field in fields) {
            settingsLayout.addView(LinearLayout(this).apply {
                gravity = Gravity.CENTER
                val label = TextView(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply{
                        weight = 0.5f
                    }
                    text = field
                }
                val entry = EditText(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply{
                        weight = 1f
                    }
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(sharedPref.getInt(field, defaultPlayers).toString())
                }
                addView(label)
                addView(entry)
                fieldMap[label] = entry
            })
        }
    }

    /**
     * Submit button
     */
    private fun submit() {
        with (sharedPref.edit()) {
            fieldMap.forEach { (key, value) -> putInt(key.text.toString(), value.text.toString().toIntOrNull() ?: defaultPlayers)}
            apply()
        }
        startActivity(Intent(this, ActivityEnum.SPLASH.activityClass))
        finish()
    }

    private lateinit var sharedPref: SharedPreferences
    private lateinit var fields: List<String>
    private lateinit var fieldMap: HashMap<TextView, EditText>
}
