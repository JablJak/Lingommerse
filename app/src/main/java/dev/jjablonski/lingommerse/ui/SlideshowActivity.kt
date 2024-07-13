package dev.jjablonski.lingommerse.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguagePair

class SlideshowActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private lateinit var languagePairs: List<LanguagePair>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow)

        textView = findViewById(R.id.textView)
        languagePairs = intent.getParcelableArrayListExtra("languagePairs") ?: listOf()

        startSlideshow()
    }

    private fun startSlideshow() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex < languagePairs.size) {
                    val pair = languagePairs[currentIndex]
                    textView.text = "${pair.original} - ${pair.translation}"
                    currentIndex++
                    handler.postDelayed(this, 10000) // Change slide every 10 seconds
                }
            }
        }, 0)
    }
}
