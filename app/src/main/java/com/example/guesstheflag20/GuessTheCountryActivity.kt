package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.guesstheflag20.ui.theme.GuessTheFlag20Theme
import org.json.JSONObject
import kotlin.random.Random

class GuessTheCountryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val countriesJson = loadCountriesJson()
        val countriesList = countriesJson.keys().asSequence().map { key ->
            key to countriesJson.getString(key)
        }.toList()

        setContent {
            var currentCountryCode by remember {
                mutableStateOf(pickRandomCountryCode(countriesJson))
            }
            var userGuess by remember { mutableStateOf("") }
            var guessResult by remember{ mutableStateOf<Pair<Boolean, String>?>(null) }
            var showList by remember{ mutableStateOf(false) }

            GuessTheFlag20Theme {
                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                    .fillMaxSize())
                {
                    FlagImage(countryCode = currentCountryCode)
                    TextField(
                        value = userGuess,
                        onValueChange = { userGuess = it },
                        label = { Text("Select Country", style = TextStyle(fontSize =
                        MaterialTheme.typography.bodyLarge.fontSize))},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showList = !showList }) {
                                Icon(Icons.Filled.ArrowDropDown, "dropdown")
                            }
                        }
                    )
                    if (showList) {
                        LazyColumn(modifier = Modifier.fillMaxHeight(.5f)) {
                            items(countriesList) { country ->
                                TextButton(onClick = {
                                    userGuess = country.second
                                    showList = false
                                }) {
                                    Text(text = country.second, style = TextStyle(fontSize =
                                    MaterialTheme.typography.bodyLarge.fontSize))
                                }
                            }
                        }
                    }
                    Button(onClick = {
                        val correctAnswer = countriesJson.getString(currentCountryCode)
                        guessResult = Pair(userGuess == correctAnswer, correctAnswer)
                    }, modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                        Text("Submit", style = TextStyle(fontSize =
                        MaterialTheme.typography.bodyLarge.fontSize))
                    }

                    guessResult?.let {
                        Text(text = if (it.first) " CORRECT!" else "Wrong",
                            color = if (it.first) Color.Green else Color.Red,
                            style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            modifier = Modifier.padding(10.dp))
                        Text(text = "The correct country was: ${it.second}",style =
                        TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            color = Color.Blue,
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )

                        Button(onClick = {
                            currentCountryCode = pickRandomCountryCode(countriesJson)
                            userGuess = "" // Reset user guess
                            guessResult = null // Reset for the next guess
                        }, modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                            Text("Next", style = TextStyle(fontSize =
                            MaterialTheme.typography.bodyLarge.fontSize))
                        }
                    }
                }
            }

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

    @SuppressLint("DiscouragedApi") //The IDE is recommending me to put this SuppressLint
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
                modifier = Modifier.fillMaxWidth().height(350.dp).padding(20.dp)
            )
        } else {

            Text(text = "Image not found", Modifier.fillMaxSize())
        }
    }
}

