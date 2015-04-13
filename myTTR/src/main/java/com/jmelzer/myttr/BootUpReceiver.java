/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 23.02.14 
*
*/


package com.jmelzer.myttr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jmelzer.myttr.logic.SyncManager;

/**
 * start the sync service after boot.
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SyncManager.class));
    }

}