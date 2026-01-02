package project.donationapp.s3342816sathishreddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.firebase.database.*
import kotlinx.coroutines.launch

data class DonationCenter(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val contact: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val image: String = "",
    val info: String = ""
)

class LocateDonationCentersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LocateDonationCentersScreen(onBack = { finish() }) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocateDonationCentersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference("DonationCenters")

    var centers by remember { mutableStateOf<List<DonationCenter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCenter by remember { mutableStateOf<DonationCenter?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var showFavouritesOnly by remember { mutableStateOf(false) }


    val visibleCenters = remember(centers, showFavouritesOnly) {
        if (!showFavouritesOnly) centers
        else centers.filter {
            FavouriteCentersPrefs.isFavourite(context, it.id)
        }
    }


    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DonationCenter>()
                for (child in snapshot.children) {
                    val center = child.getValue(DonationCenter::class.java)
                    if (center != null) list.add(center)
                }
                centers = list
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Locate Donation Centers",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.SkyBlue)
                )
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(54.0, -2.0), 5.5f)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                ) {


                    visibleCenters.forEach { center ->
                        Marker(
                            state = MarkerState(LatLng(center.latitude, center.longitude)),
                            title = center.name,
                            snippet = center.address,
                            onClick = {
                                selectedCenter = center
                                coroutineScope.launch { sheetState.show() }
                                true
                            }
                        )
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showFavouritesOnly) "⭐ Favourite Centers" else "🏥 All Centers",
                        fontWeight = FontWeight.SemiBold
                    )

                    Switch(
                        checked = showFavouritesOnly,
                        onCheckedChange = { showFavouritesOnly = it }
                    )
                }

                if (selectedCenter != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            coroutineScope.launch {
                                sheetState.hide()
                                selectedCenter = null
                            }
                        },
                        sheetState = sheetState,
                        containerColor = Color.White
                    ) {
                        DonationCenterDetails(center = selectedCenter!!) {
                            coroutineScope.launch {
                                sheetState.hide()
                                selectedCenter = null
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DonationCenterDetails(center: DonationCenter, onClose: () -> Unit) {
    val context = LocalContext.current
    var isFavourite by remember {
        mutableStateOf(FavouriteCentersPrefs.isFavourite(context, center.id))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Box {
            AsyncImage(
                model = center.image,
                contentDescription = center.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            IconButton(
                onClick = {
                    FavouriteCentersPrefs.toggleFavourite(context, center.id)
                    isFavourite = !isFavourite
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isFavourite)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = Color.Red
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(center.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(center.address, color = Color.Gray)
        Text("📞 ${center.contact}", color = Color(0xFF4CAF50))

        Spacer(Modifier.height(10.dp))
        Text(center.info)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${center.contact}"))
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("📞 Call Now", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onClose,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Close")
        }
    }
}

