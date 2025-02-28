package com.example.individualassignment_51

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorLong
import com.example.individualassignment_51.ui.theme.IndividualAssignment_51Theme
import java.lang.Math.pow
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    //make sensor manager and pressure sensor value
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    //make mutable state value for pressure
    private var pressure by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager and sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        enableEdgeToEdge()
        setContent {
            IndividualAssignment_51Theme {
                MakeScreen(pressure)
            }
        }
    }

    // Registers the sensor when the app starts (onResume)
    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // Unregisters the sensor when the app is paused (onPause) to save battery
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //updates pressure when sensor value changes
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            pressure = it.values[0]
        }
    }

    //implementation unneeded
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not needed
    }
}

//simple function to perform elevation calculation
fun calculateElevation(P: Float): Double{
    return 44330.0 * (1 - (P / 1013.25).pow(1 / 5.255))
}

//function to make full screen
@Composable
fun MakeScreen(pressure: Float){
    val elevation = calculateElevation(pressure)    //get elevation
    /*
    I create a color shift animation that changes with the pressure.
    It needs to be in grayscale. So, I convert the pressure value into a value
    within the range of 0 to 255; the range a single r, g, or b value can have.
    I make r, g, and b all this value to keep a shade of gray.
    */
    var colorPart = ((255.0/1013.0) * pressure).toInt()
    colorPart = if(colorPart>255) 255 else colorPart    //If the pressure goes above atmospheric, keep the color white
    //animation to change the color with the pressure
    val animatedColor by animateColorAsState(
        targetValue = Color(red = colorPart, green = colorPart,blue = colorPart),
        label = "color"
    )
    Scaffold(
    ) {innerPadding->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                //column background color will change
                .background(color = animatedColor)
        ) {
            //print out both the pressure and calculated elevation.
            Text(
                text = String.format("Pressure: %.3f hPa", pressure),
                fontSize = 26.sp,
                //ensure the text is always visible with a set background color
                modifier = Modifier.background(color = Color.White)
            )
            Text(
                text = String.format("Elevation: %.3f m", elevation),
                fontSize = 26.sp,
                modifier = Modifier.background(color = Color.White)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndividualAssignment_51Theme {
    }
}