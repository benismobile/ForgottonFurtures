/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.geofence;

import com.google.android.gms.location.Geofence;
import android.text.format.Time ;
/**
 * A single Geofence object, defined by its center (latitude and longitude position) and radius.
 */
public class SimpleGeofence {
    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private boolean mLooping;
    private boolean mVaryVolume ;
    private int mTransitionType;
    private long mExpirationTime ;

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public SimpleGeofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
	    boolean looping,
	    boolean varyVolume,
            int transition) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        this.mId = geofenceId;

        // Center of the geofence
        this.mLatitude = latitude;
        this.mLongitude = longitude;

        // Radius of the geofence, in meters
        this.mRadius = radius;

        // Expiration time in milliseconds
        this.mExpirationDuration = expiration;

        // Transition type
        this.mTransitionType = transition;
        // derive expiration time
        Time now = new Time() ;
	now.setToNow() ;
        this.mExpirationTime = now.toMillis(false) + mExpirationDuration ;
        
	this.mLooping = looping ;
	this.mVaryVolume = varyVolume ;

    }
    // Instance field getters
    public SimpleGeofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
	    long expirationTime,
	    boolean looping,
	    boolean varyVolume,
            int transition) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        this.mId = geofenceId;

        // Center of the geofence
        this.mLatitude = latitude;
        this.mLongitude = longitude;

        // Radius of the geofence, in meters
        this.mRadius = radius;

        // Expiration time in milliseconds
        this.mExpirationDuration = expiration;
        // Transition type
        this.mTransitionType = transition;
	this.mLooping = looping ;
	this.mVaryVolume = varyVolume ;
	// use persisted expiration time
	this.mExpirationTime = expirationTime ;
   }  

    // Instance field getters
    /**
     * Get the geofence ID
     * @return A SimpleGeofence ID
     */
    public String getId() {
        return mId;
    }



    /**
    * Get the media looping flag
    *
    */
    public boolean getLooping()
    {
       return mLooping ;

    }

    /**
    * Get the media varyVolume flag
    *
    */
    public boolean getVaryVolume()
    {
       return mVaryVolume ;

    }

    /**
     * Get the geofence latitudea
     * @return A latitude value
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Get the geofence longitude
     * @return A longitude value
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Get the geofence radius
     * @return A radius value
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Get the geofence expiration duration
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return mExpirationDuration;
    }
    public long getExpirationTime() {
        return mExpirationTime ;

    }
    /**
     * Get the geofence transition type
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                       .setRequestId(getId())
                       .setTransitionTypes(mTransitionType)
                       .setCircularRegion(
                               getLatitude(),
                               getLongitude(),
                               getRadius())
                       .setExpirationDuration(mExpirationDuration)
                       .build();
    }
}
