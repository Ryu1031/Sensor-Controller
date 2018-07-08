package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by Ryusuke on 2016/08/11.
 */
public class AppUtil {
    private static final String DATA_SAVE = "data_save";

    private static final String SAVE_IPADDRESS = "ip_address";

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void saveIpAddress(Context context, String ipaddress) {
        SharedPreferences preferences =
                context.getSharedPreferences(DATA_SAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SAVE_IPADDRESS, ipaddress);
        editor.apply();
    }

    public static String getIpAdress(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(DATA_SAVE, Context.MODE_PRIVATE);
        return preferences.getString(SAVE_IPADDRESS, "");
    }
}
