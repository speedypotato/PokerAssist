package com.example.pokerassist

import com.example.pokerassist.activities.*

enum class ActivityEnum(val activityClass: Class<*>) {
    SPLASH(SplashActivity::class.java),
    SELECT(SelectActivity::class.java),
    PREFLOP(PreflopActivity::class.java)
}