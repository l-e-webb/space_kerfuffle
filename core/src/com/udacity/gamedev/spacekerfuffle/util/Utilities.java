package com.udacity.gamedev.spacekerfuffle.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Convenience methods.
 */
public class Utilities {

    /**
     * Returns the number of seconds since a long given by TimeUtils.nanoTime()
     * @param  time  long representing timestamp from TimeUtils.nanoTime();
     * @return  Seconds since parameter time as float.
     */
    public static float secondsSince(long time) {
        return (TimeUtils.nanoTime() - time) * MathUtils.nanoToSec;
    }

    /**
     * Converts an integer to a string with a minimum number of places, padding the result with
     * leading zeros.
     * @param  number  int to be converted.
     * @param  places  Minimum desired number of places (will pad with leading zeros if necessary).
     * @return  Formatted integer string.
     */
    public static String formatWithLeadingZeros(int number, int places) {
        String str = "" + number;
        while (str.length() < places) {
            str = "0" + str;
        }
        return str;
    }
}
