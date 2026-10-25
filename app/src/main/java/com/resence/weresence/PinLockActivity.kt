package com.resence.weresence

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Sovereign Dual-PIN Gateway:
 * - Enter `0000` -> Camouflage Decoy Mode (Launches DecoyAiActivity / ai.resence.in).
 * - Enter `1234` -> Sovereign Master Mode (Launches MainActivity / WeResence Messenger).
 *
 * Screen protected with WindowManager.LayoutParams.FLAG_SECURE to prevent screenshotting PIN.
 */
class PinLockActivity : AppCompatActivity() {

    companion object {
        const val DECOY_PIN = "0000"
        const val MASTER_PIN = "1234"
    }

    private val enteredPin = StringBuilder()
    private lateinit var dots: List<View>
    private lateinit var tvErrorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        // Enforce anti-screenshot & screen capture protection
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_pin_lock)

        dots = listOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3),
            findViewById(R.id.dot4)
        )
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        setupKeypad()
    }

    private fun setupKeypad() {
        val numberButtons = listOf(
            Pair(R.id.btn0, "0"),
            Pair(R.id.btn1, "1"),
            Pair(R.id.btn2, "2"),
            Pair(R.id.btn3, "3"),
            Pair(R.id.btn4, "4"),
            Pair(R.id.btn5, "5"),
            Pair(R.id.btn6, "6"),
            Pair(R.id.btn7, "7"),
            Pair(R.id.btn8, "8"),
            Pair(R.id.btn9, "9")
        )

        for ((btnId, digit) in numberButtons) {
            findViewById<TextView>(btnId).setOnClickListener {
                onDigitClicked(digit)
            }
        }

        findViewById<TextView>(R.id.btnDelete).setOnClickListener {
            onDeleteClicked()
        }
    }

    private fun onDigitClicked(digit: String) {
        if (enteredPin.length < 4) {
            enteredPin.append(digit)
            updateDots()

            if (enteredPin.length == 4) {
                verifyPin(enteredPin.toString())
            }
        }
    }

    private fun onDeleteClicked() {
        if (enteredPin.isNotEmpty()) {
            enteredPin.deleteCharAt(enteredPin.length - 1)
            tvErrorMessage.text = ""
            updateDots()
        }
    }

    private fun updateDots() {
        for (i in dots.indices) {
            if (i < enteredPin.length) {
                dots[i].setBackgroundResource(R.drawable.bg_pin_dot_filled)
            } else {
                dots[i].setBackgroundResource(R.drawable.bg_pin_dot_empty)
            }
        }
    }

    private fun verifyPin(pin: String) {
        when (pin) {
            DECOY_PIN -> {
                // 0000: Launch Camouflage Decoy AI (ai.resence.in)
                val intent = Intent(this, DecoyAiActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            MASTER_PIN -> {
                // 1234: Unlock Sovereign WeResence
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            else -> {
                // Invalid PIN
                triggerVibration()
                tvErrorMessage.text = "Incorrect Sovereign PIN"
                dots[0].postDelayed({
                    enteredPin.clear()
                    updateDots()
                }, 350)
            }
        }
    }

    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {
            // Vibration permission may not be granted
        }
    }
}
