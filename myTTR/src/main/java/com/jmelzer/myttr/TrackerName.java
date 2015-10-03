package com.jmelzer.myttr;

/**
 * Enum used to identify the tracker that needs to be used for tracking.
 *
 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
 * storing them all in Application object helps ensure that they are created only once per
 * application instance.
 */
public enum  TrackerName {
    APP_TRACKER, // Tracker used only in this app.
    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
}
