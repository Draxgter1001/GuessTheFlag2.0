package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import kotlin.random.Random

class GuessTheFlagActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessTheFlagGame()
        }
    }

    @Composable
    fun GuessTheFlagGame() {
        val context = LocalContext.current
        val countriesJson = remember { loadCountriesJson(context) }
        var correctCountryCode by remember { mutableStateOf(pickRandomCountryCodes(countriesJson, 1).first()) }
        var flags by remember { mutableStateOf(pickRandomCountryCodes(countriesJson, 3)) }
        var message by remember { mutableStateOf("") }
        var messageColor by remember { mutableStateOf(Color.Black) }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = countriesJson.getString(correctCountryCode), style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    flags.forEach { countryCode ->
                        FlagImage(countryCode = countryCode, onClick = {
                            if (countryCode == correctCountryCode) {
                                message = "CORRECT!"
                                messageColor = Color.Green
                            } else {
                                message = "WRONG!"
                                messageColor = Color.Red
                            }
                        })
                    }
                }
                if (message.isNotEmpty()) {
                    Text(text = message, color = messageColor, modifier = Modifier.padding(8.dp))
                }
                Button(onClick = {
                    flags = pickRandomCountryCodes(countriesJson, 3)
                    correctCountryCode = flags.random()
                    message = ""
                }) {
                    Text("Next")
                }
            }
        }
    }

    private fun loadCountriesJson(context: android.content.Context): JSONObject {
        val inputStream = context.assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    private fun pickRandomCountryCodes(countriesJson: JSONObject, amount: Int): List<String> {
        val keys = countriesJson.keys().asSequence().toList()
        return List(amount) { keys[Random.nextInt(keys.size)] }.distinct()
    }

    @SuppressLint("DiscouragedApi")
    @Composable
    fun FlagImage(countryCode: String, onClick: () -> Unit) {
        val context = LocalContext.current
        val resourceId = context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)

        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Flag of $countryCode",
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
                    .clickable { onClick() }
            )
        } else {
            Text(text = "Image not found", Modifier.padding(4.dp))
        }
    }
}


