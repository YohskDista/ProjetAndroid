package hearc.ch.protoaddplan;

/**
 * Created by leonardo.distasio on 22.10.2015.
 */
public class Locator
{
    private int pX;
    private int pY;
    private int realPX;
    private int realPY;
    private final int radius = 30;
    private int minorId;
    private int majorId;
    private String macAdresse;

    public Locator(int px, int py)
    {
        this.pX = px;
        this.pY = py;
    }

    public int getX() { return pX; }

    public int getY() { return pY; }

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

    public void setRealPX(int realPX) {
        this.realPX = realPX;
    }

    public void setRealPY(int realPY) {
        this.realPY = realPY;
    }
}
