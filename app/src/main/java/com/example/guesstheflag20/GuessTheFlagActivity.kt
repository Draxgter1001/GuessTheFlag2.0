package com.example.guesstheflag20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guesstheflag20.ui.theme.GuessTheFlag20Theme
import kotlinx.coroutines.delay

class GuessTheFlagActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessTheFlag20Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GuessTheFlagGameContent()
                }
            }
        }
    }

    @Composable
    fun GuessTheFlagGameContent() {
        val countriesJson = mainFunctions.loadCountriesJson(this)
        // Pick 3 random country codes and store them in a mutable state list
        var correctCountryCode by remember { mutableStateOf(mainFunctions.pickRandomCountryCodesList(countriesJson, 1).first()) }
        var flags by remember { mutableStateOf(mainFunctions.pickRandomCountryCodesList(countriesJson, 3)) }
        var message by rememberSaveable { mutableStateOf("") }
        var messageColor by remember { mutableStateOf(Color.Black) }
        var flagClicked by rememberSaveable { mutableStateOf(false) }
        var showButton by rememberSaveable { mutableStateOf(false) }
        var timerValue by rememberSaveable { mutableStateOf(10) }
        var resetTimer by rememberSaveable { mutableStateOf(false) }

        if(setTimer){
            LaunchedEffect(resetTimer) {
                timerValue = 10
                while (timerValue > 0 && !showButton) {
                    delay(1000) // Wait for 1 second
                    timerValue--
                }
                if (timerValue == 0 && !showButton) {
                    showButton = true
                    flagClicked = true
                }
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if(setTimer){
                    Text(text = "Time left: $timerValue", style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    text = "Flag to guess: ${countriesJson.getString(correctCountryCode)}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                flags.forEach { countryCode ->
                    mainFunctions.ClickableFlagImage(countryCode = countryCode, onClick = {
                        flagClicked = true
                        if (countryCode == correctCountryCode) {
                            message = "CORRECT!"
                            messageColor = Color.Green
                        } else {
                            message = "WRONG!"
                            messageColor = Color.Red
                        }
                        showButton = true
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
                Spacer(modifier = Modifier.height(8.dp))

                if(timerValue <= 0){
                    Text(
                        text = "Time's up! Press on the button below to try again!",
                        color = Color.Blue
                    )
                }
                if(showButton){
                    Button(
                        onClick = {
                            flags = mainFunctions.pickRandomCountryCodesList(countriesJson, 3)
                            correctCountryCode = flags.random()
                            message = ""
                            flagClicked = false
                            showButton = false
                            resetTimer = !resetTimer
                        },
                        enabled = flagClicked,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(text = "Next", color = Color.Black, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}