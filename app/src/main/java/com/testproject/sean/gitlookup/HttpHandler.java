package com.testproject.sean.gitlookup;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by Sean on 3/14/2015.
 */
public class HttpHandler {

    public HttpHandler(){

    }

    /*
    @return true if connection works
     */
    public JSONArray getFromGit(String address){
        BufferedReader in = null;
        String data = "";

        try{
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("https://api.github.com" + address);
            HttpGet request = new HttpGet();
            request.getParams().setParameter("type", "all");
            request.setURI(website);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String nl = System.getProperty("line.separator");

            while((line = in.readLine()) != null){
                sb.append(line + nl);
            }
            in.close();
            data = sb.toString();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        try {
            JSONArray json = new JSONArray(data);
            return json;
        } catch(Exception e){
            System.out.println(e);
        }

        return null;
    }

    public String searchGitUsers(String user, int page){
        BufferedReader in = null;
        String data = "";

        try{
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("https://api.github.com/search/users?q=" + user + "&page=" + page);
            HttpGet request = new HttpGet();

            request.setURI(website);
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String nl = System.getProperty("line.separator");

            while((line = in.readLine()) != null){
                sb.append(line + nl);
            }
            in.close();
            data = sb.toString();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public String getRepoStatistics(String user, String repo){
        BufferedReader in = null;
        String data = "";

        try{
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("https://api.github.com/repos/" + user + "/" + repo + "/commits");
            HttpGet request = new HttpGet();

            request.setURI(website);
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String nl = System.getProperty("line.separator");

            while((line = in.readLine()) != null){
                sb.append(line + nl);
            }
            in.close();
            data = sb.toString();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
