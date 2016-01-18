package hearc.ch.maraudermapapplication.viewmap;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hearc.ch.maraudermapapplication.R;
import hearc.ch.maraudermapapplication.tools.bdd.ActionBdd;
import hearc.ch.maraudermapapplication.tools.bdd.ActionEnum;
import hearc.ch.maraudermapapplication.tools.bdd.CommunicationBDD;
import hearc.ch.maraudermapapplication.tools.object.Plan;
import hearc.ch.maraudermapapplication.tools.Preferences;
import hearc.ch.maraudermapapplication.viewmap.tools.ListPlanAdapter;
import hearc.ch.maraudermapapplication.viewmap.tools.ListPlanListener;

/**
 * Visualisation de tous les plans dans une liste
 */
public class ShowAllMapActivity extends AppCompatActivity implements ListPlanListener {

    private ListView listPlan;
    private List<Plan> listPlanObj;
    private ListPlanAdapter adapter;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_map_main);

        preferences = new Preferences(this);
        listPlanObj = new ArrayList<Plan>();

        adapter = new ListPlanAdapter(this, listPlanObj);
        adapter.addListener(this);

        listPlan = (ListView) findViewById(R.id.listPlan);
        listPlan.setAdapter(adapter);

        getListPlanBDD();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
     * Récupération de tous les plans disponible de la BDD
     */
    public void getListPlanBDD()
    {
        final String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/actions_function.php";
        final ActionBdd action = new ActionBdd();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Map<String, String> values = new HashMap<String, String>();
                    values.put("function", action.getMapAction().get(ActionEnum.GET_PLAN));
                    values.put("plans", "ok");

                    listPlanObj = CommunicationBDD.getPlanInformations(url, values, null);

                    ShowAllMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            adapter.refresh(listPlanObj);
                            listPlan.invalidate();
                            listPlan.invalidateViews();
                        }
                    });
                }
                catch (IOException e)
                {
                    Log.e("Erreur", "Erreur de récupération");
                }
            }
        }).start();
    }

    /**
     * Bluetooth enable ?
     *
     * @return
     */
    private boolean isBluetoothEnable()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void OnClickPlan(Plan plan, int position)
    {
        startVisualisationPlanActivity(plan);
    }

    /**
     * Visualisation du plan si le Bluetooth est activé
     * @param plan
     */
    public void startVisualisationPlanActivity(Plan plan)
    {
        if(isBluetoothEnable())
        {
            Intent intent = new Intent(this, VisualisationPlanActivity.class);
            intent.putExtra("Plan", plan);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Veuillez activer votre Bluetooth !", Toast.LENGTH_SHORT).show();
        }
    }
}
