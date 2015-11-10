package hearc.ch.protoaddplan;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by leonardo.distasio on 22.10.2015.
 */
public class Locator implements Parcelable
{
    private int pX;
    private int pY;
    private int realPX;
    private int realPY;
    private int radius = 30;
    private int minorId;
    private int majorId;
    private int index;
    private String macAdresse;

    public Locator(int px, int py)
    {
        this.pX = px;
        this.pY = py;
        this.macAdresse = "";
    }

    public int getX() { return pX; }

    public int getY() { return pY; }

    public int getIndex() {
        return index;
    }

    public int getRealPX() {
        return realPX;
    }

    public int getRealPY() {
        return realPY;
    }

    public int getRadius() { return radius; }

    public int getMinorId() { return minorId; }

    public int getMajorId() { return majorId; }

    public String getMacAdresse() { return macAdresse; }

    public void setMinorId(int minorId)
    {
        this.minorId = minorId;
    }

    public void setMajorId(int majorId)
    {
        this.majorId = majorId;
    }

    public void setMacAdresse(String macAdresse)
    {
        this.macAdresse = macAdresse;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRealPX(int realPX) {
        this.realPX = realPX;
    }

    public void setRealPY(int realPY) {
        this.realPY = realPY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pX);
        dest.writeInt(pY);
        dest.writeInt(realPX);
        dest.writeInt(realPY);
        dest.writeInt(radius);
        dest.writeInt(minorId);
        dest.writeInt(majorId);
        dest.writeInt(index);
        dest.writeString(macAdresse);
    }

    public static final Parcelable.Creator<Locator> CREATOR = new Parcelable.Creator<Locator>()
    {
        public Locator createFromParcel(Parcel in) {
            return new Locator(in);
        }

        public Locator[] newArray(int size) {
            return new Locator[size];
        }
    };

    private Locator(Parcel in) {
        pX = in.readInt();
        pY = in.readInt();
        realPX = in.readInt();
        realPY = in.readInt();
        radius = in.readInt();
        minorId = in.readInt();
        majorId = in.readInt();
        index = in.readInt();
        macAdresse = in.readString();
    }
}
