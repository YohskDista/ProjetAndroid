package hearc.ch.protoprintmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualisationPlanActivity extends AppCompatActivity {

    private List<Locator> listBeacons;
    private Plan planEtage;
    private ViewVisualisationPlan viewVisualisationPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation_plan);

        listBeacons = new ArrayList<Locator>();
        planEtage = (Plan) getIntent().getParcelableExtra("Plan");

        viewVisualisationPlan = (ViewVisualisationPlan) findViewById(R.id.drawViewPlanVisualisation);
        drawPlanAndBeacons();

        startThreadMoveMap(planEtage.getId());
    }

    private void startThreadMoveMap(final int idPlan)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try {
                        getListBeaconBDD(idPlan);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    public void getListBeaconBDD(final int id_plan)
    {
        final String url = "http://157.26.107.116/ProjetAndroid/action_get_beacons.php";
        //final String url = "http://192.168.1.35/ProjetAndroid/action_get_beacons.php";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Map<String, String> values = new HashMap<String, String>();
                    values.put("id_plan", id_plan+"");

                    listBeacons = CommunicationBDD.getBeaconInformations(url, values);

                    VisualisationPlanActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewVisualisationPlan.setBeaconsToDraw(listBeacons);
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

    private void drawPlanAndBeacons()
    {
        String url = "http://157.26.107.116/ProjetAndroid/" + planEtage.getImg().replace('\\', '/');
        //String url = "http://192.168.1.35/ProjetAndroid/" + planEtage.getImg().replace('\\', '/');
        viewVisualisationPlan.setImageUrl(url);
    }
}
