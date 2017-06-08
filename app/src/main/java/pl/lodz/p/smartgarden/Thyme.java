package pl.lodz.p.smartgarden;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jusia on 16.05.2017.
 */
public class Thyme extends Flower implements Parcelable {

    public Thyme() {
        this.lightValue = 4;
        this.waterValue = 4;
        this.name = "Thyme";
        this.flowerImage = "@drawable/thyme.png";
    }

    private Thyme(Parcel dest) {
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
    public static final Parcelable.Creator<Thyme> CREATOR = new Parcelable.Creator<Thyme>() {
        @Override
        public Thyme createFromParcel(Parcel in) {
            return new Thyme(in);
        }

        @Override
        public Thyme[] newArray(int size) {
            return new Thyme[size];
        }
    };
}
