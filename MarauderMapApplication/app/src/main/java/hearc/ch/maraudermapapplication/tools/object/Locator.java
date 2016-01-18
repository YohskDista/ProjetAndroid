package hearc.ch.maraudermapapplication.tools.object;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hearc.ch.maraudermapapplication.tools.bdd.ActionBdd;
import hearc.ch.maraudermapapplication.tools.bdd.ActionEnum;

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
    private double distance;

    /**
     * Constructeur
     * @param px
     * @param py
     */
    public Locator(int px, int py)
    {
        this.pX = px;
        this.pY = py;
        this.macAdresse = "";
        this.distance = 0;
    }

    /**
     * Constructeur complet
     * @param _id
     * @param _x
     * @param _y
     * @param _posMX
     * @param _posMY
     * @param _minorId
     * @param _majorId
     * @param _mac
     * @param _id_plan
     */
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

    /**
     * Constructeur avec objet JSON
     * @param obj
     * @throws JSONException
     */
    public Locator(JSONObject obj) throws JSONException
    {
        this.id = obj.getInt("id");
        this.pX = obj.getInt("x");
        this.pY = obj.getInt("y");
        this.realPX = obj.getInt("posMX");
        this.realPY = obj.getInt("posMY");
        this.minorId = obj.getInt("minorId");
        this.majorId = obj.getInt("majorId");
        this.macAdresse = obj.getString("MAC");
        this.id_plan = obj.getInt("id_plan");
    }

    /**
     * Map de création de Plan à envoyer au serveur
     * @return
     */
    public Map<String, String> createMap()
    {
        Map map = new HashMap<String, String>();

        map.put("x", pX + "");
        map.put("y", pY + "");
        map.put("posMX", realPX + "");
        map.put("posMY", realPY + "");
        map.put("minorId", minorId + "");
        map.put("majorId", majorId + "");
        map.put("MAC", macAdresse);
        map.put("id_plan", id_plan + "");

        return map;
    }

    /**
     * Get all Locators of the actual plan
     * @param actualPlanId
     * @return
     */
    public static Map<String, String> getLocatorActualPlan(int actualPlanId)
    {
        ActionBdd action = new ActionBdd();
        Map<String, String> kvPairs = new HashMap<String, String>();

        kvPairs.put("function", action.getMapAction().get(ActionEnum.GET_BEACON));
        kvPairs.put("id_plan", actualPlanId+"");

        return kvPairs;
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

    public double getDistance() {
        return distance;
    }

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

    public void setDistance(double distance) {
        this.distance = distance;
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
