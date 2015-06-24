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

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Collection;

/**
 * Handles requests the set of available {@link Layer layers} from the server
 *
 * @author michael.krussel
 */
public class LayersFetcher {
    private static final String TAG = "LayersFetcher";
    private final OkHttpClient httpClient;
    private  final Handler handler = new Handler();
    @Nullable private LayersResultListener layersResultListener;

    /**
     * @param httpClient Client to use for making http requests.
     * @throws NullPointerException If httpClient is null.
     */
    public LayersFetcher(@NonNull OkHttpClient httpClient) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null");
        }
        this.httpClient = httpClient;
    }

    /**
     * Requests a new set of available tiles in the background.  A successful load will notify the
     * {@link LayersResultListener} if one has been {@link #setLayersResultListener(LayersResultListener) set}.
     *
     * <p/> Only successful fetches are reported.  Errors are logged and ignored.  Listener's callback
     * {@link LayersResultListener#onLayersReceived(Collection) method} will be called on the same thread that
     * created this instance.
     *
     * <p/> The layers fetched will contain the {@link Layer#getTimestamp() timestamp} of the newest tiles for
     * that layer.
     */
    public void fetchAsync() {
        Request request = new Request.Builder()
                .url("http://hackathon.weather.com/Maps/jsonserieslist.do")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Unable to retrieve list of layers", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        final String json = response.body().string();
                        final Collection<Layer> layers = Layers.parseJson(json);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (layersResultListener != null) {
                                    layersResultListener.onLayersReceived(layers);
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "Error request list of layers.  statusCode=" + response.code());
                    }
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Unable to parse list of layers", e);
                }
            }
        });
    }

    /**
     * Sets the listener that will be notified of new layers that get loaded.  A value of null will have no listener,
     * and any {@link Layer layers} fetched will be ignored.
     *
     * <p/> {@link LayersResultListener#onLayersReceived(Collection)} will be called from the same thread that
     * created this instance.
     *
     * @param layersResultListener The listener to get notified of fetched layers, or null.
     */
    public void setLayersResultListener(@Nullable LayersResultListener layersResultListener) {
        this.layersResultListener = layersResultListener;
    }

    /**
     * Interface for getting notified when a new set of layers get fetched.
     *
     * @see LayersFetcher#fetchAsync().
     */
    public interface LayersResultListener {
        /**
         * Callback for when the fetcher has fetched the current set of layers.  Will be called even if no changes
         * have been made to the set of available layers.
         *
         * @param layers The set of available layers that was fetched.
         */
        void onLayersReceived(Collection<Layer> layers);
    }
}
