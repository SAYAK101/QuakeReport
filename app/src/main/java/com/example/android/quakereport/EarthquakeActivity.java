/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.quakereport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.widget.ListView;
import android.content.Intent;
import android.widget.AdapterView;
import android.net.Uri;
import android.view.View;
import java.util.ArrayList;
import java.util.*;
import android.util.Log;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

public class EarthquakeActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();

     /** URL for earthquake data from the USGS dataset */
     private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=1&limit=10";

     /** Adapter for the list of earthquakes */
     private EarthquakeAdapter mAdapter;
    private TextView mEmptyStateTextView;
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);



       // ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        // Create a new {@link ArrayAdapter} of earthquakes
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, new Object(){}.getClass().getEnclosingMethod().getName() + " There: before executing");

            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        } else {
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText("No internet Connection");
        }
     }

     /**
  +     * {@link AsyncTask} to perform the network request on a background thread, and then
  +     * update the UI with the list of earthquakes in the response.
  +     *
  +     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
  +     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
  +     * progress updates, so the second generic is just Void.
  +     *
  +     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
  +     * The doInBackground() method runs on a background thread, so it can run long-running code
  +     * (like network activity), without interfering with the responsiveness of the app.
  +     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
  +     * UI thread, so it can use the produced data to update the UI.
      */



    // Get a reference to the LoaderManager, in order to interact with loaders.

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // TODO: Create a new loader for the given URL
        //Log.i(LOG_TAG, new Object(){}.getClass().getEnclosingMethod().getName() + " There: now");
        Log.i(LOG_TAG, new Object(){}.getClass().getEnclosingMethod().getName() + " There: now");

        /**
         * Then we can replace the body of onCreateLoader() method to read the user’s latest
         * preferences for the minimum magnitude, construct a proper URI with their preference,
         * and then create a new Loader for that URI.
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        /**
         * Then we need to look up the user’s preferred sort order when we build the URI for making
         * the HTTP request. Read from SharedPreferences and check for the value associated with
         * the key: getString(R.string.settings_order_by_key). When building the URI and appending
         * query parameters, instead of hardcoding the “orderby” parameter to be “time”,
         * we will use the user’s preference (stored in the orderBy variable).
         */

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
        // Create a new loader for the given URL
        //return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }




    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        //mLookingData.setVisibility(View.GONE);

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        Log.i(LOG_TAG, new Object(){}.getClass().getEnclosingMethod().getName() + " There: this clear the adapter from previuos data");
        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // TODO: Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, new Object(){}.getClass().getEnclosingMethod().getName() + " There: this reset the data from the adapter");
            mAdapter.clear();
     }
    /**
     * And override a couple methods in EarthquakeActivity.java to inflate the menu,
     * and respond when users click on our menu item:
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // el  on create options menu lo que hace es crear el menu a partir del main xml
    // y el on options Item selected lo que hace es obtener un item del menu y llamar el intent para empezar la settings Activity
    // una vez que se halla dado click se lleva a la actividad de configuracions el cual cual abre la
    // setting_activity.xml
    // pero el fragmento me lleva a la seetings main, el cual contiene toda una pantalla de preferencias
    // que permite introducir la cantidad de vistas que deseamos ver.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
