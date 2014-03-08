/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 08.03.14 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import com.jmelzer.myttr.R;

/**
 * will be aclled after the user press the notification for new points.
 */
public class NewPointsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpointsreceived);
    }
}
