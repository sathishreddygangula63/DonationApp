package sathish.project.donationapp

import android.R.attr.padding
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.roundToInt

data class DonationInfo(
    val donorName: String = "",
    val amount: Double = 0.0,
    val campaignId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class DonateNowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val campaignId = intent.getStringExtra("campaign_id")
        val campaignTitle = intent.getStringExtra("campaign_title")
        val campaignImage = intent.getStringExtra("campaign_image")
        val raised = intent.getDoubleExtra("raised_amount", 0.0)
        val goal = intent.getDoubleExtra("goal_amount", 1.0)

        setContent {
            DonateNowScreen(
                campaignId = campaignId ?: "",
                title = campaignTitle ?: "",
                imageUrl = campaignImage ?: "",
                raisedAmount = raised,
                goalAmount = goal,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateNowScreen(
    campaignId: String,
    title: String,
    imageUrl: String,
    raisedAmount: Double,
    goalAmount: Double,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var donorName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val database = FirebaseDatabase.getInstance().getReference("Campaigns")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donate Now", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Campaign Image ---
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                // --- Progress bar ---
                LinearProgressIndicator(
                    progress = (raisedAmount / goalAmount).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50)
                )

                Text(
                    text = "£${raisedAmount.roundToInt()} raised of £${goalAmount.roundToInt()} goal",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Donor name field ---
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                // --- Donation amount field ---
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            amount = input
                        }
                    },
                    label = { Text("Donation Amount (£)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- Donate Button ---
                Button(
                    onClick = {
                        val donationAmount = amount.toDoubleOrNull()
                        if (donorName.isBlank() || donationAmount == null || donationAmount <= 0) {
                            Toast.makeText(context, "Enter valid details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true

                        val donationRef = FirebaseDatabase.getInstance().getReference("Donations").push()
                        val donationInfo = DonationInfo(
                            donorName = donorName,
                            amount = donationAmount,
                            campaignId = campaignId
                        )

                        // Update raised amount
                        database.child(campaignId).get().addOnSuccessListener { snapshot ->
                            val campaign = snapshot.getValue(Campaign::class.java)
                            if (campaign != null) {
                                val newRaised = campaign.raised_amount + donationAmount
                                database.child(campaignId).child("raised_amount").setValue(newRaised)
                                donationRef.setValue(donationInfo)

                                Toast.makeText(context, "Donation successful 💝", Toast.LENGTH_SHORT).show()
                                Log.d("DonationApp", "✅ Donation recorded: $donationInfo")
                                onBack()
                            } else {
                                Toast.makeText(context, "Campaign not found", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        }.addOnFailureListener { e ->
                            Log.e("DonationApp", "❌ Error updating donation: ${e.message}")
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Donate Now", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
