package app.smartshopper.FCM;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Felix on 12.07.2016."
 */
public class SendToParticipants
{
    /**
     * @param message Nachricht für die anderen Teilnehmer
     * @param params beliebige Anzahl an (String-)Token
     */
    public static void send(String message, String... params)
    {
        String[] tokenArray = params;

        for (int i=0;i<tokenArray.length;i++)
        {
            DownstreamMessage downstreamMessage = new DownstreamMessage();
            downstreamMessage.execute(tokenArray[0],message);
        }
    }
}
