package de.ssp.service.mytischtennis.caller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;

import java.util.concurrent.Executor;

import de.ssp.service.mytischtennis.asynctask.MyTischtennisService;
import de.ssp.service.mytischtennis.model.MyTischtennisCredentials;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;

public abstract class MyTischtennisEnsureLoginCaller<S, F> implements ServiceCaller, ServiceFinish<String, User>
{
    protected ServiceFinish<S, F> serviceFinish;
    protected Context context;
    protected String dialogMessage;
    protected Executor executor;
    protected MyTischtennisService<S, F> myTischtennisService;
    protected ServiceCallerLogin loginCaller;

    public MyTischtennisEnsureLoginCaller(Context context, String dialogMessage, ServiceFinish<S, F> serviceFinish, Executor executor)
    {
        this.context = context;
        this.dialogMessage = dialogMessage;
        this.serviceFinish = serviceFinish;
        this.executor = executor;
    }

    public MyTischtennisEnsureLoginCaller(Context context, String dialogMessage, ServiceFinish<S, F> serviceFinish)
    {
        this(context, dialogMessage, serviceFinish, null);
    }

    protected void callLoggedInService()
    {
        myTischtennisService = new MyTischtennisService<>(context, getParserEvaluator(), dialogMessage, serviceFinish);
        Log.d(this.toString(), "Service started");
        if(executor != null)
        {
            myTischtennisService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            myTischtennisService.execute();
        }
    }

    public void cancelService()
    {
        if(isServiceRunning())
        {
            Log.d(this.toString(), "Service stopped");
            myTischtennisService.cancel(true);
        }
        if(loginCaller != null && loginCaller.isServiceRunning())
        {
            Log.d(loginCaller.toString(), "Service stopped");
            loginCaller.cancelService();
        }
    }

    public boolean isServiceRunning()
    {
        return myTischtennisService != null && myTischtennisService.getStatus() == AsyncTask.Status.RUNNING;
    }

    protected abstract ParserEvaluator<S, F> getParserEvaluator();

    private boolean loginIfNecessary()
    {
        if(!LoginManager.existLoginCookie() || LoginManager.isLoginExpired() || MyTischtennisService.isLoginNecessary())
        {
            MyTischtennisCredentials credentials = new MyTischtennisCredentials(context);
            loginCaller = new ServiceCallerLogin(context, this, credentials.getUsername(), credentials.getPassword());
            loginCaller.callService();
            return true;
        }
        return false;
    }

    @Override
    public void serviceFinished(String requestValue, boolean success, User user, String errorMessage)
    {
        // Anmeldeservice ist erfolgt
        if(success && user != null)
        {
            callLoggedInService();
        }
        else
        {
            serviceFinish.serviceFinished(null, success, null, errorMessage);
        }
    }

    @Override
    public void callService()
    {
        if(! loginIfNecessary())
        {
            callLoggedInService();
        }
    }
}
