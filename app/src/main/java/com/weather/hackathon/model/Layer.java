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

/**
 * Represents a layer that can provide tiles for a type of weather data.
 *
 * @author michael.krussel
 */
public class Layer {
    private final String name;
    private final long timestamp;
    private final int maxZoom;

    /**
     * @param name The name of the layer in english.
     * @param timestamp The timestamp of the tiles to request.
     * @param maxZoom The maximum zoom level supported by this layer.
     * @throws NullPointerException If name is null.
     */
    public Layer(@NonNull String name, long timestamp, int maxZoom) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
        this.timestamp = timestamp;
        this.maxZoom = maxZoom;
    }

    /**
     * @return The name of the storm in english.
     */
    @NonNull public String getName() {
        return name;
    }

    /**
     * @return The timestamp of the tiles to request.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**\
     * @return maxZoom The maximum zoom level supported by this layer.
     */
    public int getMaxZoom() {
        return maxZoom;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Layer layer = (Layer) o;

        if (timestamp != layer.timestamp) return false;
        if (maxZoom != layer.maxZoom) return false;
        return name.equals(layer.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + maxZoom;
        return result;
    }
}
