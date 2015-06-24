/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The Weather Company's API for obtaining radar tile images is provided only for the purposes of the hackathon hosted by
 * The Weather Company in June, 2015.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the
 * warranties of merchantability, fitness for a particular purpose or noninfringement.  In no event shall the authors or
 * copyright holders be liable for any claim, damages or other liability, whether in action of contract, tort or
 * otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */
package com.weather.hackathon.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.*;

/**
 * Class for using GSON to parse the series list data.
 *
 * @author michael.krussel
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection") // GSON will update and set the collections
class LayerSeries {
    private Map<String, SeriesInfo> seriesInfo;

    private LayerSeries() {
     // Will be handled by deserialization
    }

    /**
     * Parses a json string with the series list data into a LayerSeries.
     *
     * @param json The json string to parse.
     * @return A LayerSeries representing the parsed string.
     * @throws JsonSyntaxException If json is not a valid representation for an object of typ LayersSeries
     * @throws NullPointerException If json is null.
     */
    public static LayerSeries fromJson(@NonNull String json) {
        if (json == null) {
            throw new NullPointerException("json is null");
        }
        return new Gson().fromJson(json, LayerSeries.class);
    }

    @NonNull public Map<String, SeriesInfo> getSeriesInfo() {
        return seriesInfo == null ? new HashMap<String, SeriesInfo>() : new HashMap<>(seriesInfo);
    }

    /**
     * Represents the info about the series in a layer.
     */
    static final class SeriesInfo {
        private List<Series> series;
        private int maxZoom;

        /**
         * Finds the series with the latest timestamp.
         *
         * @return The latest series for this layer.  Returns null if the layer has no series.
         */
        @Nullable public Series getLatestSeries() {
            if (series == null || series.isEmpty()) {
                return null;
            }

            Series latestSeries = series.get(0);
            for (Series seriesToCheck : series) {
                if (latestSeries.compareTo(seriesToCheck) < 0) {
                    latestSeries = seriesToCheck;
                }
            }
            return latestSeries;
        }

        /**
         * @return The maximum zoom level supported by this layer.
         */
        public int getMaxZoom() {
            return maxZoom;
        }
    }

    /**
     * A single series for a layer.
     */
    static final class Series implements Comparable<Series> {
        private long unixDate;

        /**
         * @return The timestamp of the series as a unix epoch.
         */
        public long getUnixDate() {
            return unixDate;
        }

        @Override
        public int compareTo(@NonNull Series another) {
            // unixDate will may get set to something other than zero by gson
            //noinspection ConstantConditions
            return unixDate < another.unixDate ? -1 : (unixDate == another.unixDate ? 0 : 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Series series = (Series) o;

            return unixDate == series.unixDate;

        }

        @Override
        public int hashCode() {
            return (int) (unixDate ^ (unixDate >>> 32));
        }
    }
}
