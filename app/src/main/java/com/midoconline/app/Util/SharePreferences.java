package com.midoconline.app.Util;

import android.content.Context;

/**
 * Created by Prashant on 6/10/15.
 */
public class SharePreferences implements Constants.Preferences,Constants.UserInfoKeys,Constants.QuickBoxUSer{
    private static android.content.SharedPreferences mSharedPreferences;

    public SharePreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    // Login preferences
    public void setVideoSessionToken(String token) {
        mSharedPreferences.edit().putString(USER_SESSION_TOKEN, token).commit();
    }

    public String getVideoSessionToken() {
        return mSharedPreferences.getString(USER_SESSION_TOKEN ,"");
    }

    public void setQBSessionToken(String token){
        mSharedPreferences.edit().putString(SESSION_TOKEN, token).commit();
    }

    public String getQBSessionToken(){
        return mSharedPreferences.getString(SESSION_TOKEN,"");
    }

    public void setQBLoginName(String loginname){
        mSharedPreferences.edit().putString(LOGIN_NAME, loginname).commit();
    }

    public String getQBLoginName(){
        return mSharedPreferences.getString(LOGIN_NAME,"");
    }

    public void setQBQBEmail(String email){
        mSharedPreferences.edit().putString(QBEMAIL, email).commit();
    }

    public String getQBEmail(){
        return mSharedPreferences.getString(QBEMAIL,"");
    }

    public void setQBQBPassword(String password){
        mSharedPreferences.edit().putString(QBPASSWORD, password).commit();
    }

    public String getQBPassword(){
        return mSharedPreferences.getString(QBPASSWORD,"");
    }

    public void setQBID(String userid){
        mSharedPreferences.edit().putString(QB_ID, userid).commit();
    }

    public String getQBID(){
        return mSharedPreferences.getString(QB_ID,"");
    }



    public void ClearSharepreference(){
        mSharedPreferences.edit().clear().commit();
    }

    public boolean isLoggedIn() {
        return mSharedPreferences.getBoolean(LOGGED_IN, false);
    }

    // Login preferences
    public void setLoggedIn(boolean loggedIn) {
        mSharedPreferences.edit().putBoolean(LOGGED_IN, loggedIn).commit();
    }

    public void setUserId(String userId) {
        mSharedPreferences.edit().putString(ID, userId).commit();
    }
    public String getUserId() {
        return mSharedPreferences.getString(ID, "");
    }

    public void setUserName(String userName) {
        mSharedPreferences.edit().putString(USER_NAME, userName).commit();
    }
    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME, "");
    }

    public void setUserFullName(String userName) {
        mSharedPreferences.edit().putString(FULL_NAME, userName).commit();
    }
    public String getUserFullName() {
        return mSharedPreferences.getString(FULL_NAME, "");
    }

    public void setUserEmail(String userEmail) {
        mSharedPreferences.edit().putString(EMAIL, userEmail).commit();
    }
    public String getUserEmail() {
        return mSharedPreferences.getString(EMAIL, "");
    }

    public void setUserSpecialist(String userSpecialistLicence) {
        mSharedPreferences.edit().putString(SPECIALIST, userSpecialistLicence).commit();
    }
    public String getUserSpecialist() {
        return mSharedPreferences.getString(SPECIALIST, "");
    }

    public void setUserLicence(String userSpecialistLicence) {
        mSharedPreferences.edit().putString(LICENCE, userSpecialistLicence).commit();
    }
    public String getUserLicence() {
        return mSharedPreferences.getString(LICENCE, "");
    }

    public void setAuthenticationToken(String auth_token) {
        mSharedPreferences.edit().putString(AUTH_TOKEN, auth_token).commit();
    }
    public String getAuthenticationToken() {
        return mSharedPreferences.getString(AUTH_TOKEN, "");
    }

    public void setKey(String key) {
        mSharedPreferences.edit().putString(KEY, key).commit();
    }
    public String getKey() {
        return mSharedPreferences.getString(KEY, "");
    }

    public void setSecretKey(String key) {
        mSharedPreferences.edit().putString(SECRET, key).commit();
    }
    public String getSecretKey() {
        return mSharedPreferences.getString(SECRET, "");
    }

    public void setCity(String City) {
        mSharedPreferences.edit().putString(CITY, City).commit();
    }
    public String getCity() {
        return mSharedPreferences.getString(CITY, "");
    }

    public void setCountry(String Country) {
        mSharedPreferences.edit().putString(COUNTRY, Country).commit();
    }
    public String getCountry() {
        return mSharedPreferences.getString(COUNTRY, "");
    }

    public void setMobile(String Mobile) {
        mSharedPreferences.edit().putString(MOBILE, Mobile).commit();
    }
    public String getMobile() {
        return mSharedPreferences.getString(MOBILE, "");
    }

    public void setUserThumbnail(String path) {
        mSharedPreferences.edit().putString(IMAGEPATH, path).commit();
    }
    public String getUserThumbnail() {
        return mSharedPreferences.getString(IMAGEPATH, "");
    }

    public void setUserType(String userType) {
        mSharedPreferences.edit().putString(USER_TYPE, userType).commit();
    }
    public String getUserType() {
        return mSharedPreferences.getString(USER_TYPE, "");
    }

    public void setSurname(String surname) {
        mSharedPreferences.edit().putString(USER_SURNAME, surname).commit();
    }
    public String getSurname() {
        return mSharedPreferences.getString(USER_SURNAME, "");
    }
    public void setGender(String gender) {
        mSharedPreferences.edit().putString(GENDER, gender).commit();
    }
    public String getGender() {
        return mSharedPreferences.getString(GENDER, "");
    }
    public void setBirthdate(String birthdate) {
        mSharedPreferences.edit().putString(BIRTHDATE, birthdate).commit();
    }
    public String getBirthdate() {
        return mSharedPreferences.getString(BIRTHDATE, "");
    }

    public void setTermsAndCondition(String termsAndCondition) {
        mSharedPreferences.edit().putString(TERMS_AND_CONDITIONS, termsAndCondition).commit();
    }
    public String getTermsAndCondition() {
        return mSharedPreferences.getString(TERMS_AND_CONDITIONS, "");
    }


    /// facebook parameter

    public String getLoginMode() {
        return mSharedPreferences.getString(LOGIN_MODE, "");
    }

    public void setLoginMode(String loginMode) {
        mSharedPreferences.edit().putString(LOGIN_MODE, loginMode).commit();
    }

    public String getFacebookId() {
        return mSharedPreferences.getString(FACEBOOK_ID, "");
    }

    /*
    *    facebook preferences
    */

    public void setFBAccessToken(String accessToken) {
        mSharedPreferences.edit().putString(FB_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public void setFacebookId(String id) {
        mSharedPreferences.edit().putString(FACEBOOK_ID, id).commit();
    }

    public String getFacebookProfilePicture() {
        return mSharedPreferences.getString(FACEBOOK_PROFILE_PIC_URL, "");
    }

    public void setFacebookProfilePicture(String profilePicUrl) {
        mSharedPreferences.edit().putString(FACEBOOK_PROFILE_PIC_URL, profilePicUrl).commit();
    }

    public String getFacebookEmailId() {
        return mSharedPreferences.getString(FACEBOOK_EMAIL_ID, "");
    }

    public void setFacebookEmailId(String emailId) {
        mSharedPreferences.edit().putString(FACEBOOK_EMAIL_ID, emailId).commit();
    }

    public String getFacebookName() {
        return mSharedPreferences.getString(FACEBOOK_NAME, "");
    }

    public void setFacebookName(String emailId) {
        mSharedPreferences.edit().putString(FACEBOOK_NAME, emailId).commit();
    }

    public String getFacebookCoverPic() {
        return mSharedPreferences.getString(FACEBOOK_COVER_PIC, "");
    }

    public void setFacebookCoverPic(String coverPic) {
        mSharedPreferences.edit().putString(FACEBOOK_COVER_PIC, coverPic).commit();
    }

    public String getUserHeight() {
        return mSharedPreferences.getString(HEIGHT, "");
    }

    public void setUserHeight(String height) {
        mSharedPreferences.edit().putString(HEIGHT, height).commit();
    }

    public String getUserWeight() {
        return mSharedPreferences.getString(WEIGHT, "");
    }

    public void setUserWeight(String weight) {
        mSharedPreferences.edit().putString(WEIGHT, weight).commit();
    }

    public String getUserBloodGroup() {
        return mSharedPreferences.getString(BLOOD_GROUP, "");
    }

    public void setUserBloodGroup(String bloodGroup) {
        mSharedPreferences.edit().putString(BLOOD_GROUP, bloodGroup).commit();
    }

    public String getUserZipCode() {
        return mSharedPreferences.getString(ZIP_CODE, "");
    }

    public void setUserZipCode(String zipcode) {
        mSharedPreferences.edit().putString(ZIP_CODE, zipcode).commit();
    }

    public String getUserStreet() {
        return mSharedPreferences.getString(STREET, "");
    }

    public void setUserStreet(String street) {
        mSharedPreferences.edit().putString(STREET, street).commit();
    }

    public String getUserNotes() {
        return mSharedPreferences.getString(MED_NOTES, "");
    }

    public void setUserNotes(String notes) {
        mSharedPreferences.edit().putString(MED_NOTES, notes).commit();
    }

    public String getUserState() {
        return mSharedPreferences.getString(MED_NOTES, "");
    }

    public void setUserState(String state) {
        mSharedPreferences.edit().putString(STATE, state).commit();
    }

    public String getMedicalSpecialist() {
        return mSharedPreferences.getString(MEDICAL_SPECIALIST, "");
    }

    public void setMedicalSpecialist(String specialist) {
        mSharedPreferences.edit().putString(MEDICAL_SPECIALIST, specialist).commit();
    }

    public String getSelectedDoctor() {
        return mSharedPreferences.getString(DOCTOR, "");
    }

    public void setSelectedDoctor(String doctor) {
        mSharedPreferences.edit().putString(DOCTOR, doctor).commit();
    }

    public String getStripeKey() {
        return mSharedPreferences.getString(STRIPE_KEY, "");
    }

    public void setStripKey(String key) {
        mSharedPreferences.edit().putString(STRIPE_KEY, key).commit();
    }

    public String getEmergencyTag() {
        return mSharedPreferences.getString(EMERGENCY_TAG, "");
    }

    public void setEmergencyTag(String tag) {
        mSharedPreferences.edit().putString(EMERGENCY_TAG, tag).commit();
    }

    public String getHistoryID() {
        return mSharedPreferences.getString(HISTORY_ID, "");
    }

    public void setHistoryID(String id) {
        mSharedPreferences.edit().putString(HISTORY_ID, id).commit();
    }

    public String getCardNumber() {
        return mSharedPreferences.getString(CARD_NUMBER, "");
    }

    public void setCardNumber(String cardNumber) {
        mSharedPreferences.edit().putString(CARD_NUMBER, cardNumber).commit();
    }


    public int getCardExpiryMonth() {
        return mSharedPreferences.getInt(CARD_EXPIRY_MONTH, 0);
    }

    public void setCardExpiryMonth(int cardExpiryMonth) {
        mSharedPreferences.edit().putInt(CARD_EXPIRY_MONTH, cardExpiryMonth).commit();
    }

    public int getcardExpiryYear() {
        return mSharedPreferences.getInt(CARD_EXPIRY_YEAR, 0);
    }

    public void setcardExpiryYear(int cardExpiryYear) {
        mSharedPreferences.edit().putInt(CARD_EXPIRY_YEAR, cardExpiryYear).commit();
    }

    public int getcardExpiryCvv() {
        return mSharedPreferences.getInt(CARD_CVV, 0);
    }

    public void setcardExpiryCvv(int cvv) {
        mSharedPreferences.edit().putInt(CARD_CVV, cvv).commit();
    }

    public boolean isCardDetailsAvailable() {
        return mSharedPreferences.getBoolean(IsCardDetailAvailble, false);
    }

    public void setCardDetailsAvailable(boolean flag) {
        mSharedPreferences.edit().putBoolean(IsCardDetailAvailble, flag).commit();
    }
}
