package com.around.engineerbuddy.util;

import java.util.HashMap;
import java.util.Map;

public enum BMAEnum   {

    HOME_REQUEST_ICON("HOME_REQUEST_ICON"), YEARLY("YEARLY"), MONTHLY("MONTHLY"), WEEKLY("WEEKLY"), DAILY("DAILY"), TWO_WEEK("TWO_WEEK"), DROPBOX("Dropbox"), GOOGLE_DRIVE("Google Drive"), AMAZON("Amazon Cloud Drive"), UPLOAD("UPLOAD"), DOWNLOAD(
            "DOWNLOAD"), DELETE("DELETE"), FILE("FILE"), IMAGE("IMAGE"), VIDEO("VIDEO"), AUDIO("AUDIO"), WORD("WORD"), EXCEL("EXCEL"), PPT("PPT"), TEXT("TEXT"), PDF("PDF"), ZIP("ZIP"), FILE_UNKNOWN("FILE_UNKNOWN"), ALL(
            "All"), ALL_RECIPIENTS("ALL_RECIPIENTS"), ALL_SUPERVISOR("ALL_SUPERVISOR"), CAMERA("CAMERA"), ALL_MANAGER("ALL_MANAGER"), ALL_CREW("ALL_CREW"), OWNER("OWNER"), ADMIN("ADMIN"), MANAGER("MANAGER"), SUPERVISOR("SUPERVISOR"), CREW(
            "CREW"), PLAY("PLAY"), PAUSE("PAUSE"), SIGNUP_OPTION("SIGNUP_OPTION"), OWNER_SIGNUP("OWNER_SIGNUP"), EMP_SIGNUP("EMP_SIGNUP"), EMP_SIGNUP_SUCCESS("EMP_SIGNUP_SUCCESS"), RECT_SHAPE("RECT_SHAPE"), OVAL_SHAPE(
            "OVAL_SHAPE"), ROUND_CORNER("ROUND_CORNER"), RING_SHAPE("RING_SHAPE"), FONT_REGULAR("FONT_REGULAR"), FONT_BOLD("FONT_BOLD"), FONT_ITALIC("FONT_ITALIC"), FONT_BOLD_ITALIC(
            "FONT_BOLD_ITALIC"), FONT_SEMIBOLD("FONT_SEMIBOLD"), FONT_SEMIBOLD_ITALIC("FONT_SEMIBOLD_ITALIC"), FONT_LIGHT_ITALIC("FONT_LIGHT_ITALIC"), FONT_EXTRALIGHT_ITALIC("FONT_EXTRALIGHT_ITALIC");

    private static final Map<String, BMAEnum> fnEnumMap = new HashMap<>();

    static {
        for (BMAEnum s : BMAEnum.values()) {
            fnEnumMap.put(s.text, s);
        }
    }

    private final String text;

    BMAEnum(final String text) {
        this.text = text;
    }

    public static BMAEnum fromID(String id) {
        return fnEnumMap.get(id);
    }

    @Override
    public String toString() {
        return this.text;
    }

    public boolean isEqual(String status) {
        return status != null && status.equals(this.text);
    }

    public boolean isSame(BMAEnum status) {
        return status != null && isEqual(status.toString());
    }



}
