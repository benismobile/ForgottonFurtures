package com.example.android.geofence ;

public class Audio implements IGeofenceVisitable{

  private final int id ;
  private final String track ;
  private final OnComplete onComplete ;

  public Audio(int id, String track)
  {    
      this.id = id ;
      this.track = track ;
      this.onComplete = null ;

  }

  public Audio(int id, String track, OnComplete onComplete)
  {
      this.id = id ;
      this.track = track ;
      this.onComplete = onComplete ;

  }

  public int getId(){ 
      return this.id ; 
  } 
 
  public String getTrack(){
     return this.track ;

  }

  public OnComplete getOnComplete(){
     return this.onComplete ;
  }

  @Override
  public void accept(IGeofenceVisitor geofenceVisitor)
  {
    geofenceVisitor.visit(this) ;

  }

  @Override
  public String toString()
  {
    return "audio id:" + this.id + " track:" + this.track + " onComplete:" + onComplete ;

  }

}
