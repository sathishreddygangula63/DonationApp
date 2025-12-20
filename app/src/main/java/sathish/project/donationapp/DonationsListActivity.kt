package sathish.project.donationapp

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.FlowRow

class DonationsListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonationsHistoryScreen(onBack = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationsHistoryScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Fund Donations", "Pickup Requests")

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Contributions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> FundDonationsListScreen(context)
                1 -> PickupRequestsListScreen(context)
            }
        }
    }
}

@Composable
fun FundDonationsListScreen(context : Context) {

    val userEmail = UserPrefs.getEmail(context).replace(".", "_")

    val database = FirebaseDatabase.getInstance().getReference("Donations").child(userEmail)
    var donations by remember { mutableStateOf<List<DonationInfo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DonationInfo>()
                snapshot.children.forEach {
                    it.getValue(DonationInfo::class.java)?.let(list::add)
                }
                donations = list.sortedByDescending { it.timestamp }
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                loading = false
            }
        })
    }

    when {
        loading -> LoadingView()
        donations.isEmpty() -> EmptyView("No fund donations yet 💸")
        else -> LazyColumn {
            items(donations) {
//                DonationCard(it)

                FundDonationReceiptCard(it)

            }
        }
    }
}


@Composable
fun DonationCard(donation: DonationInfo) {
    val date = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(donation.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                donation.campaignTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "₹${donation.amount}",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(4.dp))

            Text("Donor: ${donation.donorName}", color = Color.Gray)
            Text(date, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun PickupRequestsListScreen(context : Context) {

    val userEmail = UserPrefs.getEmail(context).replace(".", "_")


    val database = FirebaseDatabase.getInstance().getReference("PickupRequests").child(userEmail)
    var requests by remember { mutableStateOf<List<PickupRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<PickupRequest>()
                snapshot.children.forEach {
                    it.getValue(PickupRequest::class.java)?.let(list::add)
                }
                requests = list.sortedByDescending { it.createdAt }
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                loading = false
            }
        })
    }

    when {
        loading -> LoadingView()
        requests.isEmpty() -> EmptyView("No pickup requests yet 📦")
        else -> LazyColumn {
            items(requests) { PickupRequestCard(it) }
        }
    }
}

@Composable
fun PickupRequestCard(request: PickupRequest) {
    val date = remember(request.createdAt) {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(request.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {

            // 🔹 IMAGE + STATUS
            Box {
                AsyncImage(
                    model = request.imageUrl,
                    contentDescription = request.campaignTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                // Status Chip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    StatusChip(request.status)
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {

                // 🔹 Campaign title
                Text(
                    text = request.campaignTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(6.dp))

                // 🔹 Items donated
                Text(
                    text = "Items Donated",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(4.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    request.items.forEach { item ->
                        AssistChip(
                            onClick = {},
                            label = { Text(item, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 🔹 Address
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = request.address,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(Modifier.height(6.dp))

                // 🔹 Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


@Composable
fun FundDonationReceiptCard(donation: DonationInfo) {
    val date = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(donation.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Donation Receipt", fontWeight = FontWeight.Bold)
                Text(
                    donation.status,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(Modifier.padding(vertical = 10.dp))

            ReceiptRow("Campaign", donation.campaignTitle)
            ReceiptRow("Donor", donation.donorName)
            ReceiptRow("Amount", "£${donation.amount}")
            ReceiptRow("Date", date)

            Divider(Modifier.padding(vertical = 10.dp))

            Text(
                "Receipt ID: ${donation.donationId}",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium)
    }
}



@Composable
fun StatusChip(status: String) {
    val (bg, text) = when (status.uppercase()) {
        "PENDING" -> Color(0xFFFFC107) to "Pending"
        "APPROVED" -> Color(0xFF2196F3) to "Approved"
        "COMPLETED" -> Color(0xFF4CAF50) to "Completed"
        "REJECTED" -> Color(0xFFF44336) to "Rejected"
        else -> Color.Gray to status
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}



@Composable
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyView(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = Color.Gray)
    }
}
