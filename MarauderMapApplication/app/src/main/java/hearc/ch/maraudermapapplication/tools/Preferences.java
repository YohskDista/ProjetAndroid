package hearc.ch.maraudermapapplication.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Récupération et sauvegarde des préférences
 * Created by leonardo.distasio on 26.11.2015.
 */
public class Preferences
{
    private final static String MARAUDER_APP = "hearc.ch.mauraudermapapplication";
    private final static String SERVER_IP = "IPServer";

    private SharedPreferences preferences;

    public Preferences(Context context)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getIpServer()
    {
        return preferences.getString(SERVER_IP, "");
    }

    public void setIpServer(String ip)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER_IP, ip);
        editor.commit();
    }
}
