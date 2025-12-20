package sathish.project.donationapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

//data class PickupRequest(
//    val requestId: String = "",
//    val campaignId: String = "",
//    val userName: String = "",
//    val phone: String = "",
//    val address: String = "",
//    val items: List<String> = emptyList(),
//    val imageUrl: String = "",
//    val status: String = "PENDING",
//    val createdAt: Long = System.currentTimeMillis(),
//    val campaignTitle: String = ""
//)

data class PickupRequest(
    val requestId: String = "",
    val campaignId: String = "",
    val campaignTitle: String = "",
    val userName: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val items: List<String> = emptyList(),
    val imageUrl: String = "",
    val status: String = "PENDING",
    val createdAt: Long = System.currentTimeMillis()
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupRequestScreen(
    campaignId: String,
    campaignTitle: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var items by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it
        }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var selectedAddress by remember { mutableStateOf("") }

    val locationPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                selectedLat = it.data?.getDoubleExtra("lat", 0.0)
                selectedLng = it.data?.getDoubleExtra("lng", 0.0)
                selectedAddress = it.data?.getStringExtra("address") ?: ""
            }
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pickup Request") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                campaignTitle,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                name,
                { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                phone,
                { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                address,
                { address = it },
                label = { Text("Pickup Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = items,
                onValueChange = { items = it },
                label = { Text("Items to Donate (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    locationPicker.launch(
                        Intent(context, LocationPickerActivity::class.java)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Text(
                    if (selectedAddress.isEmpty())
                        "📍 Select Pickup Location"
                    else
                        "📍 Change Location"
                )
            }


            if (selectedAddress.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    selectedAddress,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }


            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Text(if (imageUri == null) "Upload Item Photo" else "Change Photo")
            }

            imageUri?.let {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || items.isBlank()) {
                        Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    isSubmitting = true

                    scope.launch {
                        val base64 = uriToBase64(context, imageUri!!)
                        val imageUrl = uploadToImgBB(base64)

                        if (imageUrl == null) {
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT)
                                .show()
                            return@launch
                        }

                        if (selectedLat == null || selectedAddress.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please select pickup location",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }


                        val userEmail = UserPrefs.getEmail(context).replace(".", "_")

                        val ref = FirebaseDatabase.getInstance()
                            .getReference("PickupRequests")
                            .child(userEmail)
                            .push()

//                        val request = PickupRequest(
//                            requestId = ref.key!!,
//                            campaignId = campaignId,
//                            userName = name,
//                            phone = phone,
//                            address = address,
//                            items = items.split(",").map { it.trim() },
//                            imageUrl = imageUrl,
//                            campaignTitle = campaignTitle
//                        )

                        val request = PickupRequest(
                            requestId = ref.key!!,
                            campaignId = campaignId,
                            campaignTitle = campaignTitle,
                            userName = name,
                            phone = phone,
                            address = selectedAddress,
                            latitude = selectedLat!!,
                            longitude = selectedLng!!,
                            items = items.split(",").map { it.trim() },
                            imageUrl = imageUrl
                        )


                        ref.setValue(request)
                        Toast.makeText(context, "Pickup request submitted 🚚", Toast.LENGTH_LONG)
                            .show()
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Submit Pickup Request", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun uriToBase64(context: Context, uri: Uri): String {
    val input = context.contentResolver.openInputStream(uri)
    val bytes = input!!.readBytes()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}


private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b" // <- your key (from user)

suspend fun uploadToImgBB(base64Image: String): String? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        // ImgBB accepts 'image' param as base64 string
        val form = FormBody.Builder()
            .add("key", IMGBB_API_KEY)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(form)
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return@withContext null
            if (!response.isSuccessful) return@withContext null

            val json = JSONObject(body)
            // Look for data -> url or display_url
            val data = json.optJSONObject("data")
            return@withContext data?.optString("url") ?: data?.optString("display_url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}


class PickupRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickupRequestScreen(
                campaignId = intent.getStringExtra("campaign_id") ?: "",
                campaignTitle = intent.getStringExtra("campaign_title") ?: "",
                onBack = { finish() }
            )
        }
    }
}