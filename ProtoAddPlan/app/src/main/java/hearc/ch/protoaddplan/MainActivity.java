package hearc.ch.protoaddplan;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity{

    private static final int BEACON_CONFIGURATION = 2;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath = "";
    private DrawView drawView;
    private Button btnSave;
    private Button btnInsertPlan;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawView) findViewById(R.id.drawMap);
        drawView.setMainActivity(this);

        btnInsertPlan = (Button) findViewById(R.id.btnPlaceBeacon);
        btnSave = (Button) findViewById(R.id.btnSavePlan);
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

    public void browseGalleryClick(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void savePlanAndBeacon(View v)
    {
        final List<Locator> listLocator = drawView.getListLocator();
        final int widthReal = drawView.getWidthReal();
        final int heightReal = drawView.getHeightReal();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enregistrement du plan");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();

        final int objectToSave = listLocator.size() + 1;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //String url = "http://192.168.1.35/ProjetAndroid/action_add_plan.php";
                String url = "http://157.26.107.116/ProjetAndroid/action_add_plan.php";
                Map<String, String> kvPairs = new HashMap<String, String>();
                kvPairs.put("width", drawView.getWidth()+"");
                kvPairs.put("height", drawView.getHeight()+"");
                kvPairs.put("longM", widthReal+"");
                kvPairs.put("largM", heightReal+"");

                int progression = 0;

                final int idPlan;

                try
                {
                    idPlan = CommunicationBDD.doPost(url, kvPairs, new File(picturePath));

                    progressDialog.setProgress(++progression);

                    for (Locator l : listLocator)
                    {
                        kvPairs.clear();
                        kvPairs.put("x", l.getX()+"");
                        kvPairs.put("y", l.getY()+"");
                        kvPairs.put("posMX", l.getRealPX()+"");
                        kvPairs.put("posMY", l.getRealPY()+"");
                        kvPairs.put("minorId", l.getMinorId()+"");
                        kvPairs.put("majorId", l.getMajorId()+"");
                        kvPairs.put("MAC", l.getMacAdresse());
                        kvPairs.put("id_plan", idPlan + "");

                        //CommunicationBDD.doPost("http://192.168.1.35/ProjetAndroid/action_add_beacon.php", kvPairs, null);
                        CommunicationBDD.doPost("http://157.26.107.116/ProjetAndroid/action_add_beacon.php", kvPairs, null);
                        progressDialog.setProgress(++progression);
                    }

                    progressDialog.dismiss();

                    if(idPlan > 0)
                    {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Votre plan a été inséré avec succès", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        MainActivity.this.runOnUiThread(new Runnable() {
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
                    MainActivity.this.runOnUiThread(new Runnable() {
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

        Log.i("Test", requestCode + "");
        if(requestCode == BEACON_CONFIGURATION && resultCode == RESULT_OK && data != null)
        {
            List<Locator> listLocator = drawView.getListLocator();
            Locator newLocator = data.getExtras().getParcelable("locator");
            listLocator.remove(newLocator.getIndex());
            listLocator.add(newLocator.getIndex(), newLocator);

            for (Locator l: listLocator) {
                Log.i("Beacon", l.getMajorId()+"");
            }

        }
    }

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

    public void callBeaconConfiguration(Locator locator)
    {
        Intent intent = new Intent(this, BeaconConfigurationActivity.class);
        intent.putExtra("locator", locator);
        startActivityForResult(intent, BEACON_CONFIGURATION);
    }
}
