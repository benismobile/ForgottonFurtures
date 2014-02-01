package com.example.android.audio ;

import android.app.Service ;
import android.media.MediaPlayer ;
import android.content.Intent ;
import android.app.PendingIntent ;
import android.util.Log ;
import android.os.Binder ;
import android.os.IBinder ;
import android.widget.Toast ;
import android.app.NotificationManager ;
import android.app.Notification ;
import android.util.Log ;

import com.example.android.location.R ;
import com.example.android.geofence.GeofenceUtils ;

import java.lang.CharSequence ;


public class BackgroundAudioService extends Service implements MediaPlayer.OnErrorListener {
    
    private static final String ACTION_PLAY = "com.example.android.ACTION_PLAY" ;
    private MediaPlayer mMediaPlayer = null;
    private NotificationManager mNM;
    private Notification notification ;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() 
    {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mMediaPlayer = MediaPlayer.create(this, R.raw.factory) ; 
        // Display a notification about us starting.  We put an icon in the status bar.
         showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
	if (intent.getAction().equals(ACTION_PLAY)) {
	    mMediaPlayer.start() ;
        }
	return START_STICKY;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        mp.reset() ;
	
	Log.e(GeofenceUtils.APPTAG, "Media in error state") ;
	return true;
    }

   
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        // mNM.cancel(NOTIFICATION);
        stopForeground(true) ; 
        // Tell the user we stopped.
        Toast.makeText(this, R.string.audio_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.audio_service_started);
        String track = "Robot Factory" ;

         PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), com.example.android.location.WebViewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification();
        notification.tickerText = text;
        notification.icon = R.drawable.ic_action_play ;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "Forgotton Futures Sample",
                "Playing: " + track, pi);
        startForeground(15, notification);

    }

    public class LocalBinder extends Binder {
        BackgroundAudioService getService() 
	{
            return BackgroundAudioService.this;
        }
    }
   

}


