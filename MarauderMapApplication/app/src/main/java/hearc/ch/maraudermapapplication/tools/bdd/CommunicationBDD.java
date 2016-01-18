package hearc.ch.maraudermapapplication.tools.bdd;

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

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import hearc.ch.maraudermapapplication.tools.object.Locator;
import hearc.ch.maraudermapapplication.tools.object.PersonLocator;
import hearc.ch.maraudermapapplication.tools.object.Plan;

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

                Plan p = new Plan(obj);
                listObject.add(p);
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return listObject;
    }

    /**
     * Get the Plan's id
     *
     * @param url
     * @param kvPairs
     * @return
     * @throws IOException
     */
    public static Plan getActualPlan(String url, Map<String, String> kvPairs) throws IOException
    {
        Plan plan = null;
        HttpEntity entity = doPost(url, kvPairs, null).getEntity();

        InputStream is = entity.getContent();
        String result = readResponse(is);

        try
        {
            JSONObject json_data = new JSONObject(result);
            Log.i("Test Actual plan", result);
            Log.i("Test", kvPairs.get("function"));

            plan = new Plan(json_data);
        }
        catch(Exception e)
        {
            Log.e("Fail 3 actual plan", e.toString());
        }

        return plan;
    }

    /**
     * Information Beacon
     * @param url
     * @param kvPairs
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
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

                Locator l = new Locator(obj);

                listObject.add(l);
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return listObject;
    }

    /**
     * Insertion des informations dans la BDD
     * @param url
     * @param kvPairs
     * @param file
     * @return
     * @throws IOException
     */
    public static int insert(String url, Map<String, String> kvPairs, File file) throws IOException
    {
        int code = -1;
        HttpEntity entity = doPost(url, kvPairs, file).getEntity();

        InputStream is = entity.getContent();
        String result = readResponse(is);

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));
            Log.i("code", code+"");
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return code;
    }

    // Suppression
    public static void delete(String url, Map<String, String> kvPairs) throws IOException
    {
        doPost(url, kvPairs, null).getEntity();
    }

    /**
     * Commande HTML POST : envoi le message sous forme d'en-tête HTML
     * @param url
     * @param kvPairs
     * @param file
     * @return
     * @throws IOException
     */
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

    /**
     * Lire la réponse et renvoyer l'objet sous format JSON
     * @param is
     * @return
     */
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

    /**
     * Obtenir la liste de la localisation des personnes
     * @param url
     * @param kvPairs
     * @return
     * @throws IOException
     */
    public static List<PersonLocator> getUserLocation(String url, Map<String, String> kvPairs) throws IOException {
        List<PersonLocator> listObject = new ArrayList<PersonLocator>();

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

                PersonLocator pl = new PersonLocator(obj);

                listObject.add(pl);
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return listObject;
    }
}
