package com.example.android.geofence ;

public class Audio implements IGeofenceVisitable{

  private final String id ;
  private final String track ;
  private final boolean loop ;

  public Audio(String id, String track)
  {    
      this.id = id ;
      this.track = track ;
      this.loop = false ;

  }

  public Audio(String id, String track, boolean loop)
  {
      this.id = id ;
      this.track = track ;
      this.loop = loop ;

  }

  public String getId(){ 
      return this.id ; 
  } 
 
  public String getTrack(){
     return this.track ;

  }

  public boolean getLoop(){
     return this.loop ;
  }

  @Override
  public void accept(IGeofenceVisitor geofenceVisitor)
  {
    geofenceVisitor.visit(this) ;

  }

  @Override
  public String toString()
  {
    return "audio id:" + this.id + " track:" + this.track + " loop:" + loop ;

  }

}
