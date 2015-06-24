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
package com.weather.hackathon.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.weather.hackathon.model.Layer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


/**
 * Provider for providing tiles of a {@link com.google.android.gms.maps.model.TileOverlay} of
 * a {@link Layer weather layer}.
 *
 * @author michael.krussel
 */
public class WeatherOverlayProvider extends UrlTileProvider {
    private static final String TAG = "WeatherOverlayProvider";
    private final Layer layer;

    /**
     * @param layer The layer to use for loading tiles.
     * @throws NullPointerException If layer is null.
     */
    public WeatherOverlayProvider(@NonNull Layer layer) {
        super(256, 256);
        if (layer == null) {
            throw new NullPointerException("layer is null");
        }
        this.layer = layer;
    }

    @Override
    @Nullable
    public URL getTileUrl(int x, int y, int zoom) {
        // make sure we can handle this zoom level
        if (zoom > layer.getMaxZoom()) {
            return null;
        }

        // convert the tile into a quad key
        StringBuilder quadKey = new StringBuilder();
        for (int level = zoom; level > 0; level--) {
            int mask = 1 << (level-1);
            boolean xMask = (x & mask) != 0;
            boolean yMask = (y & mask) != 0;
            if (xMask && yMask) {
                quadKey.append('3');
            } else if (yMask) {
                quadKey.append('2');
            }
            else if (xMask) {
                quadKey.append('1');
            } else {
                quadKey.append('0');
            }
        }

        try {
            return new URL(String.format(Locale.US, "http://hackathon.weather.com/Maps/imgs/%s/u%d/%s.png",
                    layer.getName(), layer.getTimestamp(), quadKey));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to create a valid tile url", e);
            return null;
        }
    }
}
