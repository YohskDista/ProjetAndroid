package hearc.ch.protoprintmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leonardo.distasio on 10.11.2015.
 */
public class Plan implements Parcelable
{
    private int id;
    private String img;
    private int width;
    private int height;
    private float longM;
    private float largM;

    public Plan()
    {

    }

    public Plan(int _id, String _img, int _width, int _height, float _longM, float _largM)
    {
        this.id = _id;
        this.img = _img;
        this.width = _width;
        this.height = _height;
        this.longM = _longM;
        this.largM = _largM;
    }

    protected Plan(Parcel in) {
        id = in.readInt();
        img = in.readString();
        width = in.readInt();
        height = in.readInt();
        longM = in.readFloat();
        largM = in.readFloat();
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(img);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeFloat(longM);
        dest.writeFloat(largM);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getLongM() {
        return longM;
    }

    public void setLongM(float longM) {
        this.longM = longM;
    }

    public float getLargM() {
        return largM;
    }

    public void setLargM(float largM) {
        this.largM = largM;
    }
}
