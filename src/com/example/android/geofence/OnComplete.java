package com.example.android.geofence ;

import java.lang.reflect.Method ;
import java.lang.reflect.InvocationTargetException ;

public class OnComplete implements IGeofenceVisitable{

// OnComplete acts as a wrapper for another visitable object such as Dialog or Audio that 
// follow on completion of the previous visited object (for example when previous Audio track completes)

  private final IGeofenceVisitable visitable ;

  public OnComplete(IGeofenceVisitable visitable)
  {
    this.visitable = visitable ;
  }
 
  public void accept(IGeofenceVisitor geofenceVisitor)
  {
    
//     geofenceVisitor.visit(this.visitable) ;

     try
     {
        // does the Visitor have a visit method that can accept the OnComplete visitable object?
        Method visitPolymorphic = geofenceVisitor.getClass().getMethod("visit", new Class[] { this.visitable.getClass() });

        if (visitPolymorphic == null) 
        {
           geofenceVisitor.visit(new GeofenceNullVisitable("No visit method for class " + this.visitable.getClass()) );
        } 
        else 
        {
           visitPolymorphic.invoke(geofenceVisitor, new Object[] {this.visitable});
        }
     }
     catch (NoSuchMethodException e)
     {
        geofenceVisitor.visit(new GeofenceNullVisitable(e.getMessage()) );
     }
     catch (InvocationTargetException e)
     {
        geofenceVisitor.visit(new GeofenceNullVisitable(e.getMessage()) );
     
     }   
     catch (IllegalAccessException e)
     {
        geofenceVisitor.visit(new GeofenceNullVisitable(e.getMessage()) );
		
     }


  }



}
