package dev.jjablonski.lingommerse.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.data.AppDatabase
import dev.jjablonski.lingommerse.model.LanguagePair
import dev.jjablonski.lingommerse.adapter.LanguagePairAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val languagePairs = mutableListOf<LanguagePair>()
    private lateinit var adapter: LanguagePairAdapter
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var originalEditText: EditText
    private lateinit var translationEditText: EditText
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerView)
        originalEditText = findViewById(R.id.originalEditText)
        translationEditText = findViewById(R.id.translationEditText)
        addButton = findViewById(R.id.addButton)

        adapter = LanguagePairAdapter(languagePairs) { pair ->
            deleteLanguagePair(pair)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadLanguagePairs()

        addButton.setOnClickListener {
            val originalText = originalEditText.text.toString()
            val translationText = translationEditText.text.toString()
            if (originalText.isNotEmpty() && translationText.isNotEmpty()) {
                val pair = LanguagePair(original = originalText, translation = translationText)
                CoroutineScope(Dispatchers.IO).launch {
                    db.languagePairDao().insert(pair)
                    loadLanguagePairs()
                }
                originalEditText.text.clear()
                translationEditText.text.clear()
            }
        }

        val slideshowButton: Button = findViewById(R.id.slideshowButton)
        slideshowButton.setOnClickListener {
            val intent = Intent(this, SlideshowActivity::class.java)
            intent.putParcelableArrayListExtra("languagePairs", ArrayList(languagePairs))
            startActivity(intent)
        }
    }

    private fun loadLanguagePairs() {
        CoroutineScope(Dispatchers.IO).launch {
            val pairs = db.languagePairDao().getAll()
            languagePairs.clear()
            languagePairs.addAll(pairs)
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteLanguagePair(pair: LanguagePair) {
        CoroutineScope(Dispatchers.IO).launch {
            db.languagePairDao().delete(pair)
            loadLanguagePairs()
        }
    }
}
