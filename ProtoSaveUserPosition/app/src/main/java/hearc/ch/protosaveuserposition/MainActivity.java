package hearc.ch.protosaveuserposition;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
{
    private TextView tvNameDevice;
    private TextView tvWhichPlan;
    private TextView tvPosX;
    private TextView tvPosY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNameDevice = (TextView) findViewById(R.id.tvNameUser);
        tvWhichPlan = (TextView) findViewById(R.id.tvWhichPlan);
        tvPosX = (TextView) findViewById(R.id.tvPosX);
        tvPosY = (TextView) findViewById(R.id.tvPosY);

        setNameDevice();
    }

    private void setNameDevice()
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();

        tvNameDevice.setText(deviceName);
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
}
