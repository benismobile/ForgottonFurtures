package com.example.android.geofence ;

public interface IGeofenceVisitor{

   public void visit(Dialog dialog) ;
   public void visit(Audio audio) ;
   public void visit(GeofenceNullVisitable nullVisited) ;




}
