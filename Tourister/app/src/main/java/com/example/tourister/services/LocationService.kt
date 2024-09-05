package com.example.tourister.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.tourister.MainActivity
import com.example.tourister.R
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private val firestore = FirebaseFirestore.getInstance()
    private val CHANNEL_ID = "LocationServiceChannel"
    private val NEARBY_THRESHOLD_METERS = 2000 // 2 km

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission", "ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("App is tracking your location")
            .setSmallIcon(R.drawable.placeholder)
            .build()

        startForeground(1, notification)

        startLocationUpdates()
        listenForAttractions()

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 sekundi
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(5000) // 5 sekundi
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    currentLocation = location // azuriranje trenutne lokacije
                    sendLocationToServer(location)
                }
            }
        }

        // Provera dozvola
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // dozvole nisu odobrene, ne moze da se pokrene azuriranja lokacije
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    //ovde se azurira lokacija korisnika u bazi
    private fun sendLocationToServer(location: Location) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
            userRef.update("lat", location.latitude)
            userRef.update("lng", location.longitude)
        }
    }

    private fun listenForAttractions() {
        firestore.collection("attractions")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { doc ->
                    if (isNearbyAttraction(doc)) {
                        Log.d("HELPER", "Nearby attraction found: ${doc.getString("name")}")
                        showNotification(doc)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("HELPER", "Firestore error: ${e.message}")
            }
    }


    private fun isNearbyAttraction(doc: DocumentSnapshot): Boolean {
        val attractionLatitude = doc.getDouble("latitude") ?: return false
        val attractionLongitude = doc.getDouble("longitude") ?: return false

        // racunanje distance izmedju korisnika i atrakcije
        val distance = calculateDistance(
            currentLocation.latitude,
            currentLocation.longitude,
            attractionLatitude,
            attractionLongitude
        )

        Log.d("HELPER","Distance to attraction: $distance meters")

        // vraca true ukoliko je atrakcija unutar unesenog radijusa
        return distance <= NEARBY_THRESHOLD_METERS
    }


    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371e3 // radijus zemlje
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // vraca distancu u metrima
    }

    private fun showNotification(doc: DocumentSnapshot) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val notificationId = doc.id.hashCode() // Generisanje jedinstvenog ID-a na osnovu ID-a dokumenta
        val pendingIntent = PendingIntent.getActivity(this, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE) //ulazim iz notifikacije u aplikaciju

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Attraction Nearby: ${doc.getString("name")}")
            .setContentText("Click to see details.")
            .setSmallIcon(R.drawable.bell)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager //salje notifikacije
        notificationManager.notify(notificationId, notification)

        Log.d("HELPER", "Showing notification for: ${doc.getString("name")}")
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
