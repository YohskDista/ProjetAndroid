package hearc.ch.maraudermapapplication.createmap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import hearc.ch.maraudermapapplication.R;
import hearc.ch.maraudermapapplication.tools.bdd.ActionBdd;
import hearc.ch.maraudermapapplication.tools.bdd.ActionEnum;
import hearc.ch.maraudermapapplication.tools.bdd.CommunicationBDD;
import hearc.ch.maraudermapapplication.tools.object.Locator;
import hearc.ch.maraudermapapplication.tools.object.Plan;
import hearc.ch.maraudermapapplication.tools.Preferences;

public class CreateMapActivity extends AppCompatActivity {

    private static final int BEACON_CONFIGURATION = 2;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath = "";
    private DrawView drawView;
    private Button btnSave;
    private Button btnInsertPlan;
    private ProgressDialog progressDialog;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        preferences = new Preferences(this);
        drawView = (DrawView) findViewById(R.id.drawMap);
        drawView.setMainActivity(this);

        btnInsertPlan = (Button) findViewById(R.id.btnPlaceBeacon);
        btnSave = (Button) findViewById(R.id.btnSavePlan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_map, menu);
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

    // Parcourir la galerie photo
    public void browseGalleryClick(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    // Sauvegarde du plan et des Beacons dans la BDD
    public void savePlanAndBeacon(View v)
    {
        final List<Locator> listLocator = drawView.getListLocator();
        final int widthReal = drawView.getWidthReal();
        final int heightReal = drawView.getHeightReal();

        // ProgressDialog d'attente
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enregistrement du plan");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();

        final int objectToSave = listLocator.size() + 1;

        final Plan plan = new Plan(0, picturePath, drawView.getWidth(), drawView.getHeight(), widthReal, heightReal);

        // Sauvegarde du Plan
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Sauvegarde du plan dans la BDD et récupération IP
                String url = "http://"+ preferences.getIpServer() +"/ProjetAndroid/actions_function.php";
                Map<String, String> kvPairs = plan.createMap();
                ActionBdd action = new ActionBdd();
                kvPairs.put("function", action.getMapAction().get(ActionEnum.ADD_PLAN));

                int progression = 0;

                final int idPlan;

                try
                {
                    idPlan = CommunicationBDD.insert(url, kvPairs, new File(picturePath));

                    progressDialog.setProgress(++progression);

                    // Sauvegarde des beacons dans la BDD
                    if(idPlan > 0)
                    {
                        for (Locator l : listLocator) {
                            kvPairs.clear();
                            l.setId_plan(idPlan);
                            kvPairs = l.createMap();
                            kvPairs.put("function", action.getMapAction().get(ActionEnum.ADD_BEACON));

                            CommunicationBDD.insert(url, kvPairs, null);
                            progressDialog.setProgress(++progression);
                        }
                    }

                    progressDialog.dismiss();

                    // Si le plan a été enregistré on quitte l'activité
                    if(idPlan > 0)
                    {
                        CreateMapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Votre plan a été inséré avec succès", Toast.LENGTH_SHORT).show();
                                CreateMapActivity.this.finish();
                            }
                        });
                    }
                    else
                    {
                        CreateMapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Il y a eu un problème", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (IOException e)
                {
                    progressDialog.dismiss();
                    CreateMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Le serveur n'est pas accessible pour le moment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Affichage du plan sélectionné
         */
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            showDialogDimension();

            drawView.setPicturePath(picturePath);

            btnInsertPlan.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }

        /**
         * Si c'est le Beacon qu'on vient de configurer
         */
        if(requestCode == BEACON_CONFIGURATION && resultCode == RESULT_OK && data != null)
        {
            List<Locator> listLocator = drawView.getListLocator();
            Locator newLocator = data.getExtras().getParcelable("locator");
            listLocator.remove(newLocator.getIndex());
            listLocator.add(newLocator.getIndex(), newLocator);

        }
    }

    /**
     * AlertDialog pour insérer la longueur et largeur du plan
     */
    public void showDialogDimension()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dimension_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);

        final EditText etWidth = (EditText) promptsView.findViewById(R.id.etDimLargeur);
        final EditText etHeight = (EditText) promptsView.findViewById(R.id.etDimLongueur);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Confirmer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int width = Integer.parseInt(etWidth.getText().toString());
                                int height = Integer.parseInt(etHeight.getText().toString());
                                drawView.setDimension(width, height);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * Configuration du Beacon
     * @param locator
     */
    public void callBeaconConfiguration(Locator locator)
    {
        Intent intent = new Intent(this, BeaconConfigurationActivity.class);
        intent.putExtra("locator", locator);
        startActivityForResult(intent, BEACON_CONFIGURATION);
    }
}
