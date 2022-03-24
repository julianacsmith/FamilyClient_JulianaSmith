package com.example.familyclient_julianasmith;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Request.*;
import Result.*;
import java.net.*;
import java.io.*;
import java.sql.SQLOutput;

public class ServerProxy { // ServerFacade

    public ServerProxy(){}

    public static void main(String[] args) {
        }

    public LoginResult login(String serverHost, String serverPort, LoginRequest request){
        LoginResult result = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login"); // Create a URL with the host, port, and correct path

            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Open up the connection the the URL
            http.setRequestMethod("POST"); // Login is a POST request
            http.setDoOutput(false); // http will include a request body
            http.connect();

            String reqData = gson.toJson(request);
            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(reqData, reqBody);
            reqBody.close(); // Request? Done

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                result = (LoginResult) gson.fromJson(respData, LoginResult.class);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                result = (LoginResult) gson.fromJson(respData, LoginResult.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public RegisterResult register(String serverHost, String serverPort, RegisterRequest request){
        RegisterResult result = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register"); // Create a URL with the host, port, and correct path

            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Open up the connection the the URL
            http.setRequestMethod("POST"); // Login is a POST request
            http.setDoOutput(false); // http will include a request body
            http.connect();

            String reqData = gson.toJson(request);
            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(reqData, reqBody);
            reqBody.close(); // Request? Done

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                result = (RegisterResult) gson.fromJson(respData, RegisterResult.class);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                result = (RegisterResult) gson.fromJson(respData, RegisterResult.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public PersonResult getPeople(String serverHost, String serverPort, String authToken){
        PersonResult result = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person"); // Create a URL with the host, port, and correct path

            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Open up the connection the the URL
            http.setRequestMethod("GET"); // getPerson is a GET request
            http.setDoOutput(false); // http will include a request body
            http.addRequestProperty("Authorization", authToken);
            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                result = (PersonResult) gson.fromJson(respData, PersonResult.class);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                result = (PersonResult) gson.fromJson(respData, PersonResult.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public PersonIDResult getPerson(String serverHost, String serverPort, String authToken, String personID){
        PersonIDResult result = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/" + personID); // Create a URL with the host, port, and correct path

            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Open up the connection the the URL
            http.setRequestMethod("GET"); // getPerson is a GET request
            http.setDoOutput(false); // http will include a request body
            http.addRequestProperty("Authorization", authToken);
            http.connect();

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                result = (PersonIDResult) gson.fromJson(respData, PersonIDResult.class);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                result = (PersonIDResult) gson.fromJson(respData, PersonIDResult.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public EventResult getEvents(){
        return null;
    }

    /*
		The readString method shows how to read a String from an InputStream.
	*/
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
