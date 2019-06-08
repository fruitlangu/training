/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.server;

/**
 * This is the Server Class used to load the server urls.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class ServerUrls {

    /**
     * The constant Base URL.
     */
    public static final String BASE_URL = "http://www.learningspacedigital.com/api/v1/";

    /**
     * Static page  Base URL.
     */
    public static final String STATIC_PAGE_URL = "http://www.learningspacedigital.com/";

    /**
     * URL for registration api.
     */
    public static final String SIGN_UP = "auth/register";

    /**
     * URL for forgot password api api.
     */
    public static final String FORGOT_PASSWRD = "auth/forgotpassword";

    /**
     * URL for home page
     */
    public static final String HOME = "home";


    /**
     * URL for the profile page
     */
    public static final String PROFILE = "profile";

    /**
     * URL for Add more preference list
     */
    public static final String ADD_MORE = "myPreferenceList";

    /**
     * URL for Browse Videos
     */
    public static final String BROWSE_VIDEOS = "myPreferenceList/all";

    /**
     * URL for get selected preference list.
     */
    public static final String GET_PREFERENCE = "myPreferenceCategoryList";

    /**
     * URL for login api.
     */
    public static final String LOGIN = "auth/login";

    /**
     * URL for Password change
     */
    public static final String CHANGE_PASSWRD = "auth/change";

    /**
     * URL for trending videos
     */
    public static final String VIDEO_LIST = "videosRelatedTrending";

    /**
     * URL for getting playlist
     */
    public static final String PLAYLIST = "playlist";

    /**
     * URL for save preference value
     */
    public static final String SAVE_PREFERENCE_LIST = "savemyPreferenceList";

    /**
     * URL for get video details
     */
    public static final String VIDEO_DETAIL = "videos/{video_id}/{slug}";

    /**
     * URL for get video details for Exam
     */
    public static final String VIDEO_DETAIL_EXAM = "videos/exam/{videoId}/{examid}";

    /**
     * URL for getting video comments
     */
    public static final String VIDEO_COMMENT = "videoComments";

    /**
     * URL for getting the customer profile
     */
    public static final String PROFILE_UPDATE = "customerProfile";

    /**
     * URL for getting the favourite list
     */
    public static final String FAVOURITE = "favourite";

    /**
     * URL for getting the notification
     */
    public static final String NOTIFICATION = "notifications";

    /**
     * URL for getting the all exam
     */
    public static final String EXAM_LIST = "allexams";

    /**
     * URL for getting the playlist
     */
    public static final String PLAYLIST_FOLLOW_UNFOLLOW = "playlists";

    /**
     * URL for getting the videos
     */
    public static final String SEARCH_VIDEOS = "videos";

    /**
     * URL for getting the video questions
     */
    public static final String POST_QUESTIONS = "videoQuestions";

    /**
     * URL for getting the live video details
     */
    public static final String LIVE_VIDEOS = "livevideos";

    /**
     * URL for getting the live more details
     */
    public static final String LIVE_MORE_DATA = "getLivevideos";

    /**
     * URL for getting the Exam Details
     */
    public static final String EXAM = "exams";

    /**
     * URL for getting the get Payment Details
     */
    public static final String PAYMENT = "getrsaResponse";

    /**
     * URL for getting the recently viewed videos
     */
    public static final String RECENTLY_VIEWED = "recentlyViewed";

    /**
     * URL for getting the playlist videos
     */
    public static final String PLAYLIST_VIDEO = "playlists/video-playlists/{video_id}";

    /**
     * URL for getting the subscription details
     */
    public static final String GET_SUBSCRIPTION_LIST = "subscriptions";

    /**
     * URL for static about us
     */
    public static final String ABOUT_US = "static/faq_mobile";

    /**
     * URL for static blog
     */
    public static final String CONTACT_US = "getsiteaddress";

    /**
     * URL to get exam by group
     */
    public static final String EXAM_GROUP = "exam/{id}";

    /**
     * URL to get group of exams
     */
    public static final String EXAM_LIST_SEPARATE = "group/{id}";

    /**
     * URL to get the contact us page
     */
    public static final String CONTACT = "staticContent/contactus";

    /**
     * Private empty constructor needed by the class.
     */
    private ServerUrls() {

    }
}

