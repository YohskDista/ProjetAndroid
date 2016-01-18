package hearc.ch.maraudermapapplication.tools.bdd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leonardo.distasio on 23.11.2015.
 */
public class ActionBdd
{
    private Map<ActionEnum, String> mapAction;

    public ActionBdd()
    {
        mapAction = new HashMap<ActionEnum, String>();

        // Création Map Enum
        for(ActionEnum e : ActionEnum.values())
        {
            mapAction.put(e, e.toString().toLowerCase());
        }
    }

    public Map<ActionEnum, String> getMapAction()
    {
        return mapAction;
    }
}
