package hearc.ch.protoprintmap;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by leonardo.distasio on 05.11.2015.
 */
public class CommunicationBDD
{

    public static List<Plan> getPlanInformations(String url, Map<String, String> kvPairs, File file) throws ClientProtocolException, IOException {
        List<Plan> listObject = new ArrayList<Plan>();

        HttpEntity entity = doPost(url, kvPairs, file).getEntity();

        InputStream is = entity.getContent();
        String result = readResponse(is);

        try
        {
            JSONObject json_data = new JSONObject(result);
            Log.i("JSON", json_data.toString());

            Iterator<String> it = json_data.keys();
            while(it.hasNext())
            {
                String key = it.next();
                JSONObject obj = json_data.getJSONObject(key);
                int id = obj.getInt("id");
                String img = obj.getString("img");
                int width = obj.getInt("width");
                int height = obj.getInt("height");
                int longM = obj.getInt("longM");
                int largM = obj.getInt("largM");
                Plan p = new Plan(id, img, width, height, longM, largM);
                listObject.add(p);
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return listObject;
    }

    public static List<Locator> getBeaconInformations(String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException
    {
        List<Locator> listObject = new ArrayList<Locator>();

        HttpEntity entity = doPost(url, kvPairs, null).getEntity();

        InputStream is = entity.getContent();
        String result = readResponse(is);

        try
        {
            JSONObject json_data = new JSONObject(result);

            Iterator<String> it = json_data.keys();
            while(it.hasNext())
            {
                String key = it.next();
                JSONObject obj = json_data.getJSONObject(key);
                int id = obj.getInt("id");
                int x = obj.getInt("x");
                int y = obj.getInt("y");
                int posMX = obj.getInt("posMX");
                int posMY = obj.getInt("posMY");
                int minorId = obj.getInt("minorId");
                int majorId = obj.getInt("majorId");
                String mac = obj.getString("MAC");
                int id_plan = obj.getInt("id_plan");

                Locator l = new Locator(id, x, y, posMX, posMY, minorId, majorId, mac, id_plan);

                listObject.add(l);
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return listObject;
    }

    private static HttpResponse doPost(String url, Map<String, String> kvPairs, File file) throws IOException
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if (kvPairs != null && kvPairs.isEmpty() == false) {
            String k, v;
            Iterator<String> itKeys = kvPairs.keySet().iterator();

            while (itKeys.hasNext()) {
                k = itKeys.next();
                v = kvPairs.get(k);
                Log.i("Comm : ", k + " => " + v);
                entityBuilder.addTextBody(k, v);
            }

            if(file != null)
            {
                entityBuilder.addBinaryBody("image", file);
            }

            httppost.setEntity(entityBuilder.build());
        }

        HttpResponse response;
        return httpclient.execute(httppost);
    }

    private static String readResponse(InputStream is)
    {
        String line = null;
        String result = null;

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        return result;
    }
}
