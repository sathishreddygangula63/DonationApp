package sathish.project.donationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.jvm.java


data class CampaignItem(
    val item_name: String = "",
    val quantity: String = ""
)


enum class CampaignType {
    fund,
    item
}


data class Campaign(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val image: String = "",
    val location: String = "",
    val category: String = "",
    val start_date: String = "",
    val end_date: String = "",
    val status: String = "",

    // 🔹 ENUM (matches Firebase exactly)
    val campaign_type: CampaignType = CampaignType.fund,

    // 🔹 Fund specific
    val goal_amount: Double = 0.0,
    val raised_amount: Double = 0.0,

    // 🔹 Item specific
    val drop_location: String = "",
    val items_required: List<ItemRequirement> = emptyList(),
    val items_collected: List<ItemRequirement> = emptyList()
)



data class ItemRequirement(
    val item_name: String = "",
    val quantity: String = ""
)



class LiveCampaignActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveCampaignScreen(onBack = { finish() })
        }
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
    val categories = listOf(
        "All",
        "Education",
        "Health & Medical",
        "Environment",
        "Disaster Relief",
        "Women Empowerment",
        "Basic Needs",
        "Hunger & Nutrition",
        "Water & Sanitation"
    )

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
                            onDonateMoney = {
                                context.startActivity(
                                    Intent(context, DonateNowActivity::class.java)
                                        .putExtra("campaign_id", campaign.id)
                                )
                            },
                            onPickupRequest = {
                                context.startActivity(
                                    Intent(context, PickupRequestActivity::class.java)
                                        .putExtra("campaign_id", campaign.id)
                                        .putExtra("campaign_title", campaign.title)
                                )
                            }
                        )

                    }

                    if (filteredCampaigns.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
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
fun CampaignCard(
    campaign: Campaign,
    onDonateMoney: () -> Unit,
    onPickupRequest: () -> Unit
) {
    val isFundCampaign = campaign.campaign_type == CampaignType.fund

    val progress = remember(campaign.raised_amount, campaign.goal_amount) {
        if (campaign.goal_amount > 0) {
            (campaign.raised_amount / campaign.goal_amount)
                .toFloat()
                .coerceIn(0f, 1f)
        } else 0f
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.background(Color.White)) {

            // 🔹 IMAGE WITH CAMPAIGN TYPE BADGE
            Box {
                AsyncImage(
                    model = campaign.image,
                    contentDescription = campaign.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                // 🔹 Campaign Type Badge (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            color = if (isFundCampaign)
                                Color(0xFFFF9800)
                            else
                                Color(0xFF4CAF50),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isFundCampaign) "FUND" else "ITEM",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = campaign.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = "📍 ${campaign.location}",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = campaign.summary,
                modifier = Modifier.padding(12.dp),
                color = Color.DarkGray
            )

            // 🔹 FUND CAMPAIGN UI
            if (isFundCampaign) {
                LinearProgressIndicator(
                    progress = progress,
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
            }

            // 🔹 ITEM CAMPAIGN UI
            if (!isFundCampaign && campaign.items_required.isNotEmpty()) {
                Text(
                    text = "Items Needed:",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                campaign.items_required.take(3).forEach { item ->
                    Text(
                        text = "• ${item.item_name} (${item.quantity})",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.DarkGray
                    )
                }

            }

            Spacer(Modifier.height(12.dp))

            // 🔹 DONATE BUTTON
            Button(
                onClick = {
                    if (isFundCampaign) onDonateMoney() else onPickupRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFundCampaign)
                        Color(0xFFFF9800)
                    else
                        Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = if (isFundCampaign) "Donate Funds" else "Donate Items",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



@Composable
fun CampaignCardOld(campaign: Campaign, onDonateClick: () -> Unit) {
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
