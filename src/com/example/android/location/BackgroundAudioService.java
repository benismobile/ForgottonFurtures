package com.example.android.location ;

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
    
    public static final String ACTION_PLAY = "com.example.android.ACTION_PLAY" ;
    public static final String EXTRA_TRACK = "com.example.android.TRACK" ;
    private MediaPlayer mMediaPlayer = null;
    private NotificationManager mNM;
    private Notification notification ;
    private final IBinder mBinder = new LocalBinder();
    private String track ;


    @Override 
    public void onCreate()
    {
	Log.d(GeofenceUtils.APPTAG, "BackgroundAudioService:onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // track = intent.getStringExtra(EXTRA_TRACK) ; 
	// TODO switch on track // check Extra allow us to specify resource id directly?
        Log.d(GeofenceUtils.APPTAG, "BackgroundAudioService:onStartCommand " + intent) ;

        mMediaPlayer = MediaPlayer.create(this, R.raw.factory) ; 
	if (intent.getAction().equals(ACTION_PLAY)) {
	    mMediaPlayer.start() ;
        }
	
	showNotification();
	return START_NOT_STICKY;
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
        stopForeground(true) ; 
        mMediaPlayer.stop() ;
	mMediaPlayer.release() ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        
        Log.d(GeofenceUtils.APPTAG, "BackgroundAudioService:showNotification") ;	
	track = "Robot Factory" ;

         PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), com.example.android.location.WebViewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification();
        notification.tickerText = "Playing: " + track ;
        notification.icon = R.drawable.ic_action_play ;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "forgotton futures",
                "Playing: " + track, pi);
        startForeground(1571, notification);

    }


    public void play()
    {
        
        if(! mMediaPlayer.isPlaying())
	{
           mMediaPlayer = MediaPlayer.create(this, R.raw.factory) ; 
	   mMediaPlayer.start() ;
	}

    }

    public class LocalBinder extends Binder {
        BackgroundAudioService getService() 
	{
            return BackgroundAudioService.this;
        }
    }
   

}


