package dev.jjablonski.lingommerse.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.texttospeech.v1.*
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.data.AppDatabase
import dev.jjablonski.lingommerse.model.LanguagePair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SlideshowActivity : AppCompatActivity() {

    private lateinit var originalTextView: TextView
    private lateinit var translationTextView: TextView
    private lateinit var progressTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var goBackButton: Button

    private var currentIndex = 0
    private val languagePairs = mutableListOf<LanguagePair>()
    private val handler = Handler(Looper.getMainLooper())
    private var playCount = 0

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow)

        db = AppDatabase.getDatabase(this)

        originalTextView = findViewById(R.id.originalTextView)
        translationTextView = findViewById(R.id.translationTextView)
        progressTextView = findViewById(R.id.progressTextView)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)
        goBackButton = findViewById(R.id.goBackButton)

        nextButton.setOnClickListener { showNextSlide() }
        previousButton.setOnClickListener { showPreviousSlide() }
        goBackButton.setOnClickListener { finish() }

        loadLanguagePairs()
    }

    private fun loadLanguagePairs() {
        CoroutineScope(Dispatchers.IO).launch {
            val pairs = db.languagePairDao().getAll()
            languagePairs.clear()
            languagePairs.addAll(pairs)
            if (languagePairs.isNotEmpty()) {
                runOnUiThread {
                    displayCurrentSlide()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@SlideshowActivity, "No data available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayCurrentSlide() {
        if (languagePairs.isEmpty()) return

        val currentPair = languagePairs[currentIndex]
        originalTextView.text = currentPair.original
        translationTextView.text = currentPair.translation
        progressTextView.text = "${currentIndex + 1} / ${languagePairs.size}"

        playCount = 0
        speakOut(currentPair)
    }

    private fun showNextSlide() {
        if (currentIndex < languagePairs.size - 1) {
            currentIndex++
            displayCurrentSlide()
        }
    }

    private fun showPreviousSlide() {
        if (currentIndex > 0) {
            currentIndex--
            displayCurrentSlide()
        }
    }

    private fun speakOut(pair: LanguagePair) {
        handler.post {
            playText(pair.original, "en-US")
        }
    }

    private fun playText(text: String, languageCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val credentials = GoogleCredentials.fromStream(resources.openRawResource(R.raw.api_key))
                val textToSpeechSettings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider { credentials }
                    .build()

                TextToSpeechClient.create(textToSpeechSettings).use { textToSpeechClient ->
                    val input = SynthesisInput.newBuilder().setText(text).build()
                    val voice = VoiceSelectionParams.newBuilder()
                        .setLanguageCode(languageCode)
                        .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                        .build()
                    val audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build()

                    val response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)
                    val audioData = response.audioContent.toByteArray()

                    val tempFile = File.createTempFile("tts_audio", ".wav", cacheDir)
                    FileOutputStream(tempFile).use { it.write(audioData) }

                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                    mediaPlayer.prepare()
                    mediaPlayer.setOnCompletionListener {
                        handler.postDelayed({
                            if (languageCode == "en-US") {
                                playText(languagePairs[currentIndex].translation, "pl-PL")
                            } else {
                                playCount++
                                if (playCount < 2) {
                                    speakOut(languagePairs[currentIndex])
                                } else {
                                    if (currentIndex < languagePairs.size - 1) {
                                        currentIndex++
                                        displayCurrentSlide()
                                    }
                                }
                            }
                        }, 1000)
                    }
                    mediaPlayer.start()

                    tempFile.deleteOnExit()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        super.onDestroy()
    }
}
