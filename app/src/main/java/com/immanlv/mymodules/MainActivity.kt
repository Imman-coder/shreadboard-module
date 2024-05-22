package com.immanlv.mymodules

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.immanlv.mymodules.ui.theme.MyModulesTheme
import com.immanlv.shredboard.Shredboard
import com.immanlv.shredboard.data.ShredboardConfig
import com.immanlv.shredboard.sharpNotation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyModulesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(heightDp = 1000, widthDp = 1000)
@Composable
private fun ShredboardPreview() {
    MyModulesTheme(darkTheme = false) {
        Box {
            Shredboard(
                config = ShredboardConfig(
                    numColumns = 7,
                    numRows = 7,
                    notation = sharpNotation,
                    rootNote = 0,
                    lowestNote = 9
                )
            ) {

            }
        }
    }


}