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

val mainFunctions = MainFunctions()

class MainFunctions {

    // Loads the countries JSON from the assets folder.
    // This method reads a JSON file containing country codes and names, which is used throughout the game.
    fun loadCountriesJson(context: Context): JSONObject {
        val inputStream = context.assets.open("countries.json")
        val jsonStr = inputStream.bufferedReader().readText()
        return JSONObject(jsonStr)
    }

    // Randomly selects a country code from the loaded JSON.
    fun pickRandomCountryCode(countriesJson: JSONObject): String {
        val keys = countriesJson.keys().asSequence().toList()
        val randomIndex = Random.nextInt(keys.size)
        return keys[randomIndex]
    }

    // Randomly selects a country code from the loaded JSON and return them as a List
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
        //Get the ID int of the selected country
        val imageId = context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)

        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
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

    // Displays the flag image for the current country and make it clickable
    @SuppressLint("DiscouragedApi")
    @Composable
    fun ClickableFlagImage(countryCode: String, onClick: () -> Unit) {
        val context = LocalContext.current
        val imageId = context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)

        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Flag of $countryCode",
                modifier = Modifier
                    .size(150.dp)
                    .clickable { onClick() }
            )
        } else {
            Text(text = "Image not found", Modifier.padding(4.dp))
        }
    }

    // Displays the custom size flag image for the Advanced Level game mode
    @SuppressLint("DiscouragedApi")
    @Composable
    fun AdvancedLevelFlagImage(countryCode: String) {
        val context = LocalContext.current
        val imageId = context.resources.getIdentifier(countryCode.lowercase(), "drawable", context.packageName)

        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
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