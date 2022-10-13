package com.trusted.donation.doted

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.trusted.donation.doted.model.Story
import com.trusted.donation.doted.ui.theme.DotedTheme
import java.text.DecimalFormat

private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            DotedApp(this)
        }
    }
}

@Composable
fun DotedApp(context: Context) {
    var index by rememberSaveable { mutableStateOf(0) }
    var position by rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling ActivityCompat#requestPermissions
    } else {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    position = LatLng(it.latitude, it.longitude)
                }
            }
    }

    DotedTheme {
        Scaffold(
            topBar = {
                Text(
                    modifier = Modifier.padding(all = 16.dp),
                    text = "We Need Help"
                )
            },
            bottomBar = {
                CustomBottomNavigation(currentIndex = index) {
                    index = it
                }
            }
        ) { padding ->
            if (index == 0)
                MapTab(Modifier.padding(padding), position)
            else
                ListTab(Modifier.padding(padding), position)
        }
    }
}

@Composable
fun ListTab(modifier: Modifier = Modifier, currentPosition: LatLng){
    LazyColumn(
        contentPadding = PaddingValues(all = 16.dp),
        modifier = modifier,
    ) {
        items(stories) { item ->
            itemListTab(modifier, item, currentPosition)
            Divider()
        }
    }
}

@Composable
fun itemListTab(modifier: Modifier = Modifier, story: Story, currentPosition: LatLng){
    var floatResults = FloatArray(1)
    val df = DecimalFormat("#.##")
    Location.distanceBetween(
        currentPosition.latitude,
        currentPosition.longitude,
        story.latitude,
        story.longitude,
        floatResults
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                ) {
            Text(text = story.title)
            Text(text = story.snippet)
        }
        Text(text = "${df.format(floatResults[0] / 1000)} km")
    }
}

@Composable
fun MapTab(modifier: Modifier = Modifier, currentPosition: LatLng){
    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(currentPosition, 15f)
    )

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = currentPosition)
        )
    }
}

@Composable
private fun CustomBottomNavigation(
    modifier: Modifier = Modifier,
    currentIndex: Int = 0,
    onSelected: (index: Int) -> Unit) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null
                )
            },
            selected = currentIndex == 0,
            onClick = {
                onSelected(0)
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null
                )
            },
            selected = currentIndex == 1,
            onClick = {
                onSelected(1)
            }
        )
    }
}

private val stories = listOf(
    Story(-28.263507236638343, -52.39932947157066, "Harosin", "Sollicitudin aliquam ipsum aptent id dictumst ligula curae libero senectus aliquet, cubilia scelerisque laoreet aliquet tempor quis fermentum ullamcorper interdum erat, massa placerat cubilia torquent arcu praesent tempor erat aptent.", 0, 0),
    Story(-28.260889627977562, -52.400177049595555, "Dagar", "Viverra sodales vitae congue iaculis interdum class primis hac proin bibendum, diam erat ut aenean viverra gravida venenatis elit pulvinar conubia, primis est dui feugiat curae hac mauris egestas sodales. ", 0, 0),
    Story(-28.261456624477784, -52.39208750743389, "Arve", "Habitant bibendum vel habitasse cursus quis sollicitudin dapibus tristique, congue suspendisse ut aptent ut tincidunt nam libero luctus, lorem ullamcorper quam ultricies congue curae pharetra. consectetur lacus faucibus sodales, imperdiet. ", 0, 0),
    Story(-28.26597358890354, -52.402505206969835, "Husaol", "Pharetra pretium donec commodo torquent vestibulum class turpis, purus sed gravida dolor dictumst auctor adipiscing, mattis eros venenatis nostra augue rutrum. euismod malesuada etiam tellus cras fames, convallis donec sociosqu. ", 0, 0),
    Story(-28.2705761904961, -52.39146318305821, "Tagalan", "Felis senectus habitasse facilisis torquent quis consectetur class, bibendum quam libero arcu pharetra proin iaculis nisl, praesent sed adipiscing nec nam iaculis. potenti imperdiet pellentesque facilisis nisl, quisque cursus purus. ", 0, 0),
    Story(-28.242828, -52.381907, "Hiesaipen", "Mauris accumsan hendrerit consequat pharetra torquent elementum curabitur, etiam sed adipiscing cras vel tellus, a donec augue eu eget himenaeos. ", 0, 0),
    Story(-28.241999, -52.438139, "Zokgaelu", "Nullam rutrum dictum mauris fermentum cursus quis fusce, litora augue pulvinar sem primis egestas, risus erat vestibulum curabitur lorem libero. ", 0, 0),
    Story(-28.181835, -52.328675, "Curuas", "Tempor accumsan libero consequat phasellus tellus nullam mi, nibh placerat sagittis magna himenaeos tempus, rutrum proin lacus imperdiet ad nisl.", 0, 0),
    Story(-28.124979, -52.296718, "Buigrak", "Vulputate elementum sem bibendum ad pretium pellentesque metus, nostra quisque in dolor lectus mollis, ante donec sapien netus laoreet congue. ", 0, 0),
    Story(-28.107857, -52.145972, "Lomoa", "Dolor quisque tellus purus sagittis potenti ipsum nunc, porttitor magna sit aliquam erat lacinia, a phasellus curabitur diam tempus primis. ", 0, 0)
)