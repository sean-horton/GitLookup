package com.testproject.sean.gitlookup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainApplication extends Activity {

    private HttpHandler httpHandler = new HttpHandler();
    private Context context;
    private ArrayList<String> repos = new ArrayList<String>();
    private ArrayList<String> forksCount = new ArrayList<String>();
    private ArrayList<String> users = new ArrayList<String>();
    private LinearLayout progressBar = null;
    private ListView list = null;
    private String user = null;
    private int page = 2;
    private static int PAGE_ITEMS = 30;
    private AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
    private AlphaAnimation fadeInAnimation = new AlphaAnimation(0f, 1f);
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Stop keyboard from opening on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main_application);

        //Store context for later use
        context = getApplicationContext();

        //Ready progress bar wheel thing
        progressBar = (LinearLayout) findViewById(R.id.headerProgress);

        //Get Fade out animation ready for progress bar
        fadeOutAnimation.setDuration(500);
        fadeInAnimation.setDuration(500);

        //Ready list view for users
        list = (ListView) findViewById(R.id.users_ListView);

        //Add listeners
        addListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Adds listeners for 'GO' button, ListView scrolling and pressing ListView item.
     */
    private void addListeners(){

        //Listen for go button press
        final Button button = (Button) findViewById(R.id.go_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //clean up any previous attempts
                users.clear();

                final EditText textField = (EditText) findViewById(R.id.git_user_text);
                user = textField.getText().toString();

                hideSoftKeyboard(MainApplication.this);

                GetUsersTask task = new GetUsersTask();
                task.execute();

            }
        });


        //Create listener - if click item, open a new Activity for repos
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos, long id) {

                repos.clear();

                TextView textView = (TextView) viewClicked.findViewById(R.id.users_text_view);
                Toast.makeText(context, textView.getText().toString(), Toast.LENGTH_SHORT).show();

                GetRepoTask task = new GetRepoTask();
                task.execute(textView.getText().toString());

            }
        });

        // On scroll listener to update page if at bottom of list - CURRENTLY NOT USED
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView lw, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //at bottom refresh page only if greater than items per page
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount >= PAGE_ITEMS) {
                    //GetUsersTask task = new GetUsersTask();
                    //task.execute();
                    //setPage(getPage() + 1);
                }

            }
        });
    }

    /*
    general user hide keyboard on button click
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /*
    Creates a new activity based on repos for a particular user
     */
    public void updateReposUi(String user){

        Intent intent = new Intent(getApplicationContext(), RepoActivity.class);
        intent.putStringArrayListExtra("repos", repos);
        intent.putStringArrayListExtra("forks_count", forksCount);
        intent.putExtra("user", user);
        startActivity(intent);

    }

    /*
    Updates ListView list to contain new users
     */
    public void updateUsersUi() {
        //Set adapter and redraw list
        adapter = new ArrayAdapter<String>(this, R.layout.users_layout, R.id.users_text_view, users);
        list.setAdapter(adapter);
        list.invalidateViews();
    }

    /*
    Get the page that we are looking at on GitHub
    @Return page
     */
    private int getPage(){
        return page;
    }

    /*
    set the page we are looking on github
     */
    private void setPage(int page){
        this.page = page;
    }

    /*
    Makes an Http request to github to get users - then updates UI
     */
    private class GetUsersTask extends AsyncTask<Integer, Void, Void>{

        private ArrayList<String> usersTemp = new ArrayList<String>();

        //Show loading image
        @Override
        protected void onPreExecute(){
            //make spinner in place of ListView
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAnimation(fadeInAnimation);
        }

        @Override
        protected Void doInBackground(Integer... page){
            String result;

            user = user.replaceAll(" ", "+");
            if(page == null)
                result = httpHandler.searchGitUsers(user, page[0]);
            else
                result = httpHandler.searchGitUsers(user, 1);

            System.out.println(result);

            //Grab users
            String findName = "\"login\":";

            while (result.contains(findName)) {
                result = result.substring(result.indexOf(findName) + 9);
                String aUser = result.substring(0, result.indexOf("\""));

                usersTemp.add(aUser);
                System.out.println(aUser);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg){
            super.onPostExecute(arg);

            users = usersTemp;
            if(users.size() >= 1) {
                updateUsersUi();
            } else{
                Toast.makeText(context, "No results", Toast.LENGTH_LONG).show();
            }

            progressBar.startAnimation(fadeOutAnimation);
            progressBar.setVisibility(View.GONE);
        }

    }

    /*
    get repos for a specific user - create new Activity for repos page
     */
    private class GetRepoTask extends AsyncTask<String, Void, Void>{

        private String user;
        private ArrayList<String> reposTemp = new ArrayList<String>();
        private ArrayList<String> forksTemp = new ArrayList<String>();

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... user) {
            try {

                this.user = user[0];
                String address = "/users/" + user[0] + "/repos";
                JSONArray result = httpHandler.getFromGit(address);

                System.out.println(result.toString());

                JSONObject obj;
                for(int i = 0; i < result.length(); i++){
                    obj = result.getJSONObject(i);
                    reposTemp.add(obj.get("name").toString());
                    forksTemp.add(obj.get("forks_count").toString());
                }

            } catch (Exception e) {

            }
            return null;
        };

        @Override
        protected void onPostExecute(Void arg){
            super.onPostExecute(arg);

            progressBar.startAnimation(fadeOutAnimation);
            progressBar.setVisibility(View.GONE);

            repos = reposTemp;
            forksCount = forksTemp;

            if(repos.size() >= 1) {
                updateReposUi(user);
            } else{
                Toast.makeText(context, "This user has no repos!", Toast.LENGTH_LONG).show();
            }
        }
    }
}





