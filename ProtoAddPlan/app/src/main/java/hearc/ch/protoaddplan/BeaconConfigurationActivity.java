package hearc.ch.protoaddplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BeaconConfigurationActivity extends AppCompatActivity {

    private Locator locator;
    private EditText etMacAdresse;
    private EditText etMinorID;
    private EditText etMajorID;
    private Button btnSaveConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_configuration);

        locator = getIntent().getExtras().getParcelable("locator");
        etMacAdresse = (EditText) findViewById(R.id.etMacAdresse);
        etMinorID = (EditText) findViewById(R.id.etMinorID);
        etMajorID = (EditText) findViewById(R.id.etMajorID);
        btnSaveConfig = (Button) findViewById(R.id.btnSaveConfigBeacon);

        etMacAdresse.setText(locator.getMacAdresse());
        etMinorID.setText(locator.getMinorId() + "");
        etMajorID.setText(locator.getMajorId() + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_configuration, menu);
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

    public void onClickSaveBeaconConfig(View view)
    {
        int minorId = Integer.parseInt(etMinorID.getText().toString());
        int majorId = Integer.parseInt(etMajorID.getText().toString());

        locator.setMacAdresse(etMacAdresse.getText().toString());
        locator.setMinorId(minorId);
        locator.setMajorId(majorId);

        Intent intent = new Intent();
        intent.putExtra("locator", locator);
        setResult(RESULT_OK, intent);

        Toast.makeText(this, "Votre beacon a été configuré", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void onClickCancel(View view)
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
