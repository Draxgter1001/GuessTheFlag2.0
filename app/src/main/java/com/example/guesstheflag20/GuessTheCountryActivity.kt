package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guesstheflag20.ui.theme.GuessTheFlag20Theme
import org.json.JSONObject
import kotlin.random.Random


class GuessTheCountryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val countriesJson = loadCountriesJson()
        val countriesList = countriesJson.keys().asSequence().map { key ->
            key to countriesJson.getString(key) }.toList()


        setContent {
            var currentCountryCode by remember { mutableStateOf(pickRandomCountryCode(countriesJson)) }
            GuessTheFlag20Theme {
                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                ){
                    FlagImage(countryCode = currentCountryCode)
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
            countryCode.lowercase(),
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Flag of $countryCode",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp)
            )
        } else {

            Text(text = "Image not found", Modifier.fillMaxSize())
        }
    }
}

