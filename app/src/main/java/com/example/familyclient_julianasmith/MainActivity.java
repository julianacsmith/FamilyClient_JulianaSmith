package com.example.familyclient_julianasmith;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import android.view.*;

import Request.LoginRequest;
import Request.RegisterRequest;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    private EditText localHostField;
    private EditText localPortField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioGroup genderField;

    private String localHost;
    private String localPort;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    private boolean allFieldsFilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        localHostField = findViewById(R.id.localHostField);
        localPortField = findViewById(R.id.localPortField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailAddressField);
        genderField = findViewById(R.id.gender);

        allFieldsFilled = false;

        localHostField.addTextChangedListener(textWatcher);
        localPortField.addTextChangedListener(textWatcher);
        usernameField.addTextChangedListener(textWatcher);
        passwordField.addTextChangedListener(textWatcher);
        firstNameField.addTextChangedListener(textWatcher);
        lastNameField.addTextChangedListener(textWatcher);
        emailField.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //Establish conenction to the server
                //If success, then react the request and log the user in via the port
                    // Then swap fragments
                //If fail, display failed to log user in
                LoginRequest request = new LoginRequest(username, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //Establish conenction to the server
                //If success, then react the request and log the user in via the port
                    // Then swap fragments
                //If fail, display failed to log user in
                RegisterRequest request = new RegisterRequest(username, password, email, firstName, lastName, gender);
            }
        });

        genderField.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.gender_Male){ // If the id is male
                    gender = "m";
                } else {
                    gender = "f";
                }
                if(allFieldsFilled){
                    registerButton.setEnabled(true);
                }
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            localHost = localHostField.getText().toString();
            localPort = localPortField.getText().toString();
            username = usernameField.getText().toString();
            password = passwordField.getText().toString();
            firstName = firstNameField.getText().toString();
            lastName = lastNameField.getText().toString();
            email = emailField.getText().toString();

            if(!localHost.isEmpty() && !localPort.isEmpty()){ // If the host an port are filled out
                if(!username.isEmpty() && !password.isEmpty()){ // If all the login fields are satisfied
                    loginButton.setEnabled(true); // Enable the login button b/c all params are filled
                    if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && gender != null){ // If you refill all the fields and the gender button was already clicked before
                        registerButton.setEnabled(true);
                    } else if(!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()){ // If the first and last name and email are filled and gender hasn't been selected yet
                        allFieldsFilled = true; // Set the parameter to true. Now we wait on the gender
                    } else { // If any of the fields are cleared
                        registerButton.setEnabled(false); // Disable the register button
                    }
                } else { // If a password or username is removed
                    loginButton.setEnabled(false); // disable the login button
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}