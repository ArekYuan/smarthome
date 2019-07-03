package cn.interlinx.entity;

import java.io.Serializable;

public class Device implements Serializable {
    private Integer deviceId;

    private Integer userid;

    private String smallImgurl = "";

    private String largeImgurl = "";

    private String desc = "";

    private Integer takeOff;

    private Integer mode;

    private Integer lock;

    private Integer windPower;

    private Integer airLamp;

    private Integer timingSet;

    private Integer waterLevel;

    private Integer status;

    private String lat = "";

    private String lng = "";

    private Integer altitude;

    private String device_mac = "";

    private String wifi_mac = "";


    public String getDevice_mac() {
        return device_mac;
    }

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    private static final long serialVersionUID = 1L;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getSmallImgurl() {
        return smallImgurl;
    }

    public void setSmallImgurl(String smallImgurl) {
        this.smallImgurl = smallImgurl == null ? null : smallImgurl.trim();
    }

    public String getLargeImgurl() {
        return largeImgurl;
    }

    public void setLargeImgurl(String largeImgurl) {
        this.largeImgurl = largeImgurl == null ? null : largeImgurl.trim();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }

    public Integer getTakeOff() {
        return takeOff;
    }

    public void setTakeOff(Integer takeOff) {
        this.takeOff = takeOff;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    public Integer getWindPower() {
        return windPower;
    }

    public void setWindPower(Integer windPower) {
        this.windPower = windPower;
    }

    public Integer getAirLamp() {
        return airLamp;
    }

    public void setAirLamp(Integer airLamp) {
        this.airLamp = airLamp;
    }

    public Integer getTimingSet() {
        return timingSet;
    }

    public void setTimingSet(Integer timingSet) {
        this.timingSet = timingSet;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat == null ? null : lat.trim();
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng == null ? null : lng.trim();
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public String getWifi_mac() {
        return wifi_mac;
    }

    public void setWifi_mac(String wifi_mac) {
        this.wifi_mac = wifi_mac;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceId=").append(deviceId);
        sb.append(", userid=").append(userid);
        sb.append(", smallImgurl=").append(smallImgurl);
        sb.append(", largeImgurl=").append(largeImgurl);
        sb.append(", desc=").append(desc);
        sb.append(", takeOff=").append(takeOff);
        sb.append(", mode=").append(mode);
        sb.append(", lock=").append(lock);
        sb.append(", windPower=").append(windPower);
        sb.append(", airLamp=").append(airLamp);
        sb.append(", timingSet=").append(timingSet);
        sb.append(", waterLevel=").append(waterLevel);
        sb.append(", status=").append(status);
        sb.append(", lat=").append(lat);
        sb.append(", lng=").append(lng);
        sb.append(", altitude=").append(altitude);
        sb.append(", device_mac=").append(device_mac);
        sb.append(", wifi_mac=").append(wifi_mac);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}