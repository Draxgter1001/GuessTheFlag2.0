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

    private fun loadCountriesJson(): JSONObject {
        val inputStream = assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    private fun pickRandomCountryCodes(countriesJson: JSONObject, amount: Int): List<String> {
        val keys = countriesJson.keys().asSequence().toList()
        return List(amount) { keys[Random.nextInt(keys.size)] }.distinct()
    }

    @Composable
    fun GuessTheFlagGame() {
        val countriesJson = remember { loadCountriesJson() }
        var correctCountryCode by remember { mutableStateOf(pickRandomCountryCodes(countriesJson, 1).first()) }
        var flags by remember { mutableStateOf(pickRandomCountryCodes(countriesJson, 3)) }
        var message by remember { mutableStateOf("") }
        var messageColor by remember { mutableStateOf(Color.Black) }
        var flagClicked by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = countriesJson.getString(correctCountryCode),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                flags.forEach { countryCode ->
                    FlagImage(countryCode = countryCode, onClick = {
                        flagClicked = true
                        if (countryCode == correctCountryCode) {
                            message = "CORRECT!"
                            messageColor = Color.Green
                        } else {
                            message = "WRONG!"
                            messageColor = Color.Red
                        }
                    })
                }

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = messageColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacing
                Button(
                    onClick = {
                        flags = pickRandomCountryCodes(countriesJson, 3)
                        correctCountryCode = flags.random()
                        message = ""
                        flagClicked = false
                    },
                    enabled = flagClicked,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Next")
                }
            }
        }
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
                    .size(200.dp)
                    .clickable { onClick() }
            )
        } else {
            Text(text = "Image not found", Modifier.padding(4.dp))
        }
    }
}