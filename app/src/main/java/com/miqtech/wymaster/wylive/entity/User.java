package com.miqtech.wymaster.wylive.entity;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xiaoyi on 2016/8/15.
 */
public class User implements Serializable {


    private String id;

    private String username;

    private String icon;

    private String iconMedia;

    private String iconThumb;

    private String telephone;

    private int coin;

    private String speech;

    private String nickname;

    private String cityCode;

    private String cityName;

    private String token;

    private int profileStatus;

    private int isReserve;

    private String bgImg;

    private int isPasswordNull;

    private int isUpdated;

    private int isUp;

    private int fans ;

    private int sex;


    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setIcon(String icon){
        this.icon = icon;
    }
    public String getIcon(){
        return this.icon;
    }
    public void setIconMedia(String iconMedia){
        this.iconMedia = iconMedia;
    }
    public String getIconMedia(){
        return this.iconMedia;
    }
    public void setIconThumb(String iconThumb){
        this.iconThumb = iconThumb;
    }
    public String getIconThumb(){
        return this.iconThumb;
    }
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    public String getTelephone(){
        return this.telephone;
    }
    public void setCoin(int coin){
        this.coin = coin;
    }
    public int getCoin(){
        return this.coin;
    }
    public void setSpeech(String speech){
        this.speech = speech;
    }
    public String getSpeech(){
        return this.speech;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }
    public void setCityCode(String cityCode){
        this.cityCode = cityCode;
    }
    public String getCityCode(){
        return this.cityCode;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public String getCityName(){
        return this.cityName;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }
    public void setProfileStatus(int profileStatus){
        this.profileStatus = profileStatus;
    }
    public int getProfileStatus(){
        return this.profileStatus;
    }
    public void setIsReserve(int isReserve){
        this.isReserve = isReserve;
    }
    public int getIsReserve(){
        return this.isReserve;
    }
    public void setBgImg(String bgImg){
        this.bgImg = bgImg;
    }
    public String getBgImg(){
        return this.bgImg;
    }
    public void setIsPasswordNull(int isPasswordNull){
        this.isPasswordNull = isPasswordNull;
    }
    public int getIsPasswordNull(){
        return this.isPasswordNull;
    }
    public void setIsUpdated(int isUpdated){
        this.isUpdated = isUpdated;
    }
    public int getIsUpdated(){
        return this.isUpdated;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}
