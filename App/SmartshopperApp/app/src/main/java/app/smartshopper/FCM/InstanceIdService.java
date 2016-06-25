package app.smartshopper.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Felix on 22.06.2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService
{
    private static final String TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh()
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        // TODO sync token with backend
        Log.e(TAG, "!!!!!!!!!!!!!!!!!! Got token: " + token);
    }
}
