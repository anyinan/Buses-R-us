package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ExpectedCountdown, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when JSON response does not have expected format
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */

    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {


            JSONArray arrivals = new JSONArray(jsonResponse);

            for (int index = 0 ; index < arrivals.length() ; index++) {

                try{
                    JSONObject arrival = arrivals.getJSONObject(index);
                    String direction = arrival.getString("Direction");
                    String routeMap = (arrival.getJSONObject("RouteMap")).getString("Href");
                    String routeName = arrival.getString("RouteName").trim();
                    String routeNo = arrival.getString("RouteNo");

                    JSONArray schedules = arrival.getJSONArray("Schedules");

                    Route r = RouteManager.getInstance().getRouteWithNumber(routeNo, routeName);

                    for (int i = 0; i < schedules.length(); i++) {
                        try{
                            JSONObject schedule = schedules.getJSONObject(i);
                            String destination = schedule.getString("Destination");
                            String pattern = schedule.getString("Pattern");
                            int expectedCountdown = schedule.getInt("ExpectedCountdown");
                            String scheduleStatus = schedule.getString("ScheduleStatus");

                            Arrival a = new Arrival(expectedCountdown, destination, r);
                            a.setStatus(scheduleStatus);
                            stop.addArrival(a);

                        }catch (JSONException e){
                            continue;
                        }
                    }

                }catch (JSONException e){
                    continue;
                }
            }
            if (!stop.iterator().hasNext()){
                throw new ArrivalsDataMissingException();
            }
    }

}
