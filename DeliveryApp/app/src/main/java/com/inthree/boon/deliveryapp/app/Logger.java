/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * This class is used provide the necessary log methods
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class Logger {

    /**
     * Tag to be presented in the log
     */
    private static String tag = "Delivery App:::";

    /**
     * Default Constructor
     */
    private Logger() {
    }

    /**
     * Method to log the error
     *
     * @param e Exception raised by the logger
     */
    public static void logError(Exception e) {
        if (e != null && !e.getMessage().isEmpty())
            Log.e(tag, e.getMessage());
    }

    /**
     * Used to track the throwable logs.
     *
     * @param msg The String reference indicates message to be logged.
     * @param th  Catch the throwable errors.
     */
    public static void logErrorThrowable(String msg, Throwable th) {
        Log.e(tag, msg, th);
    }

    /**
     * Method to log the information
     *
     * @param message Exception raised by the logger
     */
    public static void logInfo(String message) {
        Log.i(tag, message);
    }

    /**
     * Method to display the Toast message
     *
     * @param context Context
     * @param message Message to show
     */
    public static void showShortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}