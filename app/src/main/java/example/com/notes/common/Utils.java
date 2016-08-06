package example.com.notes.common;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class Utils {

    public static AlertDialog.Builder getAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        return dialogBuilder;
    }

}
