package hearc.ch.protoaddplan;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by leonardo.distasio on 05.11.2015.
 */
public class CommunicationBDD
{

    public static int doPost(String url, Map<String, String> kvPairs, File file) throws ClientProtocolException, IOException {
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
        response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();

        String line = null;
        String result = null;
        int code = -1;

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

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }

        return code;
    }
}
