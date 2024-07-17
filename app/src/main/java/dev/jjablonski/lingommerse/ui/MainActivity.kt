package dev.jjablonski.lingommerse.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jjablonski.lingommerse.R
import dev.jjablonski.lingommerse.data.AppDatabase
import dev.jjablonski.lingommerse.model.LanguageList
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
    private lateinit var listSpinner: Spinner
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var manageListsButton: Button

    private val lists = mutableListOf<LanguageList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerView)
        originalEditText = findViewById(R.id.originalEditText)
        translationEditText = findViewById(R.id.translationEditText)
        addButton = findViewById(R.id.addButton)
        listSpinner = findViewById(R.id.listSpinner)
        manageListsButton = findViewById(R.id.manageListsButton)

        listAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listSpinner.adapter = listAdapter

        adapter = LanguagePairAdapter(languagePairs) { pair ->
            deleteLanguagePair(pair)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadLanguageLists()

        addButton.setOnClickListener {
            val originalText = originalEditText.text.toString()
            val translationText = translationEditText.text.toString()
            val selectedListPosition = listSpinner.selectedItemPosition
            if (originalText.isNotEmpty() && translationText.isNotEmpty() && selectedListPosition >= 0) {
                val listId = lists[selectedListPosition].id
                val pair = LanguagePair(original = originalText, translation = translationText, listId = listId)
                CoroutineScope(Dispatchers.IO).launch {
                    db.languagePairDao().insert(pair)
                    loadLanguagePairs(listId)
                }
                originalEditText.text.clear()
                translationEditText.text.clear()
            }
        }
        val slideshowButton: Button = findViewById(R.id.slideshowButton)

        slideshowButton.setOnClickListener {
            val selectedListPosition = listSpinner.selectedItemPosition
            if (selectedListPosition >= 0) {
                val listId = lists[selectedListPosition].id
                val intent = Intent(this, SlideshowActivity::class.java)
                intent.putExtra("listId", listId)
                startActivity(intent)
            }
        }

        manageListsButton.setOnClickListener {
            val intent = Intent(this, LanguageListsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadLanguageLists() {
        CoroutineScope(Dispatchers.IO).launch {
            val loadedLists = db.languageListDao().getAll()
            if (loadedLists.isEmpty()) {
                // No lists available, redirect to LanguageListsActivity
                runOnUiThread {
                    startActivity(Intent(this@MainActivity, LanguageListsActivity::class.java))
                    finish()
                }
            } else {
                lists.clear()
                lists.addAll(loadedLists)
                runOnUiThread {
                    listAdapter.clear()
                    listAdapter.addAll(loadedLists.map { it.name })
                    listAdapter.notifyDataSetChanged()
                    loadLanguagePairs(lists[0].id)
                }
            }
        }
    }

    private fun loadLanguagePairs(listId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val pairs = db.languagePairDao().getAllFromList(listId)
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
            loadLanguagePairs(pair.listId)
        }
    }
}
