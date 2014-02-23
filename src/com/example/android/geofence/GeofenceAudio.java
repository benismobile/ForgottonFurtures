package com.example.android.geofence ;



public class GeofenceAudio
{

   private String id ;
   private double latitude ;
   private double longitude ;
   private float radius ;
   private long  duration ;
   private int transitions ;
   private String track ;  
   private OnComplete onComplete ;

   private GeofenceAudio()
   {
   }

   public boolean hasOnComplete()
   {
      if(this.onComplete != null ) 
      { 
         return true ;
      }
      else
      {
         return false ;
      }	

   }

   public class Builder
   {
     
     private GeofenceAudio geofenceAudio ;

     public Builder()
     {
       this.geofenceAudio = new GeofenceAudio() ;
       //TODO set defaults
     }
    

     public GeofenceAudio build()
     {
        // TODO validate build parameters
     
	return geofenceAudio ;
     }

     public GeofenceAudio.Builder setCircularRegion(double latitude, double longitude, float radius)
     {
        this.geofenceAudio.latitude = latitude ;
	this.geofenceAudio.longitude = longitude ;
	this.geofenceAudio.radius = radius ;
	return this ;
     }

     public GeofenceAudio.Builder setExpirationDuration(long durationMillis)
     {
        this.geofenceAudio.duration = durationMillis ;
        return this ;
     }

     public GeofenceAudio.Builder setTransitionTypes(int transitions)
     {

        this.geofenceAudio.transitions = transitions ;
	return this ;
     }

     public GeofenceAudio.Builder setTrack(String track)
     {
        this.geofenceAudio.track = track ;
	return this ;

     }
  
     public GeofenceAudio.Builder setOnComplete(OnComplete onComplete)
     {
        this.geofenceAudio.onComplete = onComplete ;
	return this;

     }

   }

}
