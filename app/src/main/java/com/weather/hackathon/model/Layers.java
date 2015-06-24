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
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Layers {
    /**
     * Parses a json string with the series list data into a list of {@link Layer layers}.
     *
     * @param json The json string to parse.
     * @return A list of a layers that have tiles available.
     * @throws JsonSyntaxException if json is not a valid representation for an object of type LayersSeries
     */
    @NonNull public static Collection<Layer> parseJson(String json) {
        if (json == null) {
            throw new NullPointerException("json input is null");
        }

        LayerSeries layerSeries = LayerSeries.fromJson(json);
        final Map<String, LayerSeries.SeriesInfo> seriesInfos = layerSeries.getSeriesInfo();
        List<Layer> layers = new ArrayList<>(seriesInfos.size());
        for (Map.Entry<String, LayerSeries.SeriesInfo> seriesEntry : seriesInfos.entrySet()) {
            final LayerSeries.SeriesInfo seriesInfo = seriesEntry.getValue();
            LayerSeries.Series latestSeries = seriesInfo.getLatestSeries();
            if (latestSeries != null) {
                layers.add(new Layer(seriesEntry.getKey(), latestSeries.getUnixDate(), seriesInfo.getMaxZoom()));
            }
        }
        return layers;
    }
}
