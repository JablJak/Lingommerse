package dev.jjablonski.lingommerse.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.texttospeech.v1.*
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguagePair
import java.io.FileOutputStream
import java.io.InputStream

class SlideshowActivity : AppCompatActivity() {

    private lateinit var originalTextView: TextView
    private lateinit var translationTextView: TextView
    private lateinit var progressTextView: TextView
    private lateinit var goBackButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private lateinit var languagePairs: List<LanguagePair>
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    private val client: TextToSpeechClient by lazy {
        val credentialsStream: InputStream = resources.openRawResource(R.raw.api_key)
        val credentials = GoogleCredentials.fromStream(credentialsStream)
        val settings = TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()
        TextToSpeechClient.create(settings)
    }
    private val voices = mapOf(
        "pl-PL" to "pl-PL-Wavenet-A",
        "en-US" to "en-US-Wavenet-D",
        "de-DE" to "de-DE-Wavenet-D",
        "it-IT" to "it-IT-Wavenet-C"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow)

        originalTextView = findViewById(R.id.originalTextView)
        translationTextView = findViewById(R.id.translationTextView)
        progressTextView = findViewById(R.id.progressTextView)
        goBackButton = findViewById(R.id.goBackButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        languagePairs = intent.getParcelableArrayListExtra("languagePairs") ?: listOf()
        mediaPlayer = MediaPlayer()

        goBackButton.setOnClickListener { finish() }
        previousButton.setOnClickListener { changeSlide(-1) }
        nextButton.setOnClickListener { changeSlide(1) }

        startSlideshow()
    }

    private fun startSlideshow() {
        if (languagePairs.isNotEmpty()) {
            showCurrentSlide()
            handler.post(slideRunnable)
        } else {
            finish()
        }
    }

    private val slideRunnable = object : Runnable {
        override fun run() {
            if (currentIndex < languagePairs.size) {
                isPlaying = true
                val pair = languagePairs[currentIndex]
                playText(pair.original, "en-US") {
                    handler.postDelayed({
                        playText(pair.translation, "pl-PL") {
                            handler.postDelayed({
                                playText(pair.original, "en-US") {
                                    handler.postDelayed({
                                        playText(pair.translation, "pl-PL") {
                                            isPlaying = false
                                            if (currentIndex < languagePairs.size - 1) {
                                                currentIndex++
                                                showCurrentSlide()
                                                handler.postDelayed(this, 2000)
                                            } else {
                                                finish()
                                            }
                                        }
                                    }, 1000)
                                }
                            }, 1000)
                        }
                    }, 1000)
                }
            }
        }
    }

    private fun showCurrentSlide() {
        val pair = languagePairs[currentIndex]
        originalTextView.text = pair.original
        translationTextView.text = pair.translation
        progressTextView.text = "Slide ${currentIndex + 1} of ${languagePairs.size}"
    }

    private fun changeSlide(direction: Int) {
        if (isPlaying) {
            mediaPlayer.stop()
            handler.removeCallbacks(slideRunnable)
            isPlaying = false
        }
        currentIndex = (currentIndex + direction).coerceIn(0, languagePairs.size - 1)
        showCurrentSlide()
        handler.post(slideRunnable)
    }

    private fun playText(text: String, languageCode: String, onComplete: () -> Unit) {
        val voiceName = voices[languageCode]
        if (voiceName == null) {
            onComplete()
            return
        }
        val input = SynthesisInput.newBuilder().setText(text).build()
        val voice = VoiceSelectionParams.newBuilder()
            .setLanguageCode(languageCode)
            .setName(voiceName)
            .setSsmlGender(SsmlVoiceGender.MALE)
            .build()
        val audioConfig = AudioConfig.newBuilder()
            .setAudioEncoding(AudioEncoding.MP3)
            .build()
        val response = client.synthesizeSpeech(input, voice, audioConfig)
        val audioContents = response.audioContent

        val outputFile = createTempFile("tts", ".mp3")
        FileOutputStream(outputFile).use { fos ->
            fos.write(audioContents.toByteArray())
        }

        mediaPlayer.reset()
        mediaPlayer.setDataSource(outputFile.absolutePath)
        mediaPlayer.prepare()
        mediaPlayer.setOnCompletionListener {
            onComplete()
        }
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
        mediaPlayer.release()
    }
}
