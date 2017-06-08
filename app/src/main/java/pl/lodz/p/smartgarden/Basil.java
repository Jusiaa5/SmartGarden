package pl.lodz.p.smartgarden;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jusia on 16.05.2017.
 */
public class Basil extends Flower implements Parcelable{

    public Basil() {
        this.lightValue = 3;
        this.waterValue = 3;
        this.name = "Basil";
        this.flowerImage = "@drawable/basil.png";
    }

    private Basil(Parcel dest) {
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
    public static final Parcelable.Creator<Basil> CREATOR = new Parcelable.Creator<Basil>() {
        @Override
        public Basil createFromParcel(Parcel in) {
            return new Basil(in);
        }

        @Override
        public Basil[] newArray(int size) {
            return new Basil[size];
        }
    };
}
