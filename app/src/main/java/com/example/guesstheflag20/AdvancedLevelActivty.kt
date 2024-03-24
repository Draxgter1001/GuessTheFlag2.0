package com.example.guesstheflag20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.guesstheflag20.ui.theme.GuessTheFlag20Theme
import kotlinx.coroutines.delay

class AdvancedLevelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessTheFlag20Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AdvancedLevelContent()
                }
            }
        }
    }

    @Composable
    fun AdvancedLevelContent() {

        val countriesJson = mainFunctions.loadCountriesJson(this)
        // Pick 3 random country codes and store them in a mutable state list
        val countryCodes = remember { mainFunctions.pickRandomCountryCodesList(countriesJson, 3).toMutableStateList() }
        var score by rememberSaveable { mutableStateOf(0) }
        var attempt by rememberSaveable { mutableStateOf(0) }
        var guesses = remember { mutableStateListOf("", "", "") }
        var correctness = remember { mutableStateListOf(false, false, false) }
        var scored = remember { mutableStateListOf(false, false, false) } // New state to track score updates
        var showButton by rememberSaveable { mutableStateOf(false) }
        var timerValue by rememberSaveable { mutableStateOf(10) }
        var resetTimer by rememberSaveable { mutableStateOf(false) }
        var stopTimer by rememberSaveable { mutableStateOf(false) }


        if(setTimer){
            LaunchedEffect(resetTimer) {
                timerValue = 10 // Reset the timer for each new country or attempt
                while (timerValue > 0 && !showButton) {
                    delay(1000) // Wait for 1 second
                    if(!stopTimer){
                        timerValue--
                    }
                }
                if (timerValue == 0 && !showButton) {
                    attempt = 3
                    showButton = true
                    correctness.indices.forEach { index ->
                        if (!correctness[index]) { // Process only if not already correct
                            val countryName = countriesJson.getString(countryCodes[index])
                            val correct = guesses[index].equals(countryName, ignoreCase = true)
                            correctness[index] = correct
                            if (correct && !scored[index]) {
                                score++ // Update the score for correct guesses
                                scored[index] = true
                            }
                            if (!correctness[index]) {
                                guesses[index] = countryName // Show correct answer in blue
                            }
                        }
                    }
                }
            }
        }

        // UI layout and logic for the game
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display the timer if setTimer is true
                if(setTimer){
                    Text(text = "Time left: $timerValue", style = MaterialTheme.typography.titleLarge)
                }
                // Display the current score
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(text = "Score: $score", style = MaterialTheme.typography.titleLarge)
                }

                // Display the flags and text fields for each country code
                countryCodes.forEachIndexed { index, countryCode ->
                    mainFunctions.AdvancedLevelFlagImage(countryCode = countryCode)
                    TextField(
                        value = guesses[index],
                        onValueChange = { value ->
                            if (!correctness[index] && attempt < 3) {
                                guesses[index] = value
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = when {
                                correctness[index] -> Color.Green
                                attempt >= 3 && !correctness[index] -> Color.Blue // Show correct answers in blue after all attempts
                                attempt > 0 && guesses[index].isNotBlank() && !correctness[index] -> Color.Red // Incorrect guesses in red, after first attempt
                                else -> Color.Black
                            }
                        ),
                        readOnly = correctness[index] || attempt >= 3 // Make TextField readOnly if guess is correct or after all attempts
                    )
                }

                // Display attempts left
                if(attempt > 0){
                    val attemptsLeft = 3 - attempt
                    Text(
                        "Attempts left: $attemptsLeft",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Submit/Next button
                Button(onClick = {
                    if(!showButton || attempt < 3){
                        correctness.indices.forEach { index ->
                            val countryName = countriesJson.getString(countryCodes[index])
                            val correct = guesses[index].equals(countryName, ignoreCase = true)
                            correctness[index] = correct // Update correctness based on current guess

                            // Increment score only if the guess is correct, not already scored, and attempts are < 3
                            if (correct && !scored[index] && attempt < 3) {
                                score++
                                scored[index] = true // Mark as scored
                            }
                        }
                    }

                    if (correctness.all { it } || attempt >= 3 || showButton) {
                        // Reset for the next round
                        countryCodes.clear()
                        countryCodes.addAll(mainFunctions.pickRandomCountryCodesList(countriesJson, 3))
                        guesses.replaceAll { "" }
                        correctness.replaceAll { false }
                        scored.replaceAll { false }
                        attempt = 0
                        showButton = false
                        stopTimer = false
                        resetTimer = !resetTimer
                    } else {
                        attempt++
                        if (attempt == 3) {
                            stopTimer = true
                            // Show correct answers for any remaining incorrect guesses
                            countryCodes.indices.forEach { index ->
                                if (!correctness[index]) { // Only update guesses that were incorrect
                                    guesses[index] = countriesJson.getString(countryCodes[index])
                                }
                            }
                        }
                    }
                }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(text = if (showButton || attempt  >= 3) "Next" else "Submit", color = Color.Black)
                }
            }
        }
    }
}