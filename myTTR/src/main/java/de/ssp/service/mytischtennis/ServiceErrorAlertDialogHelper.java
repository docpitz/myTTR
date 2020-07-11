package de.ssp.service.mytischtennis;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class ServiceErrorAlertDialogHelper
{
    public static boolean showErrorDialog(Context context, boolean success, String errorMessage)
    {
        if(! success || errorMessage != null)
        {
            errorMessage = errorMessage != null && !errorMessage.isEmpty() ? errorMessage : "Fehler unbekannt";
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle("Fehler beim Serviceaufruf")
                    .setMessage("Beim Serviceaufruf ist ein Fehler aufgetreten. \nFehler:\n"+errorMessage)
                    .setPositiveButton("Ok", null);
            dialogBuilder.create().show();
            return true;
        }
        return false;
    }
}
