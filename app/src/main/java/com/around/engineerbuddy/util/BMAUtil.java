package com.around.engineerbuddy.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.android.gms.common.util.DeviceProperties;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class BMAUtil {
//    public static boolean isAlpha(String name) {
//        return FNObjectUtil.isNonEmptyStr(name) && name.matches("[a-zA-Z]+");
//    }

    /**
    // * @param emailId
     * @return true if emailID is in correct format
     */
//    public static boolean isValidEmail(String emailId) {
//        return FNObjectUtil.isNonEmptyStr(emailId) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
//    }
//
//    public static String encodeObjectTobase64(Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof Bitmap) {
//            return encodeTobase64((Bitmap) obj);
//        } else if (obj instanceof File) {
//            return encodeFileTobase64((File) obj);
//        }
//        return obj.toString();
//    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

//    public static String encodeFileTobase64(File file) {
//        try {
//            return Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String decodeStringFromBase64(String input) {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return new String(decodedByte);
    }

//    /**
//     * Sort list containing object on the basis of key.
//     *
//     * @param array
//     * @param key
//     * @param isAsc
//     */
//    public static <T> void sortArray(List<T> array, String key, boolean isAsc) {
//        FNSortOrdering[] sortOrderArray = FNSortOrderingUtil.create(key,
//                isAsc ? "ASC" : "DESC");
//        FNSortOrderingUtil.sort(array, sortOrderArray);
//    }

    public static int getDipFromPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

//    public static boolean isNetworkAvailable(Context context) {
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//        } catch (Exception e) {
//            FNExceptionUtil.logException(e);
//            return false;
//        }
//    }

//    public static String stripSeparators(String phoneNumber) {
//        String returnStr = "";
//        if (FNObjectUtil.isEmptyStr(phoneNumber)) {
//            return returnStr;
//        }
//        for (Character c : phoneNumber.toCharArray()) {
//            if (Character.isDigit(c) || c.toString().equals("+")) {
//                returnStr += c;
//            }
//        }
//        return returnStr;
//    }

//    public static void resetDeviceData(Context context) {
//        resetDeviceData(context, true);
//    }

//    public static void resetDeviceData(Context context, boolean resetPref) {
//        resetDeviceData(context, resetPref, FNSplashScreenFactory.activity().getClass());
//    }

//    public static <T> void resetDeviceData(Context context, boolean resetPref, Class<T> activityClass) {
//        Activity activity = FNApplicationHelper.application().getActivity();
//        boolean isLoginActivity = activity instanceof FNLoginActivity;
//        boolean isSplashScreen = activity instanceof FNSplashScreen;
//        if (resetPref) {
//            FNApplicationHelper.application().resetSessionData();
//            FNApplicationHelper.application().deleteLoginInfo();
//        }
//        FNApplicationHelper.application().cancelAllNotifications();
//        if (isSplashScreen) {
//            ((FNSplashScreen) activity).onErrorOccured();
//        } else if (!isLoginActivity) {
//            Intent intent = new Intent(context, activityClass);
//            intent.putExtra("isError", true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            context.startActivity(intent);
//        }
//    }

//    public static String appNameWithoutSpace(Context context) {
//        return appName(context).replaceAll(" ", "");
//    }

  //  public static String appName(Context context) {
//        return context.getString(R.string.app_name);
//    }

    public static String to2Decimal(double value, double divider) {
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(value / divider);
    }

    public static String to2Decimal(double value) {
        return to2Decimal(value, 100.0);
    }
//
//    /**
//     * Sets String resource to the given {@link TextView}. If the resource could
//     * not be resolved with the given name, It sets the given resourceName as
//     * text to the {@code TextView}
//     *
//     * @param resourceName
//     *            [{@code String}]
//     * @param destinationTextView
//     *            [{@code TextView}]
//     */
////    public static void setTextFromResourceName(String resourceName, TextView destinationTextView) {
//        setTextFromResourceName(resourceName, destinationTextView, resourceName);
//    }

//    /**
//     * Sets String resource to the given {@link TextView}. If the resource could
//     * not be resolved with the given name, It sets the given optionalString to
//     * the {@code TextView}
//     *
//     * @param resourceName
//     *            [{@code String}]
//     * @param destinationTextView
//     *            [{@code TextView}]
//     * @param optionalString
//     *            - Optional String to be set on {@code TextView} in case of
//     *            wrong / unavailable resource name. Can be null.
//     */
//    public static void setTextFromResourceName(String resourceName, TextView destinationTextView, String optionalString) {
//        int resoureId = FNStringUtil.getIdForName(resourceName);
//        if (resoureId != 0) {
//            destinationTextView.setText(resoureId);
//        } else {
//            destinationTextView.setText(optionalString);
//        }
//    }
//
//    public static String unicodeToString(String unicodeString) {
//        return StringEscapeUtils.unescapeXml(unicodeString);
//    }

//    public static String formatPhoneNumber(String phoneNumber) {
//        if (FNObjectUtil.isEmptyStr(phoneNumber)) {
//            return phoneNumber;
//        }
//        String formattedNumber = "";
//        int startPosition = 0;
//        int endPosition = 0;
//        for (Character c : FNApplicationHelper.application().phoneFormat().toCharArray()) {
//            if (startPosition >= phoneNumber.length()) {
//                break;
//            }
//            if (Character.isLetter(c)) {
//                endPosition = startPosition + 1;
//                formattedNumber += phoneNumber.substring(startPosition, endPosition);
//                startPosition++;
//                continue;
//            }
//            formattedNumber += phoneNumber.substring(startPosition, endPosition) + c;
//        }
//        if (phoneNumber.length() > endPosition) {
//            formattedNumber += phoneNumber.substring(endPosition, phoneNumber.length());
//        }
//        return formattedNumber;
//    }

//    public static NumberFormat currencyFormat() {
//        Locale locale = new Locale(FNConstants.defaultLangId, FNApplicationHelper.application().countryCode());
//        Currency currencyInstance = Currency.getInstance(locale);
//        NumberFormat currenyFormat = NumberFormat.getCurrencyInstance(locale);
//        currenyFormat.setCurrency(currencyInstance);
//        return currenyFormat;
//    }

//    public static String formatCurrency(String amountStr, boolean convertToDollars, boolean returnZero) {
//        return formatCurrency(amountStr, convertToDollars, returnZero, false);
//    }

//    public static String formatCurrency(String amountStr, boolean convertToDollars, boolean returnZero, boolean isNegativeAllow) {
//        if (FNObjectUtil.isEmptyStr(amountStr)) {
//            amountStr = "0";
//        }
//        BigDecimal parsed = new BigDecimal(0);
//        try {
//            parsed = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_CEILING);
//            if (convertToDollars) {
//                parsed = parsed.divide(new BigDecimal(100), BigDecimal.ROUND_CEILING);
//            }
//        } catch (Exception e) {
//            FNExceptionUtil.logException(e);
//        }
//        return (returnZero || parsed.floatValue() > 0 || isNegativeAllow) ? currencyFormat().format(parsed) : "";
//    }
//
//    public static Number currencyToNumber(String amountStr) {
//        try {
//            return currencyFormat().parse(amountStr);
//        } catch (ParseException e) {
//            try {
//                return Float.parseFloat(FNObjectUtil.isNonEmptyStr(amountStr) ? amountStr : "0");
//            } catch (Exception e1) {
//            }
//        }
//        return 0;
//    }
//
//    public static String remCurrencyCode(String amountStr) {
//        return FNObjectUtil.isEmptyStr(amountStr) ? amountStr : amountStr.substring(FNApplicationHelper.application().currenyCode().length(), amountStr.length());
//    }

//    public static String formatCurrency(String amountStr, boolean convertToDollars) {
//        return formatCurrency(amountStr, convertToDollars, false);
//    }

//    public static String formatCurrencyWithoutSymbol(String amountStr, boolean convertToDollars) {
//        return remCurrencyCode(formatCurrency(amountStr, convertToDollars));
//    }
//
//    public static Long stringToNumber(String numberStr) {
//        try {
//            return Long.parseLong(FNObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
//        } catch (Exception e1) {
//            return 0L;
//        }
//    }
//
//    public static Float stringToFloat(String numberStr) {
//        try {
//            return Float.parseFloat(FNObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
//        } catch (Exception e1) {
//            return 0f;
//        }
//    }

//    public static Double stringToDouble(String numberStr) {
//        try {
//            return Double.parseDouble(FNObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
//        } catch (Exception e1) {
//            return 0.0;
//        }
//    }

//    public static void addCalendarEvent(Activity activity, FNTimestamp startTime, FNTimestamp endTime, String title) {
//        long beginTimeMs = new FNTimestamp(startTime.getTime()).getTime();
//        long endTimeMs = new FNTimestamp(endTime.getTime()).getTime();
//        if (Build.VERSION.SDK_INT >= 14) {
//            Intent intent = new Intent(Intent.ACTION_INSERT);
//            intent.setData(CalendarContract.Events.CONTENT_URI);
//            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMs);
//            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMs);
//            intent.putExtra(CalendarContract.Events.TITLE, title);
//            activity.startActivity(intent);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_EDIT);
//            intent.setType("vnd.android.cursor.item/event");
//            intent.putExtra("beginTime", beginTimeMs);
//            intent.putExtra("endTime", endTimeMs);
//            intent.putExtra("title", title);
//            activity.startActivity(intent);
//        }
//    }

    public static void doCall(Context context, long phoneNumber) {
        doCall(context, String.valueOf(phoneNumber));
    }

    public static void doCall(Context context, String phoneNumber) {
        if (BMAObjectUtil.isNonEmptyStr(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }

    public static String capitalize(String s) {
        if (BMAObjectUtil.isEmptyStr(s)) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String stringForLengthAndChar(int length, char defaultChar) {
        String resultString = "";
        for (int i = 0; i < length; i++) {
            resultString += defaultChar;
        }
        return resultString;
    }

    public static int calculatePercentage(float value, float total) {
        return (int) calculatePercentage(value, total, 0);
    }

    public static float calculatePercentage(float value, float total, int scale) {
        float result = total != 0 ? (value / total) * 100 : 0;
        return result > 0 ? BigDecimal.valueOf(result).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue() : 0;
    }

//    public static TextWatcher validEmailChecker(FNFontViewField validEmailIndicator) {
//        TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (validEmailIndicator == null) {
//                    return;
//                }
//                if (FNObjectUtil.isNonEmptyStr(s.toString())) {
//                    boolean isValidEmail = isValidEmail(s.toString());
//                    validEmailIndicator.setText(isValidEmail ? R.string.icon_check_cricle : R.string.icon_delete_circle);
//                    validEmailIndicator.setTextColor(FNUIUtil.getColor(isValidEmail ? R.color.green1 : R.color.darkRed));
//                    validEmailIndicator.setVisibility(View.VISIBLE);
//                } else {
//                    validEmailIndicator.setVisibility(View.GONE);
//                }
//            }
//        };
//        return textWatcher;
//    }

//    public static void showHidePassword(FNEditText passwordView, FNFontViewField showPasswordIndicator) {
//        passwordView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (showPasswordIndicator == null) {
//                    return;
//                }
//                showPasswordIndicator.setVisibility(FNObjectUtil.isNonEmptyStr(s.toString()) ? View.VISIBLE : View.GONE);
//            }
//        });
//        showPasswordIndicator.setOnTouchListener((v, event) -> {
//            passwordView.setTransformationMethod(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN ? null : new PasswordTransformationMethod());
//            passwordView.setSelection(passwordView.getText().length());
//            return true;
//        });
//    }
//
//    public static boolean isOverNightShift(int index) {
//        int increment = FNApplicationHelper.application().getResources().getInteger(R.integer.end_index_increment);
//        return index >= 96 - increment;
//    }

    public static boolean isParsableNumber(String aValue) {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        try {
            format.parse(aValue).floatValue();
            return true;
        } catch (ParseException e) {
            Float.parseFloat(aValue);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

//    public static void startCamera(Activity activity) {
//        FNFilePicker.options(activity)
//                .addMedia(MEDIA_TYPE_IMAGE)
//                .returnAfterFirst(true) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
//                .single() // multi mode (default mode)
//                .setCameraOnly(true)
//                .imageDirectory(appNameWithoutSpace(activity)) // captured image directory name ("Camera" folder by default)
//                .start(FNReqResCode.ATTACHMENT_REQUEST); // start image picker activity with request code
//    }

//    public static void startImagePicker(Activity activity) {
//        startImagePicker(activity, true);
//    }

//    public static void startImagePicker(Activity activity, boolean isReturnAfterFirst) {
//        FNFilePicker.options(activity)
//                .addMedia(MEDIA_TYPE_IMAGE)
//                .returnAfterFirst(isReturnAfterFirst) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
//                .single() // multi mode (default mode)
//                .imageDirectory(appNameWithoutSpace(activity)) // captured image directory name ("Camera" folder by default)
//                .start(FNReqResCode.ATTACHMENT_REQUEST); // start image picker activity with request code
//    }
//
//    public static void startFilePicker(Activity activity, int limit, long sizeLimit) {
//        FNFilePicker.options(activity)
//                .addMedia(MEDIA_TYPE_IMAGE)
//                .addMedia(MEDIA_TYPE_VIDEO)
//                .addMedia(MEDIA_TYPE_AUDIO)
//                .addMedia(MEDIA_TYPE_DOCUMENT)
//                .returnAfterFirst(false)
//                .limit(limit)
//                .sizeLimit(sizeLimit)
//                .imageDirectory(appNameWithoutSpace(activity)) // captured image directory name ("Camera" folder by default)
//                .starot(FNReqResCode.ATTACHMENT_REQUEST); // start image picker activity with request code
//
//    }

    /**
     *
     * @param context
     * @return a boolean whether a given current device resource is a tablet or
     *         not.
     */
    public static boolean isTablet(Context context) {
        return DeviceProperties.isTablet(context.getResources());
    }

//    public static ArrayList<Object> getSortedObjectArray(List<?> objects) {
//        return getSortedObjectArray(objects, true);
//    }

//    public static ArrayList<Object> getSortedObjectArray(List<?> objects, boolean isAlphaHeader) {
//        return getSortedObjectArray(objects, isAlphaHeader, FNConstants.SPECIAL_CHAR_REGEX);
//    }
//
//    public static ArrayList<Object> getSortedObjectArray(List<?> objects, boolean isAlphaHeader, String pattern) {
//        ArrayList<Object> objectsList = new ArrayList<>();
//        String previousContactName = null;
//        Collections.sort(objects, new FNEmpNameComparator());
//        if (!isAlphaHeader) {
//            return (ArrayList<Object>) objects;
//        }
//        for (Object o : objects) {
//            String initial = o.toString().substring(0, 1).toLowerCase(Locale.US).matches(pattern) ? "#" : o.toString().substring(0, 1).toUpperCase(Locale.US);
//            if (FNObjectUtil.isEmptyStr(previousContactName) || !(previousContactName.equals(initial))) {
//                FNUIEntityHeader fnuiEntityHeader = new FNUIEntityHeader();
//                fnuiEntityHeader.setTitle(initial);
//                previousContactName = initial;
//                objectsList.add(fnuiEntityHeader);
//            }
//            objectsList.add(o);
//        }
//        return objectsList;
//    }

    /**
     *
     * @param objUrl
     *            image url
     * @return a boolean whether a given url is a valid one to show alpha
     *         characters on image.
     */
//    public static boolean isAlphaImg(String objUrl) {
//        return FNObjectUtil.isEmptyStr(objUrl) || (FNObjectUtil.isNonEmptyStr(objUrl) && (objUrl.endsWith("/avatar.png") || objUrl.endsWith("Default.png")));
//    }
//
   /**
     *
   //  * @param imageText
     *            text of whom initials are going to be use in alpha image
     * @return returns the initials of a given imageText i.e. First Character of
     *         a first word and last word.
     */
//    public static String imageInitials(@Nullable String imageText) {
//        if (FNObjectUtil.isEmptyStr(imageText)) {
//            return "";
//        }
//        imageText = imageText.split(" ").length > 1 ? imageText.substring(0, 1).toUpperCase().concat(imageText.split(" ")[imageText.split(" ").length - 1].substring(0, 1).toUpperCase()) : imageText.substring(0, 1).toUpperCase();
//        return imageText;
//    }


    public static String getTitleString(String title){
        if(title==null || title.trim().length()==0){
            return "";
        }
        StringBuffer stringBuffer=new StringBuffer(title);
        stringBuffer.replace(0,1,String.valueOf(title.charAt(0)).toUpperCase());
        return stringBuffer.toString();



    }
}
