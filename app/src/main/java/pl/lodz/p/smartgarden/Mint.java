package pl.lodz.p.smartgarden;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jusia on 16.05.2017.
 */
public class Mint extends Flower implements Parcelable{

    public Mint() {
        this.lightValue = 5;
        this.waterValue = 5;
        this.name = "Mint";
        this.flowerImage = "@drawable/mint.png";
    }

    private Mint(Parcel dest) {
        super(dest);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Mint> CREATOR = new Parcelable.Creator<Mint>() {
        @Override
        public Mint createFromParcel(Parcel in) {
            return new Mint(in);
        }

        @Override
        public Mint[] newArray(int size) {
            return new Mint[size];
        }
    };
}
