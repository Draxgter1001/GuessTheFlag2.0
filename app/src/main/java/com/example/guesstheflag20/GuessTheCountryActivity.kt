package com.example.guesstheflag20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class GuessTheCountryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessTheCountryActivityContent()
        }
    }

    @Composable
    fun GuessTheCountryActivityContent() {
        // Initial setup for variables and loading the countries list.
        // This section sets up the UI and manages the state for the current country, the user's guess, and the guess result.
        val countriesJson = additionalFunctions.loadCountriesJson(this)
        val countriesList = countriesJson.keys().asSequence().map { key ->
            key to countriesJson.getString(key)
        }.toList()

        var currentCountryCode by remember { mutableStateOf(additionalFunctions.pickRandomCountryCode(countriesJson)) }
        var userGuess by remember { mutableStateOf("") }
        var guessResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
        var showList by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            additionalFunctions.FlagImage(countryCode = currentCountryCode)

            // The TextField is used for displaying the user's current guess.
            // It is read-only and shows a dropdown icon for selecting countries.
            TextField(
                value = userGuess,
                onValueChange = { userGuess = it },
                label = { Text("Select Country") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "dropdown", Modifier.clickable { showList = !showList })
                }
            )

            // Displays a list of countries when the dropdown icon is clicked.
            // This allows the user to select a country as their guess.
            if (showList) {
                LazyColumn(modifier = Modifier.fillMaxHeight(0.5f)) {
                    items(countriesList) { country ->
                        TextButton(onClick = {
                            userGuess = country.second
                            showList = false
                        }) {
                            Text(text = country.second)
                        }
                    }
                }
            }

            // A submit button to check the user's guess against the correct answer.
            // This button checks the user's guess and updates the guessResult variable accordingly.
            Button(
                onClick = {
                    // Check if the user has submitted their guess
                    if(guessResult == null){
                        // User hasn't guessed yet, so check the guess
                        val correctAnswer = countriesJson.getString(currentCountryCode)
                        guessResult = Pair(userGuess == correctAnswer, correctAnswer)
                    }else{
                        // User has guessed, so load the next country
                        currentCountryCode = additionalFunctions.pickRandomCountryCode(countriesJson)
                        userGuess = "" // Reset user guess
                        guessResult = null // Reset for the next guess
                    }
                },
                enabled = userGuess.isNotEmpty(),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(if(guessResult == null)"Submit" else "Next")
            }

            // Displays the result of the user's guess and the correct answer.
            // This section provides feedback to the user after they make a guess, indicating whether they were correct or not.
            guessResult?.let {
                Text(
                    text = if (it.first) "CORRECT!" else "WRONG",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it.first) Color.Green else Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "The correct country was: ${it.second}",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(10.dp)
                )

            }
        }
    }
}