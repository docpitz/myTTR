package de.ssp.service.mytischtennis.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;

import java.util.Date;

import de.ssp.service.mytischtennis.caller.ServiceFinish;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;

public class MyTischtennisService<S, F> extends AsyncTask<String, Void, Integer> {
    protected String errorMessage;
    protected Context context;
    protected ServiceFinish<S, F> serviceFinish;
    protected ParserEvaluator<S, F> parserEvaluation;
    protected F serviceReturnObject;
    protected boolean success;
    private ProgressDialog progressDialog;
    protected static long lastServiceCallTimestamp;
    protected String dialogMessage;

    public static boolean isLoginNecessary()
    {
        // Normalerweise sollte man dies über die Cookies direkt lösen
        long timeNow = new Date().getTime();
        return lastServiceCallTimestamp + 1000 * 30 * 60 < timeNow;
    }

    public MyTischtennisService(Context context, ParserEvaluator<S, F> parserEvaluation, String dialogMessage, ServiceFinish<S, F> serviceFinish) {
        if (context == null) throw new IllegalArgumentException("context must not be null");
        this.success = false;
        this.context = context;
        this.parserEvaluation = parserEvaluation;
        this.dialogMessage = dialogMessage;
        this.serviceFinish = serviceFinish;
    }

    @Override
    protected void onPreExecute() {
        if (dialogMessage != null && progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(dialogMessage + ", bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            long start = System.currentTimeMillis();
            serviceReturnObject = parserEvaluation.evaluateParser();
            success();
            Log.i(Constants.LOG_TAG, "parser time " + (System.currentTimeMillis() - start) + " ms");
        } catch (ValidationException e) {
            errorMessage = e.getMessage();
            success = false;
            Log.i(Constants.LOG_TAG, e.getMessage());
        } catch (LoginExpiredException e) {
            try {
                new LoginManager().logout();
                new LoginManager().relogin();
                serviceReturnObject = parserEvaluation.evaluateParser();
                success();
            } catch (Exception e2) {
                new LoginManager().logout();
                errorMessage = "Das erneute Anmelden war nicht erfolgreich";
                Log.e(Constants.LOG_TAG, "", e2);
                success = false;
            }
        } catch (NoClickTTException e) {
            Log.d(Constants.LOG_TAG, "second try after no data msg");
            success = false;
            try {
                serviceReturnObject = parserEvaluation.evaluateParser();
                Log.d(Constants.LOG_TAG, "success");
                success();
            } catch (NoClickTTException e2) {
                logError(e);
                success = false;
                errorMessage = new MyTTClickTTParserImpl().parseError(Client.lastHtml);
            }
            catch (Exception e2) {
                logError(e);
                success = false;
                errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
            }
        } catch (NetworkException e) {
            success = false;
            errorMessage = "Das Netzwerk antwortet zu langsam oder ist ausgeschaltet";
        }
        catch (NoDataException ed) {
            logError(ed);
            success = false;
            errorMessage = "Mytischtennis.de meldet: " + ed.getMessage();
        }
        catch (NiceGuysException e) {
            success = false;
        }catch (Exception e) {
//            catch all others
            success = false;
            logError(e);
            errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
        }
        return null;
    }

    protected void success()
    {
        success = true;
        lastServiceCallTimestamp = new Date().getTime();
    }

    private void logError(Exception e) {
        Log.e(Constants.LOG_TAG, "Error reading " + Client.lastUrl(), e);
        Log.e(Constants.LOG_TAG, getLastH1());

    }

    private String getLastH1() {
        if (Client.lastHtml != null)
            try {
                int start = Client.lastHtml.indexOf("<p class=\"alert alert-danger\"");
                return Client.lastHtml.substring(Client.lastHtml.indexOf("<p class=\"alert alert-danger\""),
                        Client.lastHtml.indexOf("</h1>"));
            } catch (Exception e) {
                return "no h1 found";
            }
        return "--";
    }

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            //see myttr-62
        }
        if (errorMessage == null && serviceReturnObject == null) {
            Log.d(Constants.LOG_TAG, "couldn't load data in class " + getClass());
            errorMessage = "Konnte die Daten nicht laden (Grund unbekannt)";
        }
        serviceFinish.serviceFinished(parserEvaluation.getPostElement(), success, serviceReturnObject, errorMessage);
    }
}
