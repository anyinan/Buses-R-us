package ca.ubc.cs.cpsc210.translink.util;

/**
 * Compute relationships between points, lines, and rectangles represented by LatLon objects
 */
public class Geometry {
    /**
     * Return true if the point is inside of, or on the boundary of, the rectangle formed by northWest and southeast
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param point             the point in question
     * @return                  true if the point is on the boundary or inside the rectangle
     */
    public static boolean rectangleContainsPoint(LatLon northWest, LatLon southEast, LatLon point) {

        return (between(southEast.getLatitude() ,northWest.getLatitude(), point.getLatitude()) &&
                between(northWest.getLongitude() , southEast.getLongitude() , point.getLongitude()));
    }

    /**
     * Return true if the rectangle intersects the line
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param src               one end of the line in question
     * @param dst               the other end of the line in question
     * @return                  true if any point on the line is on the boundary or inside the rectangle
     */
    public static boolean rectangleIntersectsLine(LatLon northWest, LatLon southEast, LatLon src, LatLon dst) {
        if (rectangleContainsPoint(northWest,southEast,src) || rectangleContainsPoint(northWest , southEast ,dst)){
            return true;
        }
        if (src.getLongitude() - dst.getLongitude() > 0){
            double k = calculateSlope(src.getLongitude() , dst.getLongitude() , src.getLatitude() ,dst.getLatitude());
            double b = src.getLatitude() - k * src.getLongitude();
            for (double x = dst.getLongitude() ; x <= src.getLongitude() ; x += 0.000001){
                double y = k * x  + b;
                if(rectangleContainsPoint(northWest ,southEast ,new LatLon(y ,x)))
                    return true;
            }
        }else {
            double k = calculateSlope(dst.getLongitude() , src.getLongitude() , dst.getLatitude() , src.getLatitude());
            double b = dst.getLatitude() - k * dst.getLongitude();
            for (double x = src.getLongitude(); x <= dst.getLongitude(); x += 0.000001) {
                double y = k * x + b;
                if(rectangleContainsPoint(northWest, southEast, new LatLon(y, x)))
                    return true;
            }
        }
        return false;
    }

    /**
     * A method that calculate the slope of the line
     * @param x1      the x coordinate of src point
     * @param x2      the x coordinate of dst point
     * @param y1      the y coordinate of src point
     * @param y2      the y coordinate of dst point
     * @return          double number represents the slope of line which contains two given points.
     */
    public static double calculateSlope(double x1, double x2 , double y1, double y2){
        return (y2 - y1)/(x2 - x1);
    }

    /**
     * A utility method that you might find helpful in implementing the two previous methods
     * Return true if x is >= lwb and <= upb
     * @param lwb      the lower boundary
     * @param upb      the upper boundary
     * @param x         the value in question
     * @return          true if x is >= lwb and <= upb
     */
    private static boolean between(double lwb, double upb, double x) {
        return lwb <= x && x <= upb;
    }
}
