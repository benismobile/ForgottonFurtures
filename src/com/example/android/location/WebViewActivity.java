package com.example.android.location ;

import android.app.Activity ;
import android.os.Bundle ;
import android.content.Intent ;
import android.widget.EditText ;
import android.view.View;
import android.webkit.WebView ;
import android.annotation.SuppressLint ;
import android.webkit.WebSettings ;
import android.os.* ;
import android.support.v7.app.ActionBarActivity ;
import android.support.v7.widget.SearchView;
import android.view.Menu ;
import android.util.Log ;
import android.view.MenuItem ;
import android.view.MenuInflater ;
import android.widget.Toast ;
import android.text.TextUtils ;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import android.location.Location;
import android.content.SharedPreferences;
import android.content.Context;
import android.media.SoundPool;
import android.media.AudioManager;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.MediaPlayer ;
import android.media.MediaPlayer.OnPreparedListener;

import android.text.format.DateUtils;
import com.example.android.geofence.GeofenceUtils.REMOVE_TYPE;
import com.example.android.geofence.GeofenceUtils.REQUEST_TYPE;
import com.example.android.geofence.GeofenceRemover;
import com.example.android.geofence.GeofenceRequester;
import com.example.android.geofence.SimpleGeofenceStore;
import com.example.android.geofence.SimpleGeofence;
import com.example.android.geofence.GeofenceUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap ;
import java.util.HashSet ;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time ;
import com.example.android.geofence.GeofenceDialogFragment ;
import android.app.FragmentManager ;


public class WebViewActivity extends ActionBarActivity
implements 
   LocationListener,
   GooglePlayServicesClient.ConnectionCallbacks,
   GooglePlayServicesClient.OnConnectionFailedListener,
   MediaPlayer.OnPreparedListener 


{

   private LocationClient mLocationClient;
   private LocationRequest mLocationRequest;
   boolean mUpdatesRequested = false;
   WebView webview ;
   SharedPreferences mPrefs;  // storage for location update status
   SharedPreferences.Editor mEditor;
 
   // Persistent storage for geofences
   private SimpleGeofenceStore mGeofencePrefs;

   private static final long GEOFENCE_EXPIRATION_IN_HOURS = 1;
   private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * DateUtils.HOUR_IN_MILLIS;

   // Store the current request
   private REQUEST_TYPE mRequestType;

   // Store the current type of removal
   private REMOVE_TYPE mRemoveType;


   // Store a list of geofences to add
   List<Geofence> mCurrentGeofences;

   // Add geofences handler
   private GeofenceRequester mGeofenceRequester;
   // Remove geofences handler
   private GeofenceRemover mGeofenceRemover;

   private DecimalFormat mLatLngFormat;
   private DecimalFormat mRadiusFormat;

   private SimpleGeofence mGeofence1;
   private SimpleGeofence mGeofence2;
   
   private GeofenceSampleReceiver mBroadcastReceiver;

   // An intent filter for the broadcast receiver
   private IntentFilter mIntentFilter;

   // Store the list of geofences to remove
   private List<String> mGeofenceIdsToRemove;
  
   private SoundPool mSoundPool ;
   private HashMap mSoundMap ;
   private HashSet mSoundLoadedMap ;
   private MediaPlayer mPlayer ;  
   private MediaPlayer mPlayer2 ;  

   @SuppressLint("NewApi")
   @Override
   protected void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

     // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }



     webview = new WebView(this);
     setContentView(webview);
     WebSettings webSettings = webview.getSettings();
     webSettings.setJavaScriptEnabled(true);
     webSettings.setAllowContentAccess(true) ;
     webSettings.setBlockNetworkImage (false) ;
     webSettings.setUseWideViewPort(true);
     webSettings.setLoadsImagesAutomatically (true) ;
    // WHATEVER YOU DO: DONT USE setAllowFileAccess* ON GINGERBREAB - Causes nasty crach
    // BUT needed to get the local gpx loading to work
    // webSettings.setAllowFileAccess(true) ;
    // webSettings.setAllowFileAccessFromFileURLs(true) ;
      webview.addJavascriptInterface(new WebAppInterface(this),"Android");
     webview.loadUrl("file:///android_asset/html/threshold.html");


    mLocationClient = new LocationClient(this, this, this);
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    // Open Shared Preferences
    mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

   // Get an editor
   mEditor = mPrefs.edit();
   mUpdatesRequested = true ;
   mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
   mEditor.commit();


   // Create a new broadcast receiver to receive updates from the listeners and service
     mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);
        // Action for broadcast Intents containing ENTER and EXIT TRANSITIONS
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // Instantiate a new geofence storage area
        mGeofencePrefs = new SimpleGeofenceStore(this);

        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);
   
        mSoundMap = new HashMap<Integer, Integer>();
        mSoundLoadedMap = new HashSet<Integer>();
        
	mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                    int status) {
	       Log.d(GeofenceUtils.APPTAG, "onLoadCompleteListener sampleId " + sampleId) ;
               mSoundLoadedMap.add(new Integer(sampleId)) ;
            }
        });
       
      
        mSoundMap.put(1, mSoundPool.load(this, R.raw.radio, 1));
        mSoundMap.put(2, mSoundPool.load(this, R.raw.radio2, 1));
        mSoundMap.put(3, mSoundPool.load(this, R.raw.radio5, 1));
        mSoundMap.put(4, mSoundPool.load(this, R.raw.radio6, 1));

	mPlayer =  MediaPlayer.create(this, R.raw.factory) ;
	mPlayer2 = MediaPlayer.create(this, R.raw.sleepaway) ;
      

   } // ends onCreate

   @Override
   public void onPrepared(MediaPlayer player)
   {
	player.start() ;

   }

   @Override
   public void onStop() {

     // If the client is connected
     if (mLocationClient.isConnected())
     {
        stopPeriodicUpdates();
     }

     // After disconnect() is called, the client is considered "dead".
     mLocationClient.disconnect();
     mPlayer.release() ;
     mPlayer = null ;
     super.onStop();
     mPlayer2.release() ;
     mPlayer2 = null ;
   } // end onStop()



    @Override
    public void onPause() {

        super.onPause();
        // Save the current setting for updates
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();

        mGeofencePrefs.setGeofence("1", mGeofence1);
        mGeofencePrefs.setGeofence("2", mGeofence2);

// TODO unregister broadcast receiver?
    }


    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onConnected() callback
         */
        mLocationClient.connect();
        
   }

   @Override
   public void onResume()
   {

      super.onResume();

      // If the app already has a setting for getting location updates, get it
      if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) 
      {
         mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
      }
      // Otherwise, turn off location updates until requested
      else 
      {
         mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
         mEditor.commit();
      }


      LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
      Time now = new Time() ;
      now.setToNow() ;
      long nowMillis = now.toMillis(false) ;
      mGeofence1 = mGeofencePrefs.getGeofence("1");
      mGeofence2 = mGeofencePrefs.getGeofence("2");

      // the list of current geofences is not empty
	if(mCurrentGeofences != null && mCurrentGeofences.size() > 0 && mGeofence1 != null && mGeofence2 != null)
        {
            // check if any geofences stored in SharedPrefs have expired - if so remove ALL geofences 
	    // from SharedPrefs - they will all get created again in onConnected callback method
	   
	   Log.d(GeofenceUtils.APPTAG, "onResume: geofence list not empty mGeofence1  expiretime: " + mGeofence1.getExpirationTime() + " nowMillis:" + nowMillis + " expired: " + (mGeofence1.getExpirationTime() < nowMillis)) ;

	   if(mGeofence1.getExpirationTime() < nowMillis || mGeofence2.getExpirationTime() < nowMillis)
	   {
	     Log.d(GeofenceUtils.APPTAG, "Getting rid of expired geofences from prefs") ;
	     mGeofencePrefs.clearGeofence("1") ;
	     mGeofencePrefs.clearGeofence("2") ;
	     mCurrentGeofences.clear() ;
	   }
	     

        }
	// the list of current geofences is empty but we found some in shared prefs
	if(mCurrentGeofences != null && mCurrentGeofences.size() == 0 && mGeofence1 != null && mGeofence2 != null)
	{
	   Log.d(GeofenceUtils.APPTAG, "onResume: geofence list empty mGeofence1  expiretime: " + mGeofence1.getExpirationTime() + " nowMillis:" + nowMillis + " expired: " + (mGeofence1.getExpirationTime() < nowMillis)) ;
	   
	   // check expiration time 
	   if(mGeofence1.getExpirationTime() < nowMillis || mGeofence2.getExpirationTime() < nowMillis)
	   {
	      mGeofencePrefs.clearGeofence("1") ;
	      mGeofencePrefs.clearGeofence("2") ;
	   }
	   else
	   {
              Log.d(GeofenceUtils.APPTAG, "Existing geofences not expired but list empty so add gfs: ");
              mCurrentGeofences.add(mGeofence1.toGeofence());
              mCurrentGeofences.add(mGeofence2.toGeofence());
	   }
        }
}




  public void playSound(int sound, float fSpeed, int repeat) 
  {
  
              Log.d(GeofenceUtils.APPTAG, "playSound:" + sound);
      AudioManager mgr = (AudioManager)getSystemService(AUDIO_SERVICE);
      float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
      float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      float volume = streamVolumeCurrent / streamVolumeMax; 
      
      Log.d(GeofenceUtils.APPTAG, "playSound: " + sound + " volume: " + volume);
      mSoundPool.play(sound, volume, volume, 1, repeat, fSpeed);
  }



@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.webview_activity_actions, menu);
    return super.onCreateOptionsMenu(menu);
}


@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle presses on the action bar items
    switch (item.getItemId()) {
        case R.id.get_location:
            getLocation();
            return true;
	case R.id.framemarkers:
	    framemarkers();
        default:
            return super.onOptionsItemSelected(item);
    }
}




public void getLocation()
{

// Toast.makeText(this,"Android.getLocation called",Toast.LENGTH_SHORT).show();
webview.loadUrl("javascript:getLocation();");

}

public void framemarkers()
{

 Toast.makeText(this,"Framemarkers called",Toast.LENGTH_SHORT).show();
  Intent intent = new Intent(this, com.example.android.framemarkers.FrameMarkers.class);

  startActivity(intent);

}

  private void startUpdates() {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

  private void stopUpdates() {
        mUpdatesRequested = false;

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }




    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
          }

      private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
      }


    @Override
    public void onLocationChanged(Location location) {

 // Toast.makeText(this,"Android.onLocationChanged",Toast.LENGTH_SHORT).show();



	 // Display the current location in the UI
       String latlon = LocationUtils.getLatLngJSON(this, location);



     // call javascript method to update location
     webview.loadUrl("javascript:onLocationUpdateP('" + latlon +"');");
     // TODO change volume of stream according to location
       //  mSoundPool.setVolume(1, 0.2f, 0.2f) ;

      if(!mPlayer.isPlaying())
      {
	mPlayer.start() ;
      }
      else
      {
        if(mPlayer.getCurrentPosition() > 11000)
	{
		if(mSoundLoadedMap.contains(new Integer(1)))
		{
           	  playSound(1,1,3) ;
	   	  mSoundLoadedMap.remove(new Integer(1)) ;
                }
           
         }	
	  
      }

      
      if(!mPlayer2.isPlaying())
      {
        mPlayer2.start() ;
      }

  /*
        
        if(mSoundLoadedMap.contains(new Integer(1)))
	{
           playSound(1,1,3) ;
	   mSoundLoadedMap.remove(new Integer(1)) ;

           // mSoundPool.setVolume(1, 0.5f, 0.5f) ;
	}
	
	if(mSoundLoadedMap.contains(new Integer(2)))
	{
           playSound(2,1,5) ;
	   mSoundLoadedMap.remove(new Integer(2)) ;
          //  mSoundPool.setVolume(1, 0.5f, 0.5f) ;
	}


        if(mSoundLoadedMap.contains(new Integer(3)))
	{
           playSound(3,1) ;
           mSoundPool.setVolume(1, 0.5f, 0.5f) ;
	}
        if(mSoundLoadedMap.contains(new Integer(4)))
	{
           playSound(4,1) ;
           mSoundPool.setVolume(2, 0.5f, 0.5f) ;
        }

	*/
 }


private boolean servicesConnected() {

// Check that Google Play services is available
        int resultCode =              GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode)
	   {

            // Continue
            return true;
	   }


Toast.makeText(this, "Google Play Services NotAvailable",  Toast.LENGTH_SHORT).show();

	return false;

}

    @Override
    public void onConnected(Bundle dataBundle)
    {
       Toast.makeText(this, "WebViewActivity On Connected: " + mUpdatesRequested,Toast.LENGTH_SHORT).show();
       if(mUpdatesRequested)
       {
          startPeriodicUpdates() ;
       }

      Log.d(GeofenceUtils.APPTAG, "WebViewActivity:onConnected " + mCurrentGeofences ) ;
      if(mCurrentGeofences != null && mCurrentGeofences.size() == 0)
      {
      	mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
	Log.d(GeofenceUtils.APPTAG, "WebViewActivity:onConnected: adding gfs") ;
       Toast.makeText(this, "WebViewActivity OnConnected: adding gfs"  ,Toast.LENGTH_SHORT).show();
      	mGeofence1 = new SimpleGeofence(
            "1",
            55.9494252, // Latitude
             -3.1197155,  // Longitude
            75, // radius
            // expiration time
            120 * 1000,
            // Only detect entry transitions
            Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

          mGeofence2 = new SimpleGeofence(
            "2",
            55.9494253, // Latitude
            -3.1197156, // Longitude
            75, // radius
            // Set the expiration time
            120 * 1000,
            // Detect both entry and exit transitions
            Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
 
            
            Log.d(GeofenceUtils.APPTAG, "adding 2 geofences to prefs and geofence list") ;
            mGeofencePrefs.setGeofence("1", mGeofence1);
            mGeofencePrefs.setGeofence("2", mGeofence2);
       	    mCurrentGeofences.add(mGeofence1.toGeofence());
       	    mCurrentGeofences.add(mGeofence2.toGeofence());
	
           // Start the request. Fail if there's already a request in progress
           try {
               // Try to add geofences
               mGeofenceRequester.addGeofences(mCurrentGeofences);
	       Log.d(GeofenceUtils.APPTAG, "requesting adding of geofence list items") ;
               } catch (UnsupportedOperationException e) {
                 // Notify user that previous request hasn't finished.
                 Toast.makeText(this, R.string.add_geofences_already_requested_error,
                        Toast.LENGTH_LONG).show();
                 }
      
       }
    }

    @Override
    public void onDisconnected()
    {


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

     Toast.makeText(this, "Connection Failed.",Toast.LENGTH_SHORT).show();


    }






  /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of geofences
            } else if (
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    ||
                    TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

            // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {


		// Toast.makeText(context, "GeofenceSampleReceiver:handleGeofenceStatus:" + intent, Toast.LENGTH_SHORT).show() ;
               Log.d(GeofenceUtils.APPTAG, "GeofenceSampleReceiver:handleGeofenceStatus: " + intent );

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */

               Log.d(GeofenceUtils.APPTAG, "GeofenceSampleReceiver:handleGeofenceTransition: " + intent.getStringExtra("TRANSITION_TYPE") );
         // Log.d(GeofenceUtils.APPTAG, "GeofenceSampleReceiver:handleGeofenceTransition: " + intent.getStringArrayExtra("GEOFENCE_IDS") );
	      //  int sound = mSoundMap.get(1).intValue() ;
	      int sound = 1 ;
	      String transitionType = intent.getStringExtra("TRANSITION_TYPE") ;
	      String[] triggerGeofenceIds = intent.getStringArrayExtra("GEOFENCE_IDS") ;

	      if("Entered".equals(transitionType))
	      {

	        for(int i = 1 ; i <= triggerGeofenceIds.length ; i++)
		{
		      if(mSoundLoadedMap.contains(new Integer(i)))
		      {
	      // 	 playSound(i,1) ;
               	      }
                } 


        //      GeofenceDialogFragment alert = new GeofenceDialogFragment();
	//      alert.show(getFragmentManager(), "GeofenceEventFragment") ;
	      }
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {
            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
}
