package hearc.ch.protoprintmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leonardo.distasio on 22.10.2015.
 */
public class Locator implements Parcelable
{
    private int id;
    private int pX;
    private int pY;
    private int realPX;
    private int realPY;
    private int radius = 30;
    private int minorId;
    private int majorId;
    private int index;
    private String macAdresse;
    private int id_plan;

    public Locator(int px, int py)
    {
        this.pX = px;
        this.pY = py;
        this.macAdresse = "";
    }

    public Locator(int _id, int _x, int _y, int _posMX, int _posMY, int _minorId, int _majorId, String _mac, int _id_plan)
    {
        this.id = _id;
        this.pX = _x;
        this.pY = _y;
        this.realPX = _posMX;
        this.realPY = _posMY;
        this.minorId = _minorId;
        this.majorId = _majorId;
        this.macAdresse = _mac;
        this.id_plan = _id_plan;
    }

    public int getId() { return id; }

    public int getpX() {
        return pX;
    }

    public int getpY() {
        return pY;
    }

    public int getIndex() {
        return index;
    }

    public int getId_plan() {
        return id_plan;
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

    public void setId(int id) {
        this.id = id;
    }

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

    public void setpX(int pX) {
        this.pX = pX;
    }

    public void setpY(int pY) {
        this.pY = pY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setId_plan(int id_plan) {
        this.id_plan = id_plan;
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

    public static final Creator<Locator> CREATOR = new Creator<Locator>()
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
