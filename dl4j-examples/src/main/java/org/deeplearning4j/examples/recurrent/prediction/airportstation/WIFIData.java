package org.deeplearning4j.examples.recurrent.prediction.airportstation;

/**
 * Created by Yukai Ji on 2016/12/2.
 */
public class WIFIData {

    //wifi ID
    private String wifiId;
    //乘客数量
    private double passengerCount;
    //航站楼乘客数量
    private double gatePassengerCount;
    //签到乘客数量
    private double checkinCount;
    //安检乘客数量
    private double securityCount;
    //航班数量
    private double flightCount;
    //站台楼
    private String gate;

    public WIFIData() {
    }

    public String getWifiId() {
        return wifiId;
    }

    public void setWifiId(String wifiId) {
        this.wifiId = wifiId;
    }

    public double getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(double passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getGatePassengerCount() {
        return gatePassengerCount;
    }

    public void setGatePassengerCount(double gatePassengerCount) {
        this.gatePassengerCount = gatePassengerCount;
    }

    public double getCheckinCount() {
        return checkinCount;
    }

    public void setCheckinCount(double checkinCount) {
        this.checkinCount = checkinCount;
    }

    public double getSecurityCount() {
        return securityCount;
    }

    public void setSecurityCount(double securityCount) {
        this.securityCount = securityCount;
    }

    public double getFlightCount() {
        return flightCount;
    }

    public void setFlightCount(double flightCount) {
        this.flightCount = flightCount;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("WIFI Id="+this.wifiId+", ");
        builder.append("乘客数量="+this.passengerCount+", ");
        builder.append("航站楼乘客数量="+this.gatePassengerCount+", ");
        builder.append("签到乘客数量="+this.checkinCount+", ");
        builder.append("安检乘客数量="+this.securityCount+", ");
        builder.append("航班数量="+this.flightCount+", ");
        builder.append("站台楼="+this.gate);
        return builder.toString();
    }
}
