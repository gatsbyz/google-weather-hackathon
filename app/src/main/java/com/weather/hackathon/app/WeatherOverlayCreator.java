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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.weather.hackathon.model.Layer;
import com.weather.hackathon.model.LayersFetcher;

import java.util.Collection;

/**
 * Used for creating a {@link TileOverlay} for a specific weather layer.  Listens for changes in the list
 * list of layers to update the overlay when the list changes.
 *
 * @author michael.krussel
 */
public class WeatherOverlayCreator implements LayersFetcher.LayersResultListener {
    private final GoogleMap map;
    private final String layerToDisplay;
    private TileOverlay currentOverlay;
    private Layer currentLayer;

    /**\
     * @param map The map that should display the overlay.
     * @param layerToDisplay The {@link Layer#getName() name} of the layer for which to create an overlay.
     * @throws NullPointerException If map or layerToDisplay is null
     */
    public WeatherOverlayCreator(@NonNull GoogleMap map, @NonNull String layerToDisplay) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        if (layerToDisplay == null) {
            throw new NullPointerException("layerToDisplay is null");
        }
        this.map = map;
        this.layerToDisplay = layerToDisplay;
    }

    @Override
    public void onLayersReceived(@NonNull Collection<Layer> layers) {
        for (Layer layer : layers) {
            if (layerToDisplay.equals(layer.getName())) {
                if (layer.equals(currentLayer)) {
                    break;
                }
                currentLayer = layer;

                if (currentOverlay != null) {
                    currentOverlay.remove();
                }

                TileProvider tileProvider = new WeatherOverlayProvider(layer);
                TileOverlayOptions tileOverlay = new TileOverlayOptions();
                tileOverlay.tileProvider(tileProvider);
                tileOverlay.fadeIn(true);
                currentOverlay = map.addTileOverlay(tileOverlay);

                break;

            }
        }
    }
}
