package com.around.engineerbuddy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class BMAGson {

    static BMAGson instance;
    Gson gson;
    Gson gsonExcAnnotation;

    private BMAGson() {
        this.init();
    }

    public static BMAGson store() {
        if (instance == null) {
            instance = new BMAGson();
        }
        return instance;
    }

    private void init() {
        this.gson = new GsonBuilder().create();
    }

    public String toJson(Object obj) {
        return this.gson.toJson(obj);
    }

    public <T> T getObject(Class<T> entity, Object response) {
        if (response==null) {
            return null;
        }
        try {
            String jsonString = response instanceof Map ? this.toJson(response) : BMAJsonHelper.toJSON(response).toString();
            return jsonString!=null && jsonString.trim().length()!=0 ? this.gson.fromJson(jsonString, entity) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> ArrayList<T> getList(Class<T> entity, Object response) {
        if (response == null) {
            return new ArrayList<>();
        }
        ArrayList<T> returnList = new ArrayList<>();
        try {
            String jsonsString = response instanceof Iterable ? this.toJson(response) : BMAJsonHelper.toJSON(response).toString();
            ArrayList<Object> list = this.gson.fromJson(jsonsString, new TypeToken<ArrayList<T>>() {
            }.getType());
            for (Object obj : list) {
                returnList.add(this.getObject(entity, obj));
            }
        } catch (Exception e) {
           // FNExceptionUtil.logException(e, FNApplicationHelper.application().getContext(), true);
        }
        return returnList;
    }

    public <T> ArrayList<T> getList(Object response) {
        if (response == null) {
            return new ArrayList<>();
        }
        try {
            return this.gson.fromJson(BMAJsonHelper.toJSON(response).toString(), new TypeToken<ArrayList<T>>() {
            }.getType());
        } catch (Exception e) {
           // FNExceptionUtil.logException(e, FNApplicationHelper.application().getContext(), true);
            return null;
        }
    }


}
