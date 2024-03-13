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
import androidx.compose.foundation.text.KeyboardActions
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
        var currentCountryCode by remember {
            mutableStateOf(pickRandomCountryCode(countriesJson))
        }
        var userGuess by remember { mutableStateOf("") }
        var dashes by remember {
            mutableStateOf(
                buildString {
                    repeat(countriesJson.getString(currentCountryCode).length) {
                        append('-')
                    }
                }
            )
        }
        var remainingAttempts by remember { mutableStateOf(3) }
        var message by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlagImage(countryCode = currentCountryCode)
            Text(
                text = dashes,
                style = TextStyle(fontSize = 30.sp)
            )
            TextField(
                value = userGuess,
                onValueChange = { userGuess = it },
                label = { Text("Enter a character") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    submitGuess(
                        userGuess.lowercase().firstOrNull(),
                        countriesJson.getString(currentCountryCode),
                        remainingAttempts,
                        dashes
                    ) { newDashes, newMessage, newRemainingAttempts, guessedCorrectly ->
                        dashes = newDashes
                        message = newMessage
                        remainingAttempts = newRemainingAttempts
                        if (guessedCorrectly) {
                            userGuess = ""
                            currentCountryCode = pickRandomCountryCode(countriesJson)
                            dashes = buildString {
                                repeat(countriesJson.getString(currentCountryCode).length) {
                                    append('-')
                                }
                            }
                            remainingAttempts = 3
                        }
                    }
                }),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    submitGuess(
                        userGuess.lowercase().firstOrNull(),
                        countriesJson.getString(currentCountryCode),
                        remainingAttempts,
                        dashes
                    ) { newDashes, newMessage, newRemainingAttempts, guessedCorrectly ->
                        dashes = newDashes
                        message = newMessage
                        remainingAttempts = newRemainingAttempts
                        if (guessedCorrectly) {
                            userGuess = ""
                            currentCountryCode = pickRandomCountryCode(countriesJson)
                            dashes = buildString {
                                repeat(countriesJson.getString(currentCountryCode).length) {
                                    append('-')
                                }
                            }
                            remainingAttempts = 3
                        }
                    }
                },
                enabled = userGuess.isNotBlank() && remainingAttempts > 0,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Submit")
            }
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            if (remainingAttempts <= 0) {
                Button(
                    onClick = {
                        userGuess = ""
                        currentCountryCode = pickRandomCountryCode(countriesJson)
                        dashes = buildString {
                            repeat(countriesJson.getString(currentCountryCode).length) {
                                append('-')
                            }
                        }
                        remainingAttempts = 3
                        message = ""
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Next")
                }
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
        for (i in countryName.indices) {
            if (countryName[i].equals(guessedChar, ignoreCase = true)) {
                newDashes[i] = countryName[i]
                found = true
            }
        }

        if (!found) {
            newRemainingAttempts--
            newMessage = if (newRemainingAttempts <= 0) {
                "WRONG! The correct country was: $countryName"
            } else {
                "Incorrect guess! $newRemainingAttempts attempts left."
            }
        } else if (newDashes.toString() == countryName) {
            newMessage = "CORRECT!"
            guessedCorrectly = true
        }

        if (newMessage.isNotEmpty()) {
            onResult(newDashes.toString(), newMessage, newRemainingAttempts, guessedCorrectly)
        }
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



