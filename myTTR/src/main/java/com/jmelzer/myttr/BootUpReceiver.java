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
import com.jmelzer.myttr.activities.LoginActivity;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}