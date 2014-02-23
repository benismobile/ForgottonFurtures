package com.example.android.geofence ;

public class Option{

   private final String option ;
   private final Audio audio ;

   public Option(String option, Audio audio)
   {
      this.option = option ;
      this.audio = audio ;

   }

   public String getOption(){

      return this.option ;
   }

   public Audio getAudio(){
   
      return this.audio ;
   }


   @Override
   public String toString()
   {
     return this.option + this.audio ;
   }

}
