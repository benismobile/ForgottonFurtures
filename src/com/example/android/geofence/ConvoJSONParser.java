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

   private boolean isValidGeofenceObject(JSONObject geofenceAudioObject)
   {
      return geofenceAudioObject.has("id") && 
             geofenceAudioObject.has("lat") &&
	     geofenceAudioObject.has("lon") &&
	     geofenceAudioObject.has("radius") &&
	     geofenceAudioObject.has("duration") &&
	     geofenceAudioObject.has("transitions") &&
	     geofenceAudioObject.has("track") ;

   }

   private GeofenceAudio parseGeofenceAudio(JSONObject geofenceAudioObject) throws ParseException
   {

       if(geofenceAudioObject == null) nullInput(" GeofenceAudioObject" ) ;
       if(!isValidGeofenceObject(geofenceAudioObject)) throw new ParseException("Invalid geofenceAudioObject: " + geofenceAudioObject, 1) ; 
       
       try
       {
          int id = geofenceAudioObject.getInt("id");
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
	     transitions = transitions | transition ;			
	  }
	
	  String track = geofenceAudioObject.getString("track");										

          GeofenceAudio.Builder gfBuilder = new GeofenceAudio.Builder(id) ;
	  gfBuilder.setCircularRegion(lat,lon,radius)
	  	   .setExpirationDuration(duration)
		   .setTransitionTypes(transitions)
		   .setTrack(track) ;
          

	  if(geofenceAudioObject.has("on_complete"))
	  {
	     JSONObject onComplete = geofenceAudioObject.getJSONObject("on_complete") ;
	     gfBuilder.setOnComplete(parseOnComplete(onComplete)) ;
	  }

	  return gfBuilder.build() ;

     }catch(JSONException e)
        {
	   throw new ParseException("Unexpected Error parsing geofence_audio JSON object " + e.getMessage(), 0 ) ;
        }
        
	

   }


   private OnComplete parseOnComplete(JSONObject onCompleteObj) throws ParseException, JSONException
   {
       if(onCompleteObj.has("dialog"))
       {
	 Dialog dialog = parseDialog(onCompleteObj.getJSONObject("dialog")) ;
	 return new OnComplete(dialog) ;
       }
       else if(onCompleteObj.has("audio"))
       {
          Audio audio = parseAudio(onCompleteObj.getJSONObject("audio")) ;
	  return new OnComplete(audio) ;

       }
       else
       {
          throw new ParseException("Invalid onComplete object: " + onCompleteObj, 2) ;
       }

   }

   private Dialog parseDialog(JSONObject dialogObj) throws ParseException, JSONException
   {
      if(!dialogObj.has("options")) throw new ParseException("Invalid dialog object: " + dialogObj, 1) ; 
      
         JSONArray optionsArray = dialogObj.getJSONArray("options") ;
         Option[] options = new Option[optionsArray.length()] ;

         for(int k = 0 ; k < optionsArray.length() ; k++ )
         {
            JSONObject optionObj = optionsArray.getJSONObject(k) ;
	    Option option = parseOption(optionObj) ;
	    options[k] = option ;
         }
      
      
      return new Dialog(options) ;
   }

   private Audio parseAudio(JSONObject audioObj) throws ParseException, JSONException
   {
        
        if(!audioObj.has("id") || ! audioObj.has("track")) throw new ParseException("Invalid audio object: " + audioObj, 1) ;

        int audioTrackId = audioObj.getInt("id") ;
        String audioTrack = audioObj.getString("track") ;
	if(audioObj.has("on_complete"))
	{
          JSONObject audioTrackOnComplete = audioObj.getJSONObject("on_complete") ;
	  OnComplete onAudioComplete = parseOnComplete(audioTrackOnComplete) ;
	  return new Audio(audioTrackId, audioTrack, onAudioComplete) ;
        }
	else
	{
            return new Audio(audioTrackId, audioTrack) ;
	}
   }

   private Option parseOption(JSONObject optionObj) throws ParseException, JSONException
   {
      if(!optionObj.has("option") || !optionObj.has("audio"))
      {
         throw new ParseException("Invalid Option object: " + optionObj, 1) ; 
      }

      String optionStr = optionObj.getString("option") ;
      JSONObject audioObj = optionObj.getJSONObject("audio") ;
      Audio audio = parseAudio(audioObj) ;
      return new Option(optionStr, audio) ;
   }



}
