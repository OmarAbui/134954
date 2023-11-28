package com.example.firebasecourse.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.firebasecourse.R
import com.example.firebasecourse.other.Constants
import com.example.firebasecourse.ui.RunningWorkout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) :FusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideRunningWorkoutActivityPendingIntent(
        @ApplicationContext app:Context
    )= PendingIntent.getActivity(
        app,
        0,
        Intent(app, RunningWorkout::class.java).also{
            it.action= Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app:Context,
        pendingIntent: PendingIntent
    )= NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Fitness App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}