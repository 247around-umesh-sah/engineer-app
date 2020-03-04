package com.around.engineerbuddy.entity;

// This entity class is used to some specific declare data key of related all object
public class BMAUiEntity extends BMAObject {


    public Long primaryKey;
    public Object tag;
    public Object extraParam;
    private long parentPK;
    private int weight;
    private String detail1;
    private String detail2;
    private String detail3;
    private String detail4;
    private String detail5;
    private String objectType;
    private String title;
    private String parentTitle;
    private boolean willExplore;
    private boolean isAdjViewDisabled;
    private boolean isChecked = false;
    private Object rowObject;

    public BMAUiEntity() {
    }

    public boolean isHeader() {
        return false;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParentTitle() {
        return this.parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getDetail1() {
        return this.detail1;
    }

    public void setDetail1(String detail1) {
        this.detail1 = detail1;
    }

    public String getDetail5() {
        return this.detail5;
    }

    public void setDetail5(String detail5) {
        this.detail5 = detail5;
    }

    public String getDetail2() {
        return this.detail2;
    }

    public void setDetail2(String detail2) {
        this.detail2 = detail2;
    }

    public String getDetail3() {
        return this.detail3;
    }

    public void setDetail3(String detail3) {
        this.detail3 = detail3;
    }

    public String getDetail4() {
        return this.detail4;
    }

    public void setDetail4(String detail4) {
        this.detail4 = detail4;
    }

    public long getParentPK() {
        return this.parentPK;
    }

    public void setParentPK(long parentPK) {
        this.parentPK = parentPK;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public long getPrimaryKey() {
        return this.primaryKey != null ? this.primaryKey : 0;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isWillExplore() {
        return this.willExplore;
    }

    public void setWillExplore(boolean willExplore) {
        this.willExplore = willExplore;
    }

    public boolean isAdjViewDisabled() {
        return this.isAdjViewDisabled;
    }

    public void setAdjViewDisabled(boolean isAdjViewDisabled) {
        this.isAdjViewDisabled = isAdjViewDisabled;
    }

    public boolean isEqual(BMAUiEntity detail) {
        return detail != null && detail.getPrimaryKey() == this.getPrimaryKey();
    }

    public Object getRowObject() {
        return rowObject;
    }

    public void setRowObject(Object rowObject) {
        this.rowObject = rowObject;
    }

    public boolean extraParamBoolValue() {
        return extraParam instanceof Boolean && (Boolean) extraParam;
    }

    public long extraParamLongValue() {
        return extraParam instanceof Long ? (Long) extraParam : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (tag != null && obj instanceof BMAUiEntity) {
            return tag.equals(((BMAUiEntity) obj).tag);
        }
        return super.equals(obj);
    }

    // This is custome method with one argument of title as string to set title in object.
    public static BMAUiEntity uiEntity(String title) {
        BMAUiEntity entity = new BMAUiEntity();
        entity.setTitle(title);
        return entity;
    }

}
