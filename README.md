# NR-mapdemo
A simple Android App using Google Maps and the New Relic Mobile SDK

## Overview
This demo app is simply intended for use as a code sample, or in live demonstration situtations to highlight 
the ease of implementation for Custom Attributes, Custom Events, and how to send these back to New Relic.

Currently the app consists of a single Activity, which loads a Map with a randomly placed Marker, somewhere in the world. 
Clicking anywhere else on the map will create a new, user-defined marker. 
Creating new markers or tapping any of the markers on the map will send the Title and Lat/Long co-ordinates back to New Relic as 'Mobile_Custom' events in NRDB.

You can query these events with the following NRQL query: `SELECT markerName,lat,lng FROM Mobile_Custom WHERE name = 'GeoLocation' SINCE 1 hour ago LIMIT 100`

## Requirements:

 - Android device or emulator running Android OS 8 (Oreo) or greater
 - Access to Google Play Services on this device
 - A Google Maps API key
 - A New Relic Account or Mobile Trial

