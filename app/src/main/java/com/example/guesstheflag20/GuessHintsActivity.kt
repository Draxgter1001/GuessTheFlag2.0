package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import kotlin.random.Random


class GuessHintsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessHintsContent()
        }
    }

    private fun loadCountriesJson(): JSONObject {
        val jsonStr = assets.open("countries.json").bufferedReader().use { it.readText() }
        return JSONObject(jsonStr)
    }

    private fun pickRandomCountryCode(countriesJson: JSONObject): String {
        val keys = countriesJson.keys().asSequence().toList()
        return keys[Random.nextInt(keys.size)]
    }

    @Composable
    fun GuessHintsContent() {
        val countriesJson by remember { mutableStateOf(loadCountriesJson()) }
        var currentCountryCode by remember { mutableStateOf(pickRandomCountryCode(countriesJson)) }
        var userGuess by remember { mutableStateOf("") }
        var dashes by remember { mutableStateOf("_".repeat(countriesJson.getString(currentCountryCode).length)) }
        var remainingAttempts by remember { mutableStateOf(3) }
        var message by remember { mutableStateOf("") }

        // Update this Column to use LaunchedEffect for automatically resetting game state
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            FlagImage(countryCode = currentCountryCode)
            Text(text = dashes, style = TextStyle(fontSize = 30.sp))
            TextField(
                value = userGuess,
                onValueChange = {
                    if (it.length <= 1) { // Restrict input to single character
                        userGuess = it
                    }
                },
                label = { Text("Enter a character") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                // Removed KeyboardActions for simplicity; logic is now within Button onClick
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    if (userGuess.isNotBlank()) { // Ensure non-blank input
                        submitGuess(
                            userGuess.lowercase().firstOrNull(),
                            countriesJson.getString(currentCountryCode),
                            remainingAttempts,
                            dashes
                        ) { newDashes, newMessage, newRemainingAttempts, guessedCorrectly ->
                            dashes = newDashes
                            message = newMessage
                            remainingAttempts = newRemainingAttempts
                            if (guessedCorrectly || remainingAttempts <= 0) {
                                userGuess = ""
                                currentCountryCode = pickRandomCountryCode(countriesJson)
                                dashes = "_".repeat(countriesJson.getString(currentCountryCode).length)
                                remainingAttempts = 3
                                message = if (guessedCorrectly) "CORRECT!" else "WRONG! The correct country was: ${countriesJson.getString(currentCountryCode)}"
                            }
                        }
                        userGuess = "" // Reset userGuess after each submission
                    }
                },
                enabled = userGuess.isNotBlank() && remainingAttempts > 0,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Submit")
            }
            if (message.isNotEmpty()) {
                Text(text = message, modifier = Modifier.padding(vertical = 8.dp), color = when {
                    message.startsWith("CORRECT") -> Color.Green
                    message.startsWith("WRONG") -> Color.Red
                    else -> Color.Black
                })
            }
        }
    }

    private fun submitGuess(
        guessedChar: Char?,
        countryName: String,
        remainingAttempts: Int,
        currentDashes: String,
        onResult: (String, String, Int, Boolean) -> Unit
    ) {
        if (guessedChar == null) return

        val newDashes = StringBuilder(currentDashes)
        var newRemainingAttempts = remainingAttempts
        var newMessage = ""
        var guessedCorrectly = false

        var found = false
        countryName.forEachIndexed { index, char ->
            if (char.equals(guessedChar, ignoreCase = true)) {
                newDashes[index] = char
                found = true
            }
        }

        if (!found) {
            newRemainingAttempts--
        } else if (!newDashes.contains('-')) {
            guessedCorrectly = true
        }

        newMessage = when {
            guessedCorrectly -> "CORRECT!"
            newRemainingAttempts <= 0 -> "WRONG! The correct country was: $countryName"
            else -> "Incorrect guess! $newRemainingAttempts attempts left."
        }

        onResult(newDashes.toString(), newMessage, newRemainingAttempts, guessedCorrectly)
    }

    @SuppressLint("DiscouragedApi")
    @Composable
    fun FlagImage(countryCode: String) {
        val context = LocalContext.current
        val resourceId = context.resources.getIdentifier(
            countryCode.lowercase(), "drawable", context.packageName
        )

        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Flag of $countryCode",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)
            )
        } else {
            Text(
                text = "Flag Image not found",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



