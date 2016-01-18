package hearc.ch.maraudermapapplication.viewmap;


import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hearc.ch.maraudermapapplication.R;
import hearc.ch.maraudermapapplication.tools.bdd.ActionBdd;
import hearc.ch.maraudermapapplication.tools.bdd.ActionEnum;
import hearc.ch.maraudermapapplication.tools.bdd.CommunicationBDD;
import hearc.ch.maraudermapapplication.tools.object.Locator;
import hearc.ch.maraudermapapplication.tools.object.PersonLocator;
import hearc.ch.maraudermapapplication.tools.object.Plan;
import hearc.ch.maraudermapapplication.tools.Preferences;

public class VisualisationPlanActivity extends AppCompatActivity {

    private List<Locator> listBeacons;
    private List<PersonLocator> listPersonsLocators;
    private Plan planEtage;
    private Plan planEtageUser;
    private ViewVisualisationPlan viewVisualisationPlan;
    private boolean running;
    private BeaconManager beaconManager;
    private Region region;
    private PersonLocator personLocator;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation_plan);

        preferences = new Preferences(this);
        listBeacons = new ArrayList<Locator>();
        planEtage = (Plan) getIntent().getParcelableExtra("Plan");
        personLocator = new PersonLocator(getPhoneName());
        listPersonsLocators = new ArrayList<PersonLocator>();
        planEtageUser = new Plan();

        viewVisualisationPlan = (ViewVisualisationPlan) findViewById(R.id.drawViewPlanVisualisation);
        drawPlan();
        startBeaconScan();
        threadRefreshPosition();

        startThreadMoveMap(planEtage.getId());
    }

    /**
     * Insertion ou update de la position des personnes
     */
    private void threadRefreshPosition()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(running)
                {
                    if(personLocator != null)
                    {
                        try
                        {
                            ActionBdd actionBdd = new ActionBdd();
                            Map<String, String> kvPairs = personLocator.createKvPairs();

                            if(planEtageUser != null) {
                                if (planEtage.getId() == planEtageUser.getId()) {
                                    if (personLocator.getId() <= 0)
                                        kvPairs.put("function", actionBdd.getMapAction().get(ActionEnum.INSERT_USER));
                                    else
                                        kvPairs.put("function", actionBdd.getMapAction().get(ActionEnum.UPDATE_USER));

                                    final String url = "http://" + preferences.getIpServer() + "/ProjetAndroid/actions_function.php";

                                    personLocator.setId(CommunicationBDD.insert(url, kvPairs, null));
                                }
                            }
                            Thread.sleep(200);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Scanner les Beacons
     */
    private void startBeaconScan()
    {
        beaconManager = new BeaconManager(this);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty())
                {
                    detectActualPlan(list);
                    if(planEtageUser != null) {
                        personLocator.calculatePointLocation(list, planEtageUser);
                        viewVisualisationPlan.setCircleToDraw(personLocator, listPersonsLocators, planEtage);
                    }
                }
            }
        });

        region = new Region("ranged region", UUID.fromString("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), null, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region",
                        UUID.fromString("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), 1, 48827));
            }
        });
    }

    /**
     * Calcul de la position et rafraîchissement de la position
     * @param idPlan
     */
    private void startThreadMoveMap(final int idPlan)
    {
        running = true;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(running)
                {
                    try {
                        getListBeaconBDD(idPlan);
                        getListUserLocation(idPlan);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Récupérer la liste des utilisateurs présents dans le plan
     * @param idPlan
     */
    private void getListUserLocation(final int idPlan)
    {
        final String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/actions_function.php";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Map<String, String> values = new HashMap<String, String>();

                    ActionBdd action = new ActionBdd();
                    values.put("function", action.getMapAction().get(ActionEnum.GET_ALL_USER));
                    values.put("id_plan", idPlan+"");

                    listPersonsLocators = CommunicationBDD.getUserLocation(url, values);
                }
                catch (IOException e)
                {
                    Log.e("Erreur", "Erreur de récupération");
                }
            }
        }).start();
    }
    /**
     * Verify in which Plan whe are
     *
     * @param list
     */
    private void detectActualPlan(final List<Beacon> list)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    String url = "http://" + preferences.getIpServer() + "/ProjetAndroid/actions_function.php";
                    Map<String, String> kvPairs = Plan.detectActualPlan(list);

                    planEtageUser = CommunicationBDD.getActualPlan(url, kvPairs);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visualisation_plan, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    /**
     * Supprimer la personne de la BDD
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/actions_function.php";
                Map<String, String> kvPairs = personLocator.createKvPairsDelete();

                try {
                    CommunicationBDD.delete(url, kvPairs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        beaconManager.disconnect();
        beaconManager = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Récupérer tous les beacons du plan dans la BDD
     * @param id_plan
     */
    public void getListBeaconBDD(final int id_plan)
    {
        final String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/actions_function.php";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Map<String, String> values = new HashMap<String, String>();
                    ActionBdd action = new ActionBdd();
                    values.put("function", action.getMapAction().get(ActionEnum.GET_BEACON));
                    values.put("id_plan", id_plan+"");

                    listBeacons = CommunicationBDD.getBeaconInformations(url, values);
                    personLocator.setListLocator(listBeacons);
                }
                catch (IOException e)
                {
                    Log.e("Erreur", "Erreur de récupération");
                }
            }
        }).start();
    }

    /**
     * Dessiner le plan
     */
    private void drawPlan()
    {
        String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/" + planEtage.getImg().replace('\\', '/');
        viewVisualisationPlan.setImageUrl(url);
    }

    /**
     * Récupérer le nom du téléphone
     * @return
     */
    private String getPhoneName()
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        Log.i("Test", deviceName);
        return deviceName;
    }
}
