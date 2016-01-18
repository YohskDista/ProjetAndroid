package hearc.ch.maraudermapapplication.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import hearc.ch.maraudermapapplication.R;
import hearc.ch.maraudermapapplication.tools.Preferences;

public class SettingsActivity extends AppCompatActivity {

    private Preferences preferences;
    private EditText etIpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = new Preferences(this);
        etIpServer = (EditText) findViewById(R.id.etServerIp);

        if(!preferences.getIpServer().isEmpty())
            etIpServer.setText(preferences.getIpServer());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
     * Save Preferences
     * @param v
     */
    public void saveSettingsClick(View v)
    {
        if(verifyIP(etIpServer.getText().toString()))
        {
            preferences.setIpServer(etIpServer.getText().toString());
            Toast.makeText(this, "Vos paramètres ont été sauvegardés", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            Toast.makeText(this, "Entrez une adresse IP valide", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verify the conformity of the IP address
     * @param ip
     * @return
     */
    private boolean verifyIP(String ip)
    {
        Pattern pattern = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

        return pattern.matcher(ip).matches();
    }
}
