package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import kotlin.random.Random

class AdvancedLevelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdvancedLevelContent()
        }
    }

    private fun loadCountriesJson(): JSONObject {
        val inputStream = assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    // Randomly selects a country code from the loaded JSON.
    // This is used to select a new country each time the user plays or moves to the next question.
    private fun pickRandomCountryCodes(countriesJson: JSONObject, amount: Int): List<String> {
        val keys = countriesJson.keys().asSequence().toList()
        return List(amount) { keys[Random.nextInt(keys.size)] }.distinct()
    }

    @Composable
    fun AdvancedLevelContent() {
        val countriesJson = remember { loadCountriesJson() }
        val countryCodes = remember { pickRandomCountryCodes(countriesJson, 3).toMutableStateList() }
        var score by remember { mutableStateOf(0) }
        var attempt by remember { mutableStateOf(0) }
        var guesses = remember { mutableStateListOf("", "", "") }
        var correctness = remember { mutableStateListOf(false, false, false) }
        var scored = remember { mutableStateListOf(false, false, false) } // New state to track score updates


        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("Score: $score", style = MaterialTheme.typography.titleLarge)
                }

                countryCodes.forEachIndexed { index, countryCode ->
                    FlagImage(countryCode = countryCode)
                    TextField(
                        value = guesses[index],
                        onValueChange = { value ->
                            if (!correctness[index] && attempt < 3) {
                                guesses[index] = value
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
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

                if(attempt > 0){
                    val attemptsLeft = 3 - attempt
                    Text(
                        "Attempts left: $attemptsLeft",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(onClick = {
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

                    if (correctness.all { it }) {
                        // Reset for the next round
                        countryCodes.clear()
                        countryCodes.addAll(pickRandomCountryCodes(countriesJson, 3))
                        guesses.replaceAll { "" }
                        correctness.replaceAll { false }
                        scored.replaceAll { false }
                        attempt = 0
                    } else {
                        attempt++
                        if (attempt == 3) {
                            // Show correct answers for any remaining incorrect guesses
                            countryCodes.indices.forEach { index ->
                                if (!correctness[index]) { // Only update guesses that were incorrect
                                    guesses[index] = countriesJson.getString(countryCodes[index])
                                }
                            }
                        }
                    }
                }, modifier = Modifier.padding(8.dp)) {
                    Text(if (attempt >= 3) "Next" else "Submit")
                }
            }
        }
    }

    // Displays the flag image for the current country.
    // This method attempts to load an image resource based on the country code.
    // If not found, it displays a placeholder text.
    @SuppressLint("DiscouragedApi")
    @Composable
    fun FlagImage(countryCode: String) {
        val context = LocalContext.current
        val resourceId = context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)

        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Flag of $countryCode",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(20.dp)
            )
        } else {
            Text(text = "Image not found", Modifier.fillMaxSize())
        }
    }
}