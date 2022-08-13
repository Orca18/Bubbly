package com.example.bubbly.model;

public class ExampleItem {
    private String mImageResource;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;



    //TODO 필요한 형식으로 바꾸세요!
    public ExampleItem(String imageResource, String text1, String text2, String text3, String text4) {
        this.mImageResource = imageResource;
        this.mText1 = text1;
        this.mText2 = text2;
        this.mText3 = text3;
        this.mText4 = text4;
    }

    public String getImageResource() {
        return this.mImageResource;
    }

    public String getText1() {
        return this.mText1;
    }

    public String getText2() {
        return this.mText2;
    }

    public String getText3() {
        return this.mText3;
    }

    public String getText4() {
        return this.mText4;
    }
}