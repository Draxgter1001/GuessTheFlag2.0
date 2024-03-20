package com.example.guesstheflag20

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import kotlin.random.Random

val additionalFunctions = AdditionalFunctions()

class AdditionalFunctions {

    // Loads the countries JSON from the assets folder.
    // This method reads a JSON file containing country codes and names, which is used throughout the game.
    fun loadCountriesJson(context: Context): JSONObject {
        val inputStream = context.assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    // Randomly selects a country code from the loaded JSON.
    // This is used to select a new country each time the user plays or moves to the next question.
    fun pickRandomCountryCode(countriesJson: JSONObject): String {
        val keys = countriesJson.keys().asSequence().toList()
        val randomIndex = Random.nextInt(keys.size)
        return keys[randomIndex]
    }

    fun pickRandomCountryCodesList(countriesJson: JSONObject, amount: Int): List<String> {
        val keys = countriesJson.keys().asSequence().toList()
        return List(amount) { keys[Random.nextInt(keys.size)] }.distinct()
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
                    .height(350.dp)
                    .padding(20.dp)
            )
        } else {
            Text(text = "Image not found", Modifier.fillMaxSize())
        }
    }

    @SuppressLint("DiscouragedApi")
    @Composable
    fun ClickableFlagImage(countryCode: String, onClick: () -> Unit) {
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

    @SuppressLint("DiscouragedApi")
    @Composable
    fun AdvancedLevelFlagImage(countryCode: String) {
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