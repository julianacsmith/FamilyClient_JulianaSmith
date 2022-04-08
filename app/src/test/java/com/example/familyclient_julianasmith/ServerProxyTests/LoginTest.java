package com.example.familyclient_julianasmith.ServerProxyTests;
import androidx.navigation.Navigator;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.ServerProxy;

import org.junit.Test;
import static org.junit.Assert.*;
import Request.*;
import Result.*;

public class LoginTest {
    @Test
    public void successLogin(){
        DataCache cache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy();
        String username = "GoodUsername";
        String password = "GoodPassword";
        String localHost = "localhost";
        String localPort = "8080";

        RegisterRequest registerRequest = new RegisterRequest(username, password, "goodEmail", "goodFirstName", "goodLastName", "m");
        RegisterResult registerResult = proxy.register(localHost, localPort, registerRequest);
        assertTrue(registerResult.isSuccess());

        LoginRequest request = new LoginRequest(username, password);
        LoginResult result = proxy.login(localHost, localPort, request);

        assertTrue(result.isSuccess());
    }

    @Test
    public void failedLogin(){

    }


}
