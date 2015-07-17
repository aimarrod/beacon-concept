package com.olbalabs.beaconconcept.domain;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class Beacon {

    String uuid;
    String mayor;
    String minor;
    boolean checked = false;

    public Beacon() {
        super();
    }

    public Beacon(String uuid, String mayor, String minor) {
        super();
        this.uuid = uuid;
        this.mayor = mayor;
        this.minor = minor;
    }

    public Beacon(String uuid, String mayor, String minor, boolean b) {
        super();
        this.uuid = uuid;
        this.mayor = mayor;
        this.minor = minor;
        this.checked = b;
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

    public Region getRegion(){
        return new Region("MyRegion", Identifier.parse(getUuid()), Identifier.parse(getMayor()), Identifier.parse(getMinor()));
    };

    public boolean isChecked(){
        return checked;
    }
}
