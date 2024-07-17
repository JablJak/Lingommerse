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
import dev.jjablonski.lingommerse.adapter.LanguageListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LanguageListsActivity : AppCompatActivity() {

    private val languageLists = mutableListOf<LanguageList>()
    private lateinit var adapter: LanguageListAdapter
    private lateinit var db: AppDatabase
    private lateinit var listsRecyclerView: RecyclerView
    private lateinit var listNameEditText: EditText
    private lateinit var addListButton: Button
    private lateinit var originalLanguageSpinner: Spinner
    private lateinit var translationLanguageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_lists)

        db = AppDatabase.getDatabase(this)

        listsRecyclerView = findViewById(R.id.listsRecyclerView)
        listNameEditText = findViewById(R.id.listNameEditText)
        addListButton = findViewById(R.id.addListButton)
        originalLanguageSpinner = findViewById(R.id.originalLanguageSpinner)
        translationLanguageSpinner = findViewById(R.id.translationLanguageSpinner)

        val languages = listOf("en-US", "pl-PL", "es-ES", "fr-FR") // Dodaj tutaj więcej języków, jeśli potrzebujesz
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        originalLanguageSpinner.adapter = languageAdapter
        translationLanguageSpinner.adapter = languageAdapter

        adapter = LanguageListAdapter(languageLists, { list ->
            openLanguagePairsActivity(list)
        }, { list ->
            deleteLanguageList(list)
        })
        listsRecyclerView.layoutManager = LinearLayoutManager(this)
        listsRecyclerView.adapter = adapter

        loadLanguageLists()

        addListButton.setOnClickListener {
            val listName = listNameEditText.text.toString()
            val originalLanguage = originalLanguageSpinner.selectedItem.toString()
            val translationLanguage = translationLanguageSpinner.selectedItem.toString()
            if (listName.isNotEmpty() && originalLanguage.isNotEmpty() && translationLanguage.isNotEmpty()) {
                val list = LanguageList(name = listName, originalLanguage = originalLanguage, translationLanguage = translationLanguage)
                CoroutineScope(Dispatchers.IO).launch {
                    db.languageListDao().insert(list)
                    loadLanguageLists()
                }
                listNameEditText.text.clear()
            }
        }
    }

    private fun loadLanguageLists() {
        CoroutineScope(Dispatchers.IO).launch {
            val lists = db.languageListDao().getAll()
            languageLists.clear()
            languageLists.addAll(lists)
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun openLanguagePairsActivity(list: LanguageList) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("listId", list.id)
        startActivity(intent)
    }

    private fun deleteLanguageList(list: LanguageList) {
        CoroutineScope(Dispatchers.IO).launch {
            db.languageListDao().delete(list)
            loadLanguageLists()
        }
    }
}
