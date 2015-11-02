package hearc.ch.protoaddplan;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final int BEACON_CONFIGURATION = 2;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath = "";
    private DrawView drawView;
    private Button btnSave;
    private Button btnInsertPlan;

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

    public void savePlan(View v)
    {
        List<Locator> listLocator = drawView.getListLocator();
        int widthReal = drawView.getWidthReal();
        int heightReal = drawView.getHeightReal();
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

        Log.i("Test", requestCode+"");
        if(requestCode == BEACON_CONFIGURATION && resultCode == RESULT_OK && data != null)
        {
            List<Locator> listLocator = drawView.getListLocator();
            Locator newLocator = data.getExtras().getParcelable("locator");
            listLocator.add(newLocator.getIndex(), newLocator);

            Log.i("Test", "Salut");
            for (Locator l: listLocator) {
                Log.i("Test", l.getMajorId()+"");
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
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
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
