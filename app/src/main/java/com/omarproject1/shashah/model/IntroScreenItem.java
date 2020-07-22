package com.omarproject1.shashah.model;

public class IntroScreenItem {

    String introTitle,introDescription;
    int introImage;

    public IntroScreenItem() {
    }

    public IntroScreenItem(String introTitle, String introDescription, int introImage) {
        this.introTitle = introTitle;
        this.introDescription = introDescription;
        this.introImage = introImage;
    }

    public String getIntroTitle() {
        return introTitle;
    }

    public void setIntroTitle(String introTitle) {
        this.introTitle = introTitle;
    }

    public String getIntroDescription() {
        return introDescription;
    }

    public void setIntroDescription(String introDescription) {
        this.introDescription = introDescription;
    }

    public int getIntroImage() {
        return introImage;
    }

    public void setIntroImage(int introImage) {
        this.introImage = introImage;
    }
}
