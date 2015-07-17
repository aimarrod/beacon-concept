package com.olbalabs.beaconconcept.domain;

/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class Beacon {

    String uuid;
    String mayor;
    String minor;

    public Beacon() {
        super();
    }

    public Beacon(String uuid, String mayor, String minor) {
        super();
        this.uuid = uuid;
        this.mayor = mayor;
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMayor() {
        return mayor;
    }

    public void setMayor(String mayor) {
        this.mayor = mayor;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
