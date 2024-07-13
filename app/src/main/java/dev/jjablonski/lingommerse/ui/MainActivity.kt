package dev.jjablonski.lingommerse.ui;

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.model.LanguagePair
import dev.jjablonski.lingommerse.ui.adapter.LanguagePairAdapter

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    private lateinit var originalEditText: EditText
    private lateinit var translationEditText: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private val languagePairs = mutableListOf<LanguagePair>()
    private val adapter = LanguagePairAdapter(languagePairs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        originalEditText = findViewById(R.id.originalEditText)
        translationEditText = findViewById(R.id.translationEditText)
        addButton = findViewById(R.id.addButton)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // MainActivity.kt
        // Inside onCreate method, after setting up the RecyclerView

        addButton.setOnClickListener {
            val original = originalEditText.text.toString()
            val translation = translationEditText.text.toString()
            if (original.isNotEmpty() && translation.isNotEmpty()) {
                languagePairs.add(LanguagePair(original, translation))
                adapter.notifyDataSetChanged()
                originalEditText.text.clear()
                translationEditText.text.clear()
            }
        }

        val slideshowButton: Button = findViewById(R.id.slideshowButton)
        slideshowButton.setOnClickListener {
            val intent = Intent(this, SlideshowActivity::class.java)
            intent.putParcelableArrayListExtra("languagePairs", ArrayList<Parcelable>(languagePairs))
            startActivity(intent)
        }
    }
}

