/**
 * @category boon
 * @package com.inthree.utils
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */
package com.inthree.boon.deliveryapp.utils;

import android.util.Log;

/**
 * StringOperationsUtils is used for access the twitter after user login to post  and share the page
 *
 * @author Contus Team <developers@contus.in>
 * @version 3.5
 */

public class StringOperationsUtils {

    /**
     * Instantiates a new string operations.
     */
    public StringOperationsUtils() {
        Log.i("Constructor", " string operation");
    }

    /**
     * Titleize for display the uppercase to lowercase
     *
     * @param source1 The source get the editText string
     * @return The  EditText string
     */
    public String titleize(String source1) {
        boolean cap = true;
        String source;
        source = source1.toLowerCase();
        char[] out = source.toCharArray();
        int i = source.length();
        int len = i;
        for (i = 0; i < len; i++) {
            if (Character.isWhitespace(out[i])) {
                cap = true;
                continue;
            }
            if (cap) {
                out[i] = Character.toUpperCase(out[i]);
                cap = false;
            }
        }
        return new String(out);
    }




}
