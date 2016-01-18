package hearc.ch.maraudermapapplication.createmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hearc.ch.maraudermapapplication.R;
import hearc.ch.maraudermapapplication.tools.object.Locator;

public class BeaconConfigurationActivity extends AppCompatActivity {

    private Locator locator;
    private EditText etMacAdresse;
    private EditText etMinorID;
    private EditText etMajorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_configuration);

        // Récupération Locator
        locator = getIntent().getExtras().getParcelable("locator");

        // EditText du fichier XML
        etMacAdresse = (EditText) findViewById(R.id.etMacAdresse);
        etMinorID = (EditText) findViewById(R.id.etMinorID);
        etMajorID = (EditText) findViewById(R.id.etMajorID);

        etMacAdresse.setHint(locator.getMacAdresse());
        etMinorID.setHint(locator.getMinorId() + "");
        etMajorID.setHint(locator.getMajorId() + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    *   Sauvegarde de la configuration Beacon
    * */
    public void onClickSaveBeaconConfig(View view)
    {

        int minorId = Integer.parseInt(notBeNull(etMinorID.getText().toString(), 0).toString());
        int majorId = Integer.parseInt(notBeNull(etMajorID.getText().toString(), 0).toString());

        locator.setMacAdresse(notBeNull(etMacAdresse.getText().toString(), "").toString());
        locator.setMinorId(minorId);
        locator.setMajorId(majorId);

        Intent intent = new Intent();
        intent.putExtra("locator", locator);
        setResult(RESULT_OK, intent);

        Toast.makeText(this, "Votre beacon a été configuré", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * Test si l'objet est null et le remplace si c'est le cas
     * @param text
     * @param value
     * @return
     */
    private Object notBeNull(String text, Object value)
    {
        if(text == null) return value;
        return text;
    }

    /**
     * Bouton d'annulation
     * @param view
     */
    public void onClickCancel(View view)
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
