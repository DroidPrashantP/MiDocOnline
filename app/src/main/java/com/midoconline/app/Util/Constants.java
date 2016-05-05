package com.midoconline.app.Util;

/**
 * Created by Prashant on 6/10/15.
 */
public interface Constants {

    int MY_SOCKET_TIMEOUT_MS = 6000;
    int MAX_RETRIES = 2;

    String APP_ID = "35038";
    String AUTH_KEY = "yVQnaBUHBuHHr9B";
    String AUTH_SECRET = "4TWZa9b4vRSeudf";
    String ACCOUNT_KEY = "HafiUn7qpT2aoucko1Vu";

    /*
   * Change this to your publishable key.
   *
   * You can get your key here: https://manage.stripe.com/account/apikeys
   */
//    String PUBLISHABLE_KEY = "sk_test_0mwlAmQVrlBfQJ5sHlgmXQQy";  // test key
//      String PUBLISHABLE_KEY = "pk_test_rtY8DogK3CGi5lBmPTZVA7AZ"; // publishable test key
//    String PUBLISHABLE_KEY = "sk_live_LQlW1Ljf8PpqTPGeLtPveDvs"; //  live key
    String PUBLISHABLE_KEY = "pk_live_cM3pk51kT0hLVYa0SIBGB8BB";  // live publishable key


    int NOTIFICATION_FORAGROUND = 1004;
    int NOTIFICATION_CONNECTION_LOST = 1005;

    int CALL_ACTIVITY_CLOSE = 1000;
    public final static int LOGIN_TASK_CODE = 1002;
    public final static int LOGIN_RESULT_CODE = 1003;

    public final static String CALL_RESULT = "call_result";
    public final static String CALL_ACTION_VALUE = "call_action_value";

    public final static int RECEIVE_NEW_SESSION = 11110;
    public final static int USER_NOT_ANSWER = 11111;
    public final static int CALL_REJECT_BY_USER = 11112;
    public final static int RECEIVE_HANG_UP_FROM_USER = 11113;
    public final static int SESSION_CLOSED = 11114;
    public final static int SESSION_START_CLOSE = 11115;

    public final static int START_CONNECT_TO_USER = 22220;
    public final static int CONNECTED_TO_USER = 22221;
    public final static int CONNECTION_CLOSED_FOR_USER = 22222;
    public final static int DISCONNECTED_FROM_USER = 22223;
    public final static int DISCONNECTED_TIMEOUT_FROM_USER = 22224;
    public final static int CONNECTION_FAILED_WITH_USER = 22225;
    public final static int ERROR = 22226;

    //Start service variant
    public final static String START_SERVICE_VARIANT = "start_service_variant";
    public final static int AUTOSTART = 1006;
    public final static int RELOGIN = 1007;
    public final static int LOGIN = 1008;


    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String LOGIN_RESULT = "result";

    //Shared Preferences constants
    String USER_LOGIN = "user_login";
    String USER_PASSWORD = "user_password";
    String USER_ID = "user_id";

    long ANSWER_TIME_INTERVAL = 45l;

    //CALL ACTIVITY CLOSE REASONS
    int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    String WIFI_DISABLED = "wifi_disabled";

    String OPPONENTS_LIST_EXTRAS = "opponents_list";
    String CALL_DIRECTION_TYPE_EXTRAS = "call_direction_type";
    String CALL_TYPE_EXTRAS = "call_type";
    String USER_INFO_EXTRAS = "user_info";
    String SHARED_PREFERENCES = "preferences";
    String QB_EXCEPTION_EXTRAS = "exception";
    String KEY_QBHEADER = "QB-Token";
    String DEVICE_TYPE = "android";
    String SUCCESS = "Success";
    String ADULT_EMERGENCY = "adult_emergency";
    String KID_EMERGENCY = "kide_emergency";
    String NORMAL_CALL = "normal_call";
    String OPPONANT_ID = "opponant_id";

    public enum CALL_DIRECTION_TYPE {
        INCOMING,
        OUTGOING
    }

    interface Preferences {
        String PREFERENCES = "midoconline_pref";
        String LOGGED_IN = "logged_in";
        String USER_SESSION_TOKEN = "token";
    }

    interface UserInfoKeys {
        String AVATAR = "image_url";
        String SECRET = "secret_key";
        String KEY = "key";
        String AUTH_TOKEN = "authentication_token";
        String USER_NAME = "user_name";
        String FULL_NAME = "full_name";
        String ID = "user_id";
        String EMAIL = "email";
        String SPECIALIST = "specialist";
        String LICENCE = "licence";
        String CITY = "city";
        String COUNTRY = "country";
        String MOBILE = "mobile";
        String IMAGEPATH = "user_image_url";
        String USER_TYPE = "user_type";
        String USER_SURNAME = "surname";
        String GENDER = "gender";
        String BIRTHDATE = "dob";
        String LOGIN_MODE = "login_mode";
        String HEIGHT = "height";
        String WEIGHT = "weight";
        String BLOOD_GROUP = "blood_group";
        String ZIP_CODE = "zip_code";
        String STREET = "street";
        String MED_NOTES = "med_notes";
        String STATE = "state";
        String TERMS_AND_CONDITIONS = "terms_and_condition";
        String MEDICAL_SPECIALIST = "medical_specialist";
        String DOCTOR = "doctor";
        String STRIPE_KEY = "stripe_key";
        String EMERGENCY_TAG = "emergency_tag";
        String HISTORY_ID = "id";

        String FACEBOOK_ID = "id";
        String FACEBOOK_EMAIL_ID = "email";
        String FACEBOOK_NAME = "name";
        String FACEBOOK_PROFILE_PIC_URL = "profilePicUrl";
        String FACEBOOK_PROFILE_PIC = "picture";
        String FACEBOOK_COVER_PIC_KEY = "cover";
        String DATA = "data";
        String URL = "url";
        String SOURCE = "source";

        String FB_ACCESS_TOKEN = "fb_access_token";
        String FACEBOOK_COVER_PIC = "facebook_coverpic";
        String DD_ACCESS_TOKEN = "dd_access_token";
        String REFRESH_TOKEN = "refresh_token";

        String MODE_FACEBOOK = "facebook";
        String MODE_TWITTER = "twitter";
        String MODE_GOOGLE = "open_id";
        String MODE_EMAIL = "email";

        String CARD_NUMBER = "card_number";
        String CARD_EXPIRY_MONTH = "card_expiry_month";
        String CARD_EXPIRY_YEAR = "card_expiry_year";
        String CARD_CVV = "card_cvv";
        String IsCardDetailAvailble = "isCardDetailAvailable";
    }

    interface BundleKeys {
        String USERTYPE = "userType";
        String PATIENT = "Patient";
        String DOCTOR = "Doctor";
        String FACEBOOK_PARAM_KEYS = "id,name,email,picture.type(large),cover";
        String FIELDS = "fields";
        String NORMAL_CALL = "normal call";
        String HISTORY_CALL = "history call";
        String CALL_TYPE = "call_type";
        String CREATE_USER_SESSION_WITH_QB = "Create_session_with_qb";

        String OPPONANT_NAME = "OpponantName";
    }

    interface URL {
        String BASE_URL = "https://www.midoconline.com/";
        String PATIENT_SIGNUP_URL = BASE_URL + "tokens/user_sign_up";
        String DOCTOR_SIGNUP_URL = BASE_URL + "tokens/doctor_sign_up";
        String SIGNINSTEPONE_URL = BASE_URL + "tokens/get_key.json";
        String SIGNINSTEPTWO_URL = BASE_URL + "tokens.json";
        String SAVE_CARD_URL = BASE_URL + "save_card_details";
        String CHARGE_PAYMENT_URL = BASE_URL + "charge_payment.json";
        String PATIENT_PROFILE_URL = BASE_URL + "user_profile?";
        String DOCTOR_PROFILE_OPTION_URL = BASE_URL + "save_card_details";
        String FORGOTPASSWORD_URL = BASE_URL + "forget_password_request";
        String FACEBOOK_URL = BASE_URL + "tokens/facebook_authentication";
        String UPDATE_PROFILE = BASE_URL + "update_details.json";
        String GET_DOCTOR_LIST = BASE_URL + "view_doctors_list.json";

        String UPDATE_HISTORY_BEFORE_VIDEO_CALL = BASE_URL + "video_chats";
        String UPDATE_HISTORY_AFTER_VIDEO_CALL = BASE_URL + "video_chats";
        String UPDATE_HISTORY_END_VIDEO_CALL = BASE_URL + "video_chats";
        String DOCOTR_HISTORY = BASE_URL + "doctor_history.json";
        String PATIENT_HISTORY = BASE_URL + "patient_history.json";
    }

    interface QuickBox {
        String BASE_URL = "http://api.quickblox.com/";
        String ALL_USER = BASE_URL + "users.json";
    }

    interface QuickBoxUSer {
        String LOGIN_NAME = "login name";
        String QBPASSWORD = "password";
        String QBEMAIL = "email";
        String SESSION_TOKEN = "session_token";
        String QB_ID = "userID";
    }

    interface ApiType {
        String ALL_USER_API = "all Users";

    }

    public class SupportKeys {
    }
}
