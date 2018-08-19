package cc.dmji.api.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IpSbResp {
    @JsonProperty("offset")
    private String offset;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("city")
    private String city;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("area_code")
    private String areaCode;
    @JsonProperty("region")
    private String region;
    @JsonProperty("dma_code")
    private String dmaCode;
    @JsonProperty("organization")
    private String organization;
    @JsonProperty("country")
    private String country;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("country_code3")
    private String countryCode3;
    @JsonProperty("continent_code")
    private String continentCode;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("region_code")
    private String regionCode;

    public IpSbResp() {
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDmaCode() {
        return dmaCode;
    }

    public void setDmaCode(String dmaCode) {
        this.dmaCode = dmaCode;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountryCode3() {
        return countryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        this.countryCode3 = countryCode3;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
