package com.testproject.sean.gitlookup.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.testproject.sean.gitlookup.Backend.HttpHandler;
import com.testproject.sean.gitlookup.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sean on 3/27/2015.
 */
public class CommitActivity extends Activity{

    private String user;
    private String repo;
    private Context context;
    private ProgressBar progressBar;
    private ArrayList<String> message = new ArrayList<String>();
    private ArrayAdapter adapter;
    private ListView list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commits);

        this.user = getIntent().getStringExtra("user");
        this.repo = getIntent().getStringExtra("repo");
        this.context = getApplicationContext();
        progressBar = (ProgressBar) findViewById(R.id.commits_progressBar);
        progressBar.setAlpha(0f);
        list = (ListView) findViewById(R.id.commits_listView);

        //Set Repo name
        TextView textView = (TextView) findViewById(R.id.textView_commits_repo);
        textView.setText("Repo: " + repo);

        //if the strings aren't empty, do something... they should never be empty but catch it
        if(user.compareTo("") != 0 && repo.compareTo("") != 0){
            getCommits();
        } else {
            finish();
        }
    }

    private void getCommits(){
        getCommitsTask task = new getCommitsTask();
        task.execute();
    }

    public void createListView(){
        adapter = new ArrayAdapter<String>(this, R.layout.users_layout, R.id.users_text_view, message);
        list.setAdapter(adapter);
        list.invalidateViews();
    }

    private class getCommitsTask extends AsyncTask<Void, Void, Void>{

        private String results;
        private ArrayList<String> messages = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            progressBar.setAlpha(1f);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            results = new HttpHandler().getRepoStatistics(user, repo);
            System.out.println(results);
            parseJson(results);

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {

            //remove progressBar
            progressBar.animate().alpha(0f).setDuration(500);
            progressBar.setVisibility(View.GONE);

            message = messages;
            createListView();
        }

        private void parseJson(String results){

            try {
                JSONArray json = new JSONArray(results);
                JSONObject obj = new JSONObject();
                for(int i = 0; i < json.length(); i++){
                    obj = json.getJSONObject(i);
                    obj = obj.getJSONObject("commit");
                    messages.add(obj.get("message").toString());
                }
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }

}
