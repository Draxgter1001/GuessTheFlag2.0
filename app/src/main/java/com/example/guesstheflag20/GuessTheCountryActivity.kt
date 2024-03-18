package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import kotlin.random.Random

class GuessTheCountryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessTheCountryActivityContent()
        }
    }

    private fun loadCountriesJson(): JSONObject {
        val inputStream = assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    private fun pickRandomCountryCode(countriesJson: JSONObject): String {
        val keys = countriesJson.keys().asSequence().toList()
        val randomIndex = Random.nextInt(keys.size)
        return keys[randomIndex]
    }

    @Composable
    fun GuessTheCountryActivityContent() {
        val countriesJson = loadCountriesJson()
        val countriesList = countriesJson.keys().asSequence().map { key ->
            key to countriesJson.getString(key)
        }.toList()

        var currentCountryCode by remember { mutableStateOf(pickRandomCountryCode(countriesJson)) }
        var userGuess by remember { mutableStateOf("") }
        var guessResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
        var showList by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlagImage(countryCode = currentCountryCode)

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

            Button(
                onClick = {
                    val correctAnswer = countriesJson.getString(currentCountryCode)
                    guessResult = Pair(userGuess == correctAnswer, correctAnswer)
                },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text("Submit")
            }

            guessResult?.let {
                Text(
                    text = if (it.first) "CORRECT!" else "WRONG",
                    color = if (it.first) Color.Green else Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "The correct country was: ${it.second}",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        currentCountryCode = pickRandomCountryCode(countriesJson)
                        userGuess = "" // Reset user guess
                        guessResult = null // Reset for the next guess
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text("Next")
                }
            }
        }
    }

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
                    .height(350.dp)
                    .padding(20.dp)
            )
        } else {
            Text(text = "Image not found", Modifier.fillMaxSize())
        }
    }
}