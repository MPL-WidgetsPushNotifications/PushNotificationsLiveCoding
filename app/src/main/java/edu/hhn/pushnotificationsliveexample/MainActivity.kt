package edu.hhn.pushnotificationsliveexample

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.hhn.pushnotificationsliveexample.ui.theme.PushNotificationsLiveExampleTheme

private const val CHANNEL_ID = "channel_id_example_preparation"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PushNotificationsLiveExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Buttons(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        ::sendNotification,
                        applicationContext
                    )
                }
            }
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test notification"
            val desc = "This is a test notification for Mobility Project Lab"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    public fun sendNotification(builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            // Aktuelle Zeit als ID, damit die Nachrichten sich nicht gegenseitig ersetzen
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    sendNotification: (builder: NotificationCompat.Builder) -> Unit,
    context: Context
) {
    var isNotificationPermissionGranted = false

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            isNotificationPermissionGranted = isGranted
        })

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Button(onClick = {
            if (!isNotificationPermissionGranted) {
                permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }) {
            Text(text = "Request permissions")
        }

        Button(onClick = {
            sendNotification(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hhn_logo_small)
                    .setContentTitle("Example title")
                    .setContentText("This is a message")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            )
        }) {
            Text(text = "Notification with text")
        }

        Button(onClick = {
            val iconBitmapSmall =
                BitmapFactory.decodeResource(context.resources, R.drawable.hhn_logo_small)
            val iconBitmapLarge =
                BitmapFactory.decodeResource(context.resources, R.drawable.hhn_logo_large)

            sendNotification(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hhn_logo_small)
                    .setContentTitle("Example title")
                    .setContentText("This is the message content")
                    .setLargeIcon(iconBitmapLarge)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(iconBitmapSmall))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            )
        }) {
            Text("Notification with text and picture")
        }

        Button(onClick = {
            sendNotification(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hhn_logo_small)
                    .setContentTitle("Example title")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("This is the message content and it's sooooo loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            )
        }) {
            Text("Notification with big text")
        }

        Button(onClick = {
            sendNotification(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hhn_logo_small)
                    .setContentTitle("Example title")
                    .setContentText("This is the content text of an progress bar notification")
                    .setProgress(100, 70, false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            )
        }) {
            Text("Notification with Progressbar")
        }

        Button(onClick = {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://hs-heilbronn.de")
            )

            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            sendNotification(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hhn_logo_small)
                    .setContentTitle("Example title")
                    .setContentText("This notification navigates to an intent")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            )
        }) {
            Text("Notification to intent")
        }
    }
}
