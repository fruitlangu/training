/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.app;


import com.inthree.boon.deliveryapp.model.Barcode;


public final class Constants {

    /**
     * constanst for sqlite
     */
    public static final String DB_NAME = "boonboxdelivery.sqlite";

    /**
     * Sqlite Version
     */


    public static final int DB_VERSION = 18;
    public static final String ROLE_ID = "role_id";



    /**
     * constant for username
     */
    public static String USER_NAME = "";

    /**
     * Constant for HTML BLUE
     */
    public static final String HTML_HEAD_BLUE = "<medium><b><font color='#33b5e5'>";

    /**
     * Constant for HTML GREEN
     */
    public static final String HTML_HEAD_GREEN = "<medium><font color='#23CD63'>";

    /**
     * Constant for HTML LIGHT GREY
     */
    public static final String HTML_HEAD_LIGHT_GREY = "<medium><b><font color='#ADB7C6'>";

    /**
     * Constant for HTML LIGHT BLACK
     */
    public static final String HTML_HEAD_LIGHT_BLACK = "<medium><font color='#4d5559'>";

    /**
     * Constant for HTML NO BOLD
     */
    public static final String HTML_HEAD_CLOSE_NO_BOLD = "</font></medium>";

    /**
     * Constant for HTML close tag
     */
    public static final String HTML_HEAD_CLOSE = "</font></b></medium>";

    /**
     * Constant value for storing the user id in preference
     */
    public static final String USER_ID = "user_id";

    /**
     * Constant value for storing the user access token in preference
     */
    public static final String USER_LOGIN_ACCESS_TOKEN = "login_user_access_token";

    /**
     * Constant value for mobile
     */
    public static final String MOBILE = "phone";

    /**
     * Constant value for the mobile header
     */
    public static final String MOBILE_HEADER = "mobile";

    //**********        Customer Details values               ***********/

    public static final String SHIPMENT_NUMBER = "shipment_number";

    public static final String REFERENCE_NO = "order_id";

    public static final String ORDER_ID = "order_id";
    /**
     * Constant value for notification data type for access token Constant value to access the
     * api
     */
    public static String DELIVERED_TITLE = "Please fill the Fields *";

    public static String UNDELIVERED_TITLE = "Reason";

    /**
     * Constance for lat and lang  in billing page
     */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String CUR_LATITUDE = "curelatitude";
    public static final String CUR_LONGITUDE = "curlongitude";

    /**
     * Get the User ID by using username and login
     */
    public static final String LOGIN_USER_ID = "user_id";
    public static final String LOGIN_USER_EMAIL = "email";
    public static final String LOGIN_USER_NAME = "name";
    public static final String USER_LANGUAGE= "language";


    /**
     * Constance for lat and lang  in delivera activity page
     */
    public static final String UN_DEL_LATITUDE = "un_latitude";
    public static final String UN_DEL_LONGITUDE = "un_longitude";

    public static final String UN_DEL_CUR_LATITUDE = "un_curelatitude";
    public static final String UN_DEL_CUR_LONGITUDE = "un_curlongitude";

    /**
     * Constant for push notification
     */
    public static final String DEVICE = "device_model";
    public static final String DEVICE_TOKEN_REGID = "device_token";


    /***
     * Map activity for on and off
     */
    public static final String LOCATION = "yes";
    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places Auto Complete";

    /*Get the adhaar details*/
    public static String aadharCode;

    /*Get the adhaar details*/
    public static String invoicenumber;

    /*Get the barcode data*/
    public static Barcode printLetterBarcodeData;

    public static final String LastSyncTime = "last_sync";

    /**
     * Constant Constructor
     */
    private Constants() {

    }

    /**
     * Constants for api headers
     */
    public static final class ApiHeaders {



    // public static final String BASE_URL = "http://testcloud.in3access.in/lastmile/apis/";
      public static final String BASE_URL = "http://devcloud.in3access.in/lastmile/apis/";
      public static final String BFIL_BASE_URL = "http://devcloud.in3access.in/middleware_old/commonAPI/Bfildelivery/";
//        public static final String BASE_URL = "https://lastmile.boonbox.com/apis/";




        /**
         * Constant value for notification data type for mobile app verification Constant value to
         * access the api
         */
        public static final String LEARNINGSPACE_MOBILE_APP = "X-REQUEST-TYPE";

        /**
         * Constant value for notification data type for user Id Constant value to access the api
         */
        public static final String LEARNING_SPACE_USER_ID = "X-USER-ID";

        /**
         * Constant value for notification data type for access token Constant value to access the
         * api
         */
        public static final String LEARNING_SPACE_ACCESS_TOKEN = "X-ACCESS-TOKEN";

        /**
         * Constant Constructor
         */
        private ApiHeaders() {

        }
    }

}