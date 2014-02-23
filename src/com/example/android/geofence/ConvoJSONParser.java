package com.example.android.geofence ;

import org.json.JSONObject ;
import org.json.JSONArray ;
import org.json.JSONException ;
import java.text.ParseException ;
import com.google.android.gms.location.Geofence;


public class ConvoJSONParser
{
 
   private final String JSONString ;

   public ConvoJSONParser(String JSONString){
      this.JSONString = JSONString ;
      
   }

   public Convo parseConvo(JSONObject conversationObject) throws ParseException
   {
      if(this.JSONString == null ) nullInput("ConvoJSONParser not initialized with JSON String") ;
      if(conversationObject == null) nullInput("conversation object not intialised") ;

      try 
      {
            if(!conversationObject.has("name")) missingKey("name") ;
            if(!conversationObject.has("geofence_audio")) missingKey("geofence_audio") ;
	    
	    String name = conversationObject.getString("name") ;
            JSONObject geofenceAudioObj = conversationObject.getJSONObject("geofence_audio");
            GeofenceAudio geofenceAudio = parseGeofenceAudio(geofenceAudioObj) ;
	    return new Convo(name, geofenceAudio) ;

      
      }catch (JSONException e) 
       {
	   throw new ParseException("Unexpected Error parsing Conversation JSON object " + e.getMessage(), 0 ) ;
       }

   }

   private void missingKey(String key) throws ParseException
   {
     throw new ParseException("object must have key \"" + key + "\"", 0 ) ;
   }

   private void nullInput(String missingInput) throws ParseException
   {
     throw new ParseException("Missing input: " + missingInput, 0) ;
   }

   private GeofenceAudio parseGeofenceAudio(JSONObject geofenceAudioObject) throws ParseException
   {

    // process tag geofence_audio
/*       int id = geofenceAudioObject.getInt("id");
       double lat = geofenceAudioObject.getDouble("lat");
       double lon = geofenceAudioObject.getDouble("lon");
       float radius = (float)geofenceAudioObject.getDouble("radius");
       long duration = geofenceAudioObject.getLong("duration");
       JSONArray transitionsArray = geofenceAudioObject.getJSONArray("transitions") ;
       int transitions = 0;
       for(int j=0; j < transitionsArray.length() ; j++)
       {
          String transitionStr = transitionsArray.getString(j) ;
          int transition = 0 ;
	  if("ENTER".equals(transitionStr))
	  {
	     transition = Geofence.GEOFENCE_TRANSITION_ENTER ;
	  }
	  else if("EXIT".equals(transitionStr))
	  {							
	     transition = Geofence.GEOFENCE_TRANSITION_EXIT ;
          }				
	  transitions = transitions | transition ;													}
																		        String track = geofenceAudioObject.getString("track");												
																		       JSONObject onComplete = geofenceAudioObject.getJSONObject("on_complete") ;
  */
  return null ; 
   }


   private Dialog parseDialog(JSONObject dialogObj) throws ParseException
   {
      return null ;
   }

   private Audio parseAudio(JSONObject audioObj) throws ParseException
   {
      return null ;
   }

   private Option parseOption(JSONObject optionObj) throws ParseException
   {
      return null ;
   }



}
