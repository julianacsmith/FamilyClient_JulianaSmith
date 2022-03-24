package com.example.familyclient_julianasmith;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import Models.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    private ServerProxy proxy;

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

    private Listener listener;

    public interface Listener{
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
                    registerButton.setEnabled(false);
                }
            } else {
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);
        localHostField = view.findViewById(R.id.localHostField);
        localPortField = view.findViewById(R.id.localPortField);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        firstNameField = view.findViewById(R.id.firstNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        emailField = view.findViewById(R.id.emailAddressField);
        genderField = view.findViewById(R.id.gender);

        proxy = new ServerProxy();
        allFieldsFilled = false;

        localHostField.addTextChangedListener(textWatcher);
        localPortField.addTextChangedListener(textWatcher);
        usernameField.addTextChangedListener(textWatcher);
        passwordField.addTextChangedListener(textWatcher);
        firstNameField.addTextChangedListener(textWatcher);
        lastNameField.addTextChangedListener(textWatcher);
        emailField.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Handler loginHandler = new Handler();
                Handler personHandler = new Handler();
                LoginRequest request = new LoginRequest(username, password);
                Runnable loginTask = new Runnable() {
                    private LoginResult result;
                    private PersonResult personResult;
                    @Override
                    public void run() {
                        result = proxy.login(localHost, localPort, request);
                        loginHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(result == null || !result.isSuccess()){
                                    Toast.makeText(getActivity(), "Invalid Login", Toast.LENGTH_SHORT).show();
                                } else {
                                    Runnable personTask = new Runnable() {
                                        @Override
                                        public void run() {
                                            personResult = proxy.getPeople(localHost, localPort, result.getAuthToken());
                                            DataCache cache = DataCache.getInstance();
                                            cache.setPeople(personResult);
                                            Person user = cache.getPersonByID(result.getPersonID());
                                            String firstName = user.getFirstName();
                                            String lastName = user.getLastName();
                                            personHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(), "Successful Login! Welcome " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    };
                                    Thread personThread = new Thread(personTask);
                                    personThread.start();
                                }
                            }
                        });
                    }
                };
                Thread loginThread = new Thread(loginTask);
                loginThread.start();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Handler registerHandler = new Handler();

                RegisterRequest request = new RegisterRequest(username, password, email, firstName, lastName, gender);
                Runnable registerTask = new Runnable() {
                    private RegisterResult result;
                    @Override
                    public void run() {
                        result = proxy.register(localHost, localPort, request);

                        registerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(result == null || !result.isSuccess()){
                                    Toast.makeText(getActivity(), "Invalid Register", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Successful Register! Welcome " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    public RegisterResult getResult() {
                        return result;
                    }
                };
                Thread registerThread = new Thread(registerTask);
                registerThread.start();
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

        return view;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case (R.id.loginButton):
                break;
            case (R.id.registerButton):
                break;
        }
    }
}