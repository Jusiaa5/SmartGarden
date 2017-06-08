package pl.lodz.p.smartgarden;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jusia on 16.05.2017.
 */
public abstract class Flower implements Parcelable {
    protected int lightValue, waterValue;
    protected String name;
    protected String flowerImage;

    public Flower() {

    }

    public int getLightValue() {
        return lightValue;
    }

    public int getWaterValue() {
        return waterValue;
    }

    public String getName() {
        return this.name;
    }

    public String getFlowerImage() {
        return this.flowerImage;
    }

    protected Flower(Parcel in) {
        lightValue = in.readInt();
        waterValue = in.readInt();
        name = in.readString();
        flowerImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lightValue);
        dest.writeInt(waterValue);
        dest.writeString(name);
        dest.writeString(flowerImage);
    }

}
