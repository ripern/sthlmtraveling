package com.markupartist.sthlmtraveling;

import static com.markupartist.sthlmtraveling.ApiSettings.STHLM_TRAVELING_API_ENDPOINT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xml.sax.InputSource;

import android.util.Log;

public class Planner {
    private static final String TAG = "Planner";
    private static Planner instance = null;
    /**
     * Needed to keep track of which routes to remove when getting earlier and
     * later routes. Later on we will return the full set then this will be removed.
     */
    private int mPreviousRouteCount;
    private int mRequestCount;
    private String mIdent;
    private StopParser mStopFinder;
    private RouteParser mRouteFinder;
    private RouteDetailParser mRouteDetailFinder;
    private ArrayList<Route> mRoutes = null;
    private ArrayList<String> mRouteDetails = null;

    private Planner() {
        mStopFinder = new StopParser();
        mRouteFinder = new RouteParser();
        mRouteDetailFinder = new RouteDetailParser();
    }

    public ArrayList<String> findStop(String name) {
        ArrayList<String> stops = new ArrayList<String>();
        try {
            URL endpoint = new URL(STHLM_TRAVELING_API_ENDPOINT
                    + "?method=findStop&name=" + URLEncoder.encode(name));
            InputSource input = new InputSource(endpoint.openStream());
            stops = mStopFinder.parseStops(input);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return stops;
    }

    public ArrayList<Route> findRoutes(String from, String to) {
        Log.d(TAG, "Searching for from=" + from + ",to=" + to);

        String fromEncoded = URLEncoder.encode(from);
        String toEncoded = URLEncoder.encode(to);

        mRoutes = new ArrayList<Route>();
        try {
            URL endpoint = new URL(STHLM_TRAVELING_API_ENDPOINT
                    + "?method=findRoutes&from=" 
                    + fromEncoded + "&to=" + toEncoded);
            InputSource input = new InputSource(endpoint.openStream());
            mRoutes = mRouteFinder.parseRoutes(input);

            mPreviousRouteCount = mRoutes.size();
            // Update the request count, needed for all request that needs an ident.
            mRequestCount = mRouteFinder.getRequestCount();
            mIdent = mRouteFinder.getIdent();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return mRoutes;
    }

    public ArrayList<Route> findEarlierRoutes() {
        if (mIdent == null) {
            Log.e(TAG, "findEarlierRoutes was accessed before findRoutes.");
            throw new IllegalStateException("findRoutes must be run firsts.");
        }

        String ident = URLEncoder.encode(mIdent);

        mRoutes = new ArrayList<Route>();
        try {
            URL endpoint = new URL(STHLM_TRAVELING_API_ENDPOINT
                    + "?method=findEarlierRoutes"
                    + "&requestCount=" + mRequestCount
                    + "&ident=" + ident);
            InputSource input = new InputSource(endpoint.openStream());
            mRoutes = mRouteFinder.parseRoutes(input);
            /*
            ArrayList<Route> routes = mRouteFinder.parseRoutes(input);
            int newCount = routes.size();
            int count = mPreviousRouteCount;
            int itemsToAdd = newCount - count;
            for (int i = 0; i < itemsToAdd; i++) {
                Route route = routes.get(i);
                mRoutes.add(route);
            }
            mPreviousRouteCount = routes.size();
            */

            // Update the request count, needed for all request that needs an ident.
            mRequestCount = mRouteFinder.getRequestCount();
            mIdent = mRouteFinder.getIdent();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return mRoutes;
    }

    public ArrayList<Route> findLaterRoutes() {
        if (mIdent == null) {
            Log.e(TAG, "findEarlierRoutes was accessed before findRoutes.");
            throw new IllegalStateException("findRoutes must be run firsts.");
        }

        String ident = URLEncoder.encode(mIdent);

        mRoutes = new ArrayList<Route>();
        try {
            URL endpoint = new URL(STHLM_TRAVELING_API_ENDPOINT
                    + "?method=findLaterRoutes"
                    + "&requestCount=" + mRequestCount
                    + "&ident=" + ident);
            InputSource input = new InputSource(endpoint.openStream());
            
            mRoutes = mRouteFinder.parseRoutes(input);
            /*
            ArrayList<Route> routes = mRouteFinder.parseRoutes(input);

            int count = mPreviousRouteCount;
            for (int i = count; i < routes.size(); i++) {
                Route route = routes.get(i);
                mRoutes.add(route);
            }
            */

            //mPreviousRouteCount = routes.size();
            mRequestCount = mRouteFinder.getRequestCount();
            mIdent = mRouteFinder.getIdent();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return mRoutes;
    }

    public ArrayList<Route> lastFoundRoutes() {
        return mRoutes;
    }

    public ArrayList<String> findRouteDetails(Route route) {
        mRouteDetails = new ArrayList<String>();
        try {
            URL endpoint = new URL(STHLM_TRAVELING_API_ENDPOINT
                    + "?method=routeDetail&ident=" + route.ident 
                    + "&routeId=" + route.routeId
                    + "&requestCount=" + mRequestCount);
            mRouteDetails = mRouteDetailFinder.parseDetail(
                    new InputSource(endpoint.openStream()));
            // Update the request count, needed for all request that needs an ident.
            mRequestCount = mRouteDetailFinder.getRequestCount();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return mRouteDetails;
    }

    public ArrayList<String> lastFoundRouteDetail() {
        return mRouteDetails;
    }

    public static Planner getInstance() {
        if (instance == null)
            instance = new Planner();
        return instance;
    }
}