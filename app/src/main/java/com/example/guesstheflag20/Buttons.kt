package com.example.guesstheflag

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guesstheflag20.AdvancedLevelActivty
import com.example.guesstheflag20.GuessHintsActivity
import com.example.guesstheflag20.GuessTheCountryActivity
import com.example.guesstheflag20.GuessTheFlagActivity

@Composable
fun MultipleButtons(context: Context){

    val fontSize by remember { mutableStateOf(24.sp) }

    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {
        Button(onClick = { val intent = Intent(context, GuessTheCountryActivity::class.java)
            context.startActivity(intent)}, modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
            .padding(top = 4.dp)) {
            Text(text = "Guess The Country", style = TextStyle(fontSize = fontSize))
        }

        Button(onClick = { val intent = Intent(context, GuessHintsActivity::class.java)
            context.startActivity(intent)}, modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .padding(top = 30.dp)) {
            Text(text = "Guess-Hints", style = TextStyle(fontSize = fontSize))
        }

        Button(onClick = { val intent = Intent(context, GuessTheFlagActivity::class.java)
            context.startActivity(intent) }, modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .padding(top = 30.dp)) {
            Text(text = "Guess The Flag", style = TextStyle(fontSize = fontSize))
        }

        Button(onClick = { val intent = Intent(context, AdvancedLevelActivty::class.java)
            context.startActivity(intent) }, modifier = Modifier
            .size(width = 300.dp, height = 100.dp)
            .padding(top = 30.dp)) {
            Text(text = "Advanced Level", style = TextStyle(fontSize = fontSize))
        }
    }
}