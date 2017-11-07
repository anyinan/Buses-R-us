package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }
    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when JSON data does not have expected format
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     */

    public void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {

            JSONArray stops = new JSONArray(jsonResponse);
            for (int index = 0 ; index < stops.length() ; index++){
                try{
                    JSONObject stop = stops.getJSONObject(index);
                    parseStop(stop);
                }catch (JSONException e){
                    throw new StopDataMissingException();
                }

            }
    }

    /**
     * Parse one stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  stop    JSONObject parsed by all stops.
     * @throws JSONException   when JSON data does not have expected format
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is not an array </li>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     */

    public void parseStop(JSONObject stop) throws JSONException, StopDataMissingException {

        String name = stop.getString("Name").trim();

        String atStreet = stop.getString("AtStreet");

        int wheelchairAccess = stop.getInt("WheelchairAccess");
        String bayNo = stop.getString("BayNo");
        Double lat = stop.getDouble("Latitude");
        Double lon = stop.getDouble("Longitude");
        LatLon location = new LatLon(lat , lon);
        int stopNo = stop.getInt("StopNo");
        String onStreet = stop.getString("OnStreet");
        String city = stop.getString("City");
        String route = stop.getString("Routes");

        for (String r : route.split(",")){
            storeStop(stopNo , name , location , r.trim() );
        }
    }

    /**
     * Store the parsed Stop into the named Stop
     *
     * @param stopNo       the number of the stop
     * @param name       the name of the stop
     * @param location          the Latlon of each stop
     * @param route          the route contained by the stop
     */
    private void storeStop(int stopNo , String name , LatLon location , String route) throws JSONException {
        Stop s = StopManager.getInstance().getStopWithId(stopNo , name , location);
        s.addRoute(RouteManager.getInstance().getRouteWithNumber(route));
    }
}
