package com.example.eventbookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;
import DataProvider.ServiceDataProvider;
import cs.dal.eventbookapp.LoginPage;
import cs.dal.eventbookapp.Registeration;

/**
 *
 * Class Name : HomeActivity
 *
 * Funtionalities: Login screen to make connection to the service
 *
 */


public class HomeActivity extends Activity {

    public static MobileServiceClient mClient;
    public static boolean UserLoggedIn;
    public static MobileServiceUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UserLoggedIn =false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button btnFaceBookLogin = (Button) findViewById(R.id.btnLoginFaceBook);
        btnFaceBookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                 * Authentication of the user via Facebook(OAuth)
                 *
                 */

                mClient.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
                    @Override
                    public void onCompleted(MobileServiceUser user, Exception exception, ServiceFilterResponse response) {
                        currentUser = user;
                        if(exception==null){
                            UserLoggedIn = true;
                            Intent intent = new Intent(HomeActivity.this,DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            UserLoggedIn = false;
                        }
                    }
                });
            }
        });

        ConnectToService();

        /*
         * Login Button OnClick Listener
          * On click navigate the user to the login page.
         */

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        /*
            OnClick Listener for Register Button
            Navigate the user to the registeration page
         */

        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Registeration.class);
                startActivity(intent);
            }
        });
    }


    /**
     *
     * Method Name: ConnectToService
     *
     * Functionalites: To make a OkHTTP connection call
     *
     */

    public void ConnectToService(){
                try {
                    ServiceDataProvider.Instantiate(this);
                    mClient = ServiceDataProvider.getGlobalInstance().getMobileServiceClient();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Extending the default timeout of 10s to 20s
                mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient client = new OkHttpClient();
                        client.setReadTimeout(20, TimeUnit.SECONDS);
                        client.setWriteTimeout(20, TimeUnit.SECONDS);
                        return client;
                    }
                });
    }


    private class ClientFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {
            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();
            //runOnUiThread(new Runnable() {
            // @Override
            //public void run() {
            // if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
            //}
            //});
            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);
            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    //  runOnUiThread(new Runnable() {

                    //@Override
                    //public void run() {
                    //  if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                    //}
                    //});

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}

