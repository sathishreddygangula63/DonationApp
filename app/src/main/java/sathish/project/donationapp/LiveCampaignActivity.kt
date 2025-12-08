//package sathish.project.donationapp
//
//import android.R.attr.onClick
//import android.R.attr.text
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.google.firebase.database.*
//
//data class Campaign(
//    val id: String = "",
//    val title: String = "",
//    val summary: String = "",
//    val image: String = "",
//    val location: String = "",
//    val goal_amount: Double = 0.0,
//    val raised_amount: Double = 0.0,
//    val campaign_url: String = ""
//)
//
//class LiveCampaignActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent { LiveCampaignScreen(
//            onBack = { finish()}
//        ) }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LiveCampaignScreen(
//    onBack: () -> Unit
//) {
//    val context = LocalContext.current
//    var campaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // Firebase reference
//    val database = FirebaseDatabase.getInstance().getReference("Campaigns")
//
//    LaunchedEffect(Unit) {
//        Log.d("FirebaseCampaigns", "📡 Starting to fetch campaign data from Firebase...")
//
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("FirebaseCampaigns", "✅ DataSnapshot received: ${snapshot.childrenCount} campaigns found")
//
//                val list = mutableListOf<Campaign>()
//
//                for (child in snapshot.children) {
//                    Log.d("FirebaseCampaigns", "🔍 Processing campaign node: ${child.key}")
//
//                    val campaign = child.getValue(Campaign::class.java)
//
//                    if (campaign != null) {
//                        Log.d(
//                            "FirebaseCampaigns",
//                            """
//                        ➕ Added Campaign:
//                        ID: ${campaign.id}
//                        Title: ${campaign.title}
//                        Location: ${campaign.location}
//                        Goal: ${campaign.goal_amount}
//                        Raised: ${campaign.raised_amount}
//                        -------------------------------
//                        """.trimIndent()
//                        )
//                        list.add(campaign)
//                    } else {
//                        Log.w("FirebaseCampaigns", "⚠️ Skipped null or malformed campaign node: ${child.key}")
//                    }
//                }
//
//                campaigns = list
//                isLoading = false
//                Log.d("FirebaseCampaigns", "✅ Successfully loaded ${campaigns.size} campaigns into UI")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseCampaigns", "❌ Firebase error: ${error.message}")
//                isLoading = false
//            }
//        })
//    }
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Live Campaigns", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.Black
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(id = R.color.SkyBlue)
//                )
//            )
//
//        }
//    ) { padding ->
//        if (isLoading) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                campaigns.forEach { campaign ->
//                    CampaignCard(
//                        campaign = campaign,
//                        onDonateClick = {
//                            val intent = Intent(context, DonateNowActivity::class.java).apply {
//                                putExtra("campaign_id", campaign.id)
//                                putExtra("campaign_title", campaign.title)
//                                putExtra("campaign_image", campaign.image)
//                                putExtra("raised_amount", campaign.raised_amount)
//                                putExtra("goal_amount", campaign.goal_amount)
//                            }
//                            context.startActivity(intent)
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CampaignCard(campaign: Campaign, onDonateClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .padding(12.dp)
//            .fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(6.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(modifier = Modifier.background(Color.White)) {
//            AsyncImage(
//                model = campaign.image,
//                contentDescription = campaign.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = campaign.title,
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(horizontal = 12.dp)
//            )
//            Text(
//                text = "📍 ${campaign.location}",
//                style = MaterialTheme.typography.labelMedium,
//                color = Color.Gray,
//                modifier = Modifier.padding(horizontal = 12.dp)
//            )
//            Text(
//                text = campaign.summary,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.DarkGray,
//                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//            )
//
//            LinearProgressIndicator(
//                progress = (campaign.raised_amount / campaign.goal_amount).toFloat(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 12.dp)
//                    .height(6.dp),
//                color = Color(0xFF4CAF50)
//            )
//
//            Text(
//                text = "₹${campaign.raised_amount} raised of ₹${campaign.goal_amount}",
//                color = Color.Gray,
//                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//            )
//
//            Button(
//                onClick = onDonateClick,
//                shape = RoundedCornerShape(50),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp)
//                    .height(44.dp)
//            ) {
//                Text("Donate Now", fontWeight = FontWeight.Bold)
//            }
//        }
//    }
//}


package sathish.project.donationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class Campaign(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val image: String = "",
    val location: String = "",
    val goal_amount: Double = 0.0,
    val raised_amount: Double = 0.0,
    val campaign_url: String = "",
    val category: String = "",
    val end_date: String = ""
)

class LiveCampaignActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LiveCampaignScreen(onBack = { finish() }) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCampaignScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var campaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
    var filteredCampaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }

    val database = FirebaseDatabase.getInstance().getReference("Campaigns")

    // 🔹 Fetch Data from Firebase
    LaunchedEffect(Unit) {
        Log.d("FirebaseCampaigns", "📡 Fetching campaign data...")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Campaign>()
                for (child in snapshot.children) {
                    val campaign = child.getValue(Campaign::class.java)
                    if (campaign != null) list.add(campaign)
                }
                campaigns = list
                filteredCampaigns = list
                isLoading = false
                Log.d("FirebaseCampaigns", "✅ Loaded ${campaigns.size} campaigns")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseCampaigns", "❌ Firebase error: ${error.message}")
                isLoading = false
            }
        })
    }

    // 🔹 Category list for chips
    val categories = listOf("All", "Education", "Health & Medical", "Environment", "Disaster Relief", "Women Empowerment", "Basic Needs", "Hunger & Nutrition", "Water & Sanitation")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Live Campaigns",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF9FAFB))
            ) {
                // 🔹 Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = category
                                filteredCampaigns = if (category == "All") {
                                    campaigns
                                } else {
                                    campaigns.filter { it.category == category }
                                }
                            },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isSelected) Color(0xFF03A9F4) else Color.White,
                                labelColor = if (isSelected) Color.White else Color.Black,
                                selectedContainerColor = Color(0xFF03A9F4)
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                // 🔹 Campaign List
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp)
                ) {
                    filteredCampaigns.forEach { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            onDonateClick = {
                                val intent = Intent(context, DonateNowActivity::class.java).apply {
                                    putExtra("campaign_id", campaign.id)
                                    putExtra("campaign_title", campaign.title)
                                    putExtra("campaign_image", campaign.image)
                                    putExtra("raised_amount", campaign.raised_amount)
                                    putExtra("goal_amount", campaign.goal_amount)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }

                    if (filteredCampaigns.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No campaigns found for \"$selectedCategory\"", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampaignCard(campaign: Campaign, onDonateClick: () -> Unit) {
    val endDateFormatted = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(campaign.end_date)
        val display = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        display.format(date!!)
    } catch (e: Exception) {
        "Unknown"
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            AsyncImage(
                model = campaign.image,
                contentDescription = campaign.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = campaign.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = "📍 ${campaign.location}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = "🏷️ ${campaign.category}",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF0288D1),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            Text(
                text = campaign.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            LinearProgressIndicator(
                progress = (campaign.raised_amount / campaign.goal_amount).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(6.dp),
                color = Color(0xFF4CAF50)
            )

            Text(
                text = "£${campaign.raised_amount} raised of £${campaign.goal_amount}",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Text(
                text = "📅 Ends on: $endDateFormatted",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDonateClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(44.dp)
            ) {
                Text("Donate Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}
