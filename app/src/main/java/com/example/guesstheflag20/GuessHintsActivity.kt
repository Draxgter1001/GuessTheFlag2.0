package com.example.guesstheflag20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class GuessHintsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessHintsContent()
        }
    }

    // The main content of the GuessHintsActivity, comprising UI components and game logic.
    @Composable
    fun GuessHintsContent() {
        // Remembered state variables to manage game status, input, attempts, and messages.
        val countriesJson by remember { mutableStateOf(additionalFunctions.loadCountriesJson(this)) }
        var currentCountryCode by remember { mutableStateOf(additionalFunctions.pickRandomCountryCode(countriesJson)) }
        var userGuess by remember { mutableStateOf("") }
        var dashes by remember { mutableStateOf("_".repeat(countriesJson.getString(currentCountryCode).length)) }
        var remainingAttempts by remember { mutableStateOf(3) }
        var message by remember { mutableStateOf("") }
        var showNextButton by remember { mutableStateOf(false) }

        // Layout for displaying the game UI, including the flag, input field, and buttons.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            additionalFunctions.FlagImage(countryCode = currentCountryCode)
            Text(text = dashes, style = TextStyle(fontSize = 30.sp))
            TextField(
                value = userGuess,
                onValueChange = {
                    if (it.length <= 1) { // Ensure only single characters are inputted.
                        userGuess = it
                    }
                },
                label = { Text("Enter a character") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Button for submitting guesses or moving to the next flag, with logic for disabling/enabling based on input or game state.
            Button(
                onClick = {
                    if (!showNextButton) {
                        if (userGuess.isNotBlank()) {
                            // Function call to process the guess and update the game state accordingly.
                            submitGuess(userGuess.lowercase().firstOrNull(), countriesJson.getString(currentCountryCode), remainingAttempts, dashes) { newDashes, newMessage, newRemainingAttempts, guessedCorrectly ->
                                dashes = newDashes
                                message = newMessage
                                remainingAttempts = newRemainingAttempts
                                showNextButton = guessedCorrectly || remainingAttempts <= 0
                            }
                            userGuess = "" // Clear input after each submission.
                        }
                    } else {
                        // Logic for setting up the game for the next flag.
                        currentCountryCode = additionalFunctions.pickRandomCountryCode(countriesJson)
                        dashes = "_".repeat(countriesJson.getString(currentCountryCode).length)
                        remainingAttempts = 3
                        message = ""
                        showNextButton = false
                    }
                },
                enabled = userGuess.isNotBlank() || showNextButton,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(if (!showNextButton) "Submit" else "Next")
            }

            // Displays messages to the user about their guess's correctness, remaining attempts, or the correct answer.
            if (message.isNotBlank()) {
                val messageColor = when {
                    message.startsWith("CORRECT!") -> Color.Green
                    remainingAttempts <= 0 && message.startsWith("WRONG! The correct country was:") -> Color.Blue
                    message.startsWith("WRONG!") -> Color.Red
                    else -> Color.Black // Default color for messages that are neither correct nor wrong guesses.
                }

                Text(
                    text = message,
                    color = messageColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    // Function to process the user's guess, update the display of dashes (for guessed letters), and manage attempts.
    private fun submitGuess(
        guessedChar: Char?,
        countryName: String,
        remainingAttempts: Int,
        currentDashes: String,
        onResult: (String, String, Int, Boolean) -> Unit
    ) {
        if (guessedChar == null) return // Guard clause for null input.

        val newDashes = StringBuilder(currentDashes)
        var newRemainingAttempts = remainingAttempts
        var guessedCorrectly = false

        var found = false
        countryName.forEachIndexed { index, char ->
            if (char.equals(guessedChar, ignoreCase = true) && newDashes[index] == '_') {
                newDashes[index] = char
                found = true
            }
        }

        if (!found) {
            newRemainingAttempts--
            // Logic for revealing a character when the guess is wrong, to help guide further guesses.
            if (newRemainingAttempts > 0) {
                val indicesToReveal = mutableListOf<Int>()
                newDashes.forEachIndexed { index, c ->
                    if (c == '_') indicesToReveal.add(index)
                }
                if (indicesToReveal.isNotEmpty()) {
                    val randomIndexToReveal = indicesToReveal.random()
                    newDashes[randomIndexToReveal] = countryName[randomIndexToReveal]
                }
            }
        }

        guessedCorrectly = !newDashes.contains('_') // Determine if the game is won.

        val newMessage = when {
            guessedCorrectly -> "CORRECT! The correct country was: $countryName"
            newRemainingAttempts <= 0 -> "WRONG! The correct country was: $countryName"
            else -> "Attempts left: $newRemainingAttempts"
        }
        onResult(newDashes.toString(), newMessage, newRemainingAttempts, guessedCorrectly)
    }
}