package com.jmelzer.myttr.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class GoogleMapStarter {


    public static void showMap(Activity activity, String lokal) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(UrlUtil.formatAddressToGoogleMaps(lokal)));
        activity.startActivity(intent);
    }
}