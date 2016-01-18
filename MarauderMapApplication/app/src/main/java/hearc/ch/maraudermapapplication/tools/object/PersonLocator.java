package hearc.ch.maraudermapapplication.tools.object;

import android.graphics.Color;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hearc.ch.maraudermapapplication.tools.bdd.ActionBdd;
import hearc.ch.maraudermapapplication.tools.bdd.ActionEnum;
import hearc.ch.maraudermapapplication.tools.math.Kalman;

/**
 * Created by leonardo.distasio on 16.11.2015.
 */
public class PersonLocator
{
    private int id;
    private String nom;
    private int pX;
    private int pY;
    private int id_plan;
    private int radius;
    private int color;
    private List<Locator> listLocator;
    private List<Locator> listNearLocator;

    public PersonLocator()
    {
        this.id = 0;
        this.radius = 25;
        this.color = Color.RED;
        this.listLocator = new ArrayList<Locator>();
        this.listNearLocator = new ArrayList<Locator>();
    }

    public PersonLocator(String name)
    {
        this();
        this.nom = name;
    }

    /**
     * Constructeur avec objet JSON
     * @param obj
     * @throws JSONException
     */
    public PersonLocator(JSONObject obj) throws JSONException
    {
        this(obj.getString("nom"));
        this.id = obj.getInt("id");
        this.pX = obj.getInt("posX");
        this.pY = obj.getInt("posY");
        this.id_plan = obj.getInt("id_plan");
    }

    /**
     * Création de personne
     * @return
     */
    public Map<String, String> createKvPairs()
    {
        Map<String, String> kvPairs = new HashMap<String, String>();

        kvPairs.put("id", id + "");
        kvPairs.put("nom", nom+"");
        kvPairs.put("posX", pX+"");
        kvPairs.put("posY", pY + "");
        kvPairs.put("id_plan", id_plan+"");

        return kvPairs;
    }

    /**
     * Créer Map de suppression de Personne
     * @return
     */
    public Map<String, String> createKvPairsDelete()
    {
        Map<String, String> kvPairs = new HashMap<String, String>();
        ActionBdd actionBdd = new ActionBdd();

        kvPairs.put("id", id+"");
        kvPairs.put("function", actionBdd.getMapAction().get(ActionEnum.DELETE_USER));

        return kvPairs;
    }

    /**
     * Calculé la position sur le Plan
     *
     * @param listBeacon
     * @param plan
     */
    public void calculatePointLocation(List<Beacon> listBeacon, Plan plan)
    {
        listNearLocator.clear();
        Random rand = new Random();
        double ak = rand.nextGaussian();

        for(Beacon b : listBeacon) {
            Locator l = searchLocator(b);
            if (l != null) {
                double accuracy = Utils.computeAccuracy(b);

                Kalman kalman = new Kalman(accuracy, ak);
                l.setDistance(kalman.calculateKalman());

                listNearLocator.add(l);
            }
        }
        trilateration(listNearLocator, plan);
    }

    /**
     * Calcul de trilatération
     * @param nearLocator
     * @param plan
     */
    private void trilateration(List<Locator> nearLocator, Plan plan)
    {
        if(nearLocator.size() >= 3)
        {
            Locator l1 = nearLocator.get(0);
            Locator l2 = nearLocator.get(1);
            Locator l3 = nearLocator.get(2);

            this.id_plan = l1.getId_plan();

            double r1 = l1.getDistance();
            double r2 = l2.getDistance();
            double r3 = l3.getDistance();

            double a,b,c,d,dist1,dist2,coeff;

            a = 2*(l3.getpX()-l1.getpX());
            b = 2*(l3.getpY()-l1.getpY());
            c = 2*(l3.getpX()-l2.getpX());
            d = 2*(l3.getpY()-l2.getpY());

            dist1 = carre(r1)-carre(r3)+carre(l3.getpX())-carre(l1.getpX())+carre(l3.getpY())-carre(l1.getpY());
            dist2 = carre(r2)-carre(r3)+carre(l3.getpX())-carre(l2.getpX())+carre(l3.getpY())-carre(l2.getpY());

            coeff = 1/(a*d-b*c);

            int newX = (int)((coeff*d*dist1) + (coeff*(-b)*dist2));
            int newY = (int)((coeff*(-c)*dist1) + (coeff*a*dist2));

            if(distanceIsCorrect(newX, newY, plan))
            {
                this.pX = (int) ((coeff * d * dist1) + (coeff * (-b) * dist2));
                this.pY = (int) ((coeff * (-c) * dist1) + (coeff * a * dist2));
            }
        }
    }

    private boolean distanceIsCorrect(int newX, int newY, Plan plan)
    {
        return newX >= 0 && newY >= 0 && newX <= plan.getWidth() && newY <= plan.getHeight();
    }

    private double carre(double x)
    {
        return Math.pow(x,2);
    }

    private Locator searchLocator(Beacon b)
    {
        for (Locator l : listLocator)
        {
            if(l.getMinorId() == b.getMajor() && l.getMajorId() == b.getMinor())
                return l;
        }
        return null;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getpX() {
        return pX;
    }

    public void setpX(int pX) {
        this.pX = pX;
    }

    public int getpY() {
        return pY;
    }

    public void setpY(int pY) {
        this.pY = pY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Locator> getListLocator() {
        return listLocator;
    }

    public void setListLocator(List<Locator> listLocator) {
        this.listLocator = listLocator;
    }

    public List<Locator> getListNearLocator() {
        return listNearLocator;
    }

    public void setListNearLocator(List<Locator> listNearLocator) {
        this.listNearLocator = listNearLocator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_plan() {
        return id_plan;
    }

    public void setId_plan(int id_plan) {
        this.id_plan = id_plan;
    }
}
