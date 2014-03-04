package com.example.android.geofence ;



public class GeofenceAudio implements IGeofenceVisitable
{

   private int id ;
   private double latitude ;
   private double longitude ;
   private float radius ;
   private long  duration ;
   private int transitions ;
   private String track ;  
   private OnComplete onComplete ;

   private GeofenceAudio(int id)
   {
      this.id = id ;
   }

   public int getId()
   {
      return this.id ;
   }

   public double getLatitude()
   {
      return this.latitude ;
   }

   public double getLongitude()
   {
      return this.longitude ;
   }

   public float getRadius()
   {
      return this.radius ;
   }

   public long getDuration()
   {
      return this.duration ;
   }

   public int getTransitions()
   {
      return this.transitions ;
   }

   public String getTrack()
   {
      return this.track ;
   }

   public OnComplete getOnComplete()
   {
      return this.onComplete ;
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

   @Override
   public void accept(IGeofenceVisitor gfVisitor)
   {
      gfVisitor.visit(this) ;
   }

   @Override 
   public String toString()
   {
      String requiredFields =  "id:" + this.id  + " latitude:" + this.latitude + " longitude:" + longitude + " radius:" + radius + " duration: " + duration + " transitions: " + transitions + " track: " + track ;

     if(this.onComplete != null)
     {
        return requiredFields + onComplete ;
     }
     else
     {
        return requiredFields ;
     }

   }

   public static class Builder
   {
     
     private GeofenceAudio geofenceAudio ;

     public Builder(int id)
     {
       this.geofenceAudio = new GeofenceAudio(id) ;
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
