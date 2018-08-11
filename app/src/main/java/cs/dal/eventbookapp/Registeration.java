package cs.dal.eventbookapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.example.eventbookapp.HomeActivity;
import com.example.eventbookapp.R;

import java.util.ArrayList;

import DataProvider.EventUsersDataProvider;
import Models.EventUsers;

public class Registeration extends AppCompatActivity {


    // Declaring the object for all edittexts and Button

    EditText fullname , emailid , password ,confirmpassword ;
    CardView btnsignup;
    Boolean Ismatch = false;
    Boolean Iscreate = true;
    EventUsers IsUserregistered;
    String username;
    String email;
    String pass;
    Button signin;
    String emailtext;
    boolean isAdmin =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        /*
         * Initialize the necessary values
         *
         */

        fullname = (EditText) findViewById(R.id.Name);
        emailid = (EditText)findViewById(R.id.Regemail);
        password = (EditText)findViewById(R.id.Rpassword);
        confirmpassword = (EditText)findViewById(R.id.Rconfirmpassword);
        btnsignup = (CardView)findViewById(R.id.signup);
        signin =(Button)findViewById(R.id.signIn);
        ((CheckBox)findViewById(R.id.checkBoxAdmin)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             isAdmin=b;
            }
        });

        /*
         * Sign up button OnClick Listener
          * Get the necessary values from the form
          * Validate all the necessary fields
          * Email address - validation
          * Empty fields validation
          * validation to check whether the password match when he has entered twice
          *
         */

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               username = fullname.getText().toString();
                email = emailid.getText().toString();
                pass = password.getText().toString();
                emailtext = emailid.getText().toString().trim();

                // to validate a email
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (emailtext.matches(emailPattern)){
                    Iscreate = true;
                }
                else{
                    Iscreate = false;
                    emailid.setError("Email Id is not a valid , Enter the correct email id");
                }



                if (fullname.getText().toString().length()== 0){
                    fullname.setError("Name not entered");
                    fullname.requestFocus();
                    Iscreate = false;
                }
                if (emailid.getText().toString().length()==0){
                    emailid.setError("Enter the email Address");
                    emailid.requestFocus();
                    Iscreate = false;
                }

                if (password.getText().toString().length() == 0){
                    password.setError("Enter the Password");
                    password.requestFocus();
                    Iscreate = false;
                }

                if (password.getText().toString().length() < 5){
                    password.setError("Minimum length of the passowrd should be 5 Characters");
                    password.requestFocus();
                    Iscreate = false;
                }


                if (confirmpassword.getText().toString().length() == 0){
                    confirmpassword.setError("Enter the Confirm Password");
                    confirmpassword.requestFocus();
                    Iscreate = false;
                }

                if (password.getText().toString().equals(confirmpassword.getText().toString())){
                    Ismatch = true;

                }
                if (Ismatch == false){
                    Iscreate = false;
                    confirmpassword.setError("Password Not matched , Enter the correct Password");

                }

                if (Iscreate && Ismatch){
                    createuser();
                }

            }
        });


        /*
         *  Redirecting user from registration page to login page when user has already registered and by mistake enters registration page
         */

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(Registeration.this,LoginPage.class);
                startActivity(intent1);

            }
        });

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     *
     * Method Name : createuser
     *
     * Funtionalites: Async task to create a new user in the DB
     *
     * 1) Get the necessary values
     * 2) Create a object for eventUsersDataProvider and set the necessary values
     * 3) Create a new user
     * 4) If the email id already exists, show that the user has already registered
     * 5) If email is not present, register the user and show toasts
     *
     */

    private void createuser(){
        AsyncTask<Void,Void,Void> registerasync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                EventUsersDataProvider eventUsersDataProvider = new EventUsersDataProvider();
                EventUsers newUser = new EventUsers() ;
                newUser.setUserName(username);
                newUser.setEmailAddress(email.toLowerCase());
                newUser.setPassword(pass);
                newUser.setHasAdminRights(isAdmin);
                // Check for Dupicate User
                  ArrayList<EventUsers> usersFound =  eventUsersDataProvider.GetEventUserbyEmail(newUser.getEmailAddress().toLowerCase());
                if(usersFound.size()>0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailid.setError("Email Id already Exist. Please Login.");
                        }
                    });

                    return  null;
                }

               IsUserregistered = eventUsersDataProvider.CreateEventUsers(newUser);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (IsUserregistered!=null){
                            Toast.makeText(getApplicationContext(),"Registeration successful",Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Registeration.this,HomeActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                        else {
                            Log.d("User Registered?",""+IsUserregistered);
                        }
                    }
                });
                return null;

            }
        };
        runAsyncTask(registerasync);


    }
}





