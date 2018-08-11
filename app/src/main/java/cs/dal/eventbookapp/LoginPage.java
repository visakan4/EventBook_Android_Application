package cs.dal.eventbookapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventbookapp.DashboardActivity;
import com.example.eventbookapp.R;

import java.util.ArrayList;

import DataProvider.EventUsersDataProvider;
import DataProvider.ServiceDataProvider;
import Models.EventUsers;

/**
 *
 * Class Name: LoginPage
 *
 * Functionality: To allow the users login to EventBook application
 *
 */

public class LoginPage extends AppCompatActivity {

    /**
     * Initialize the necessary values
     */

    EditText l_emailid, l_password;
    CardView btnloginpage;
    String email;
    String logpassword;
    boolean Isvalidate = true;
    boolean Issuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        /*
         * Initialize the values needed
         */

        l_emailid = (EditText) findViewById(R.id.loginemail);
        l_password = (EditText) findViewById(R.id.loginpassword);
        btnloginpage = (CardView)findViewById(R.id.cardView);


        /*
            OnClick Listener for login button
            On login, validate the user name and password, check whether the user entered values are valid.
            If valid, navigate the user to dashboardactivity
            If not valid, show proper error message
         */

        btnloginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = l_emailid.getText().toString();
                logpassword = l_password.getText().toString();

                if (l_emailid.getText().toString().length()== 0){
                    l_emailid.setError("Enter the Email Address");
                    l_emailid.requestFocus();
                    Isvalidate = false;
                }

                if (l_password.getText().toString().length()==0){
                    l_password.setError("Enter the password to login");
                    l_password.requestFocus();
                    Isvalidate =false;
                }

                if (true==Isvalidate){
                   loginapp();
                }

            }
        });

    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            //check
            return task.execute();
        }
    }


    /**
     *
     * Method Name :  loginapp
     *
     * Functionalities: Async task to validate the login credentials of the user
     *
     */

    private void loginapp(){
        AsyncTask<Void,Void,Void> loginasync = new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                EventUsersDataProvider eventUsersDataProvider = new EventUsersDataProvider();
                final ArrayList<EventUsers> loginemail = eventUsersDataProvider.GetEventUserbyEmail(email);

                if (!loginemail.isEmpty()){
                    if (email.equalsIgnoreCase(loginemail.get(0).getEmailAddress())){
                        if (logpassword.equalsIgnoreCase(loginemail.get(0).getPassword()) ) {
                            Issuccess = true;
                        }else{
                           Issuccess =false;
                        }
                    }else{
                        Issuccess = false;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Issuccess){
                            ServiceDataProvider.getGlobalInstance().setNormalLogin(true);
                            ServiceDataProvider.getGlobalInstance().setCurrentuser(loginemail.get(0));
                            Intent intent1 = new Intent(LoginPage.this,DashboardActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Login Failed , user name and password doesn't match",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return null;
            }
        };
        runAsyncTask(loginasync);
    }
}

