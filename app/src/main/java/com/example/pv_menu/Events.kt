package com.example.pv_menu

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Events : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var eventsAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        listView = findViewById(R.id.listViewEvenimente)

        // JSON fals pentru evenimente
        val jsonFakeEvents = """
            [
                {"title": "Eveniment 1", "date": "2024-05-20"},
                {"title": "Eveniment 2", "date": "2024-06-15"},
                {"title": "Eveniment 3", "date": "2024-07-10"}
            ]
        """

        // Parsează JSON-ul
        val events: List<Event> = Gson().fromJson(jsonFakeEvents, object : TypeToken<List<Event>>() {}.type)
        val eventsDisplayList = events.map { "${it.title} - ${it.date}" }

        // Creează un ArrayAdapter
        eventsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventsDisplayList)

        // Setează adapter-ul pe ListView
        listView.adapter = eventsAdapter
    }

    data class Event(val title: String, val date: String)
}
