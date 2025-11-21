package sathish.project.donationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LiveCampaignActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveCampaignCardView()
        }
    }
}@Composable
fun LiveCampaignCardView()
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.SkyBlue))
                .padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_donation),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp)
                    .clickable {
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Live Campaign",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

        }


        CampaignCard(R.drawable.live_campaign,"3 Days Left","Campaign Name","A live campaign is a coordinated, time-bound effort to promote a product, service, or cause, with \"live\" referring to a few key concepts depending on the context.")
        CampaignCard(R.drawable.live_campaign,"3 Days Left","Campaign Name","A live campaign is a coordinated, time-bound effort to promote a product, service, or cause, with \"live\" referring to a few key concepts depending on the context.")


    }

}

@Composable
fun CampaignCard(campaignImg: Int, daysLeft: String, campaignName: String, campaignDes: String)
{
    Card(
        modifier = Modifier
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    )
    {
        Column(
            modifier = Modifier.fillMaxSize()

        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = painterResource(id = campaignImg),

                contentDescription = "Image",
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Text(
                text = "Category",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = daysLeft,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }


        Text(
            text = campaignName,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 6.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = campaignDes,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(50),     // makes the button curved / pill shaped
            colors = ButtonDefaults.buttonColors(
                containerColor =colorResource(id = R.color.yellow),  // background color
                contentColor = Color.White            // text color
            ),
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Donate Now",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(18.dp))


    }
}

@Preview(showBackground = true)
@Composable
fun  LiveCampaignCardPreview() {
    LiveCampaignCardView()
}

