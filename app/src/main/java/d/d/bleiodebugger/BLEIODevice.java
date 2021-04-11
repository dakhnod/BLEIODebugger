package d.d.bleiodebugger;

import androidx.annotation.Nullable;

public class BLEIODevice {
    private String address;
    boolean contant;
    int count;
    int batteryPercent;

    public BLEIODevice(String address, boolean contant, int count, int batteryPercent) {
        this.address = address;
        this.contant = contant;
        this.count = count;
        this.batteryPercent = batteryPercent;
    }

    public String getAddress() {
        return address;
    }

    public void setContact(boolean contant) {
        this.contant = contant;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public boolean getContact() {
        return contant;
    }

    public int getCount() {
        return count;
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof BLEIODevice)) return false;

        BLEIODevice other = (BLEIODevice) obj;

        if(!getAddress().equals(other.getAddress())) return false;
        if(getCount() != other.getCount()) return false;
        if(getBatteryPercent() != other.getBatteryPercent()) return false;
        if(getContact() != other.getContact()) return false;

        return true;
    }
}
