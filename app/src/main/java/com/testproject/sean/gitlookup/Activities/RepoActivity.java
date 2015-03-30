package com.testproject.sean.gitlookup.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.testproject.sean.gitlookup.Backend.HttpHandler;
import com.testproject.sean.gitlookup.R;
import com.testproject.sean.gitlookup.Adapters.RepoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 3/14/2015.
 */
public class RepoActivity extends Activity {

    private ArrayList<String> repos;
    private ArrayList<String> forks_count;
    private String user;
    private Context context;
    private Dialog dialog;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        this.repos = getIntent().getStringArrayListExtra("repos");
        this.user = getIntent().getStringExtra("user");
        this.forks_count = getIntent().getStringArrayListExtra("forks_count");
        context = this.getApplicationContext();
        dialog = new Dialog(RepoActivity.this);
        textView1 = new TextView(RepoActivity.this);

        //set username at top of activity
        TextView username = (TextView) findViewById(R.id.textview_activity_username);
        username.setText("User: " + user);

        showRepos();
    }

    public void showRepos() {
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.repo_layout, R.id.repo_name_text, repos);

        ListView list = (ListView) findViewById(R.id.repo_ListView);
        List<RepoListItem> items = new ArrayList<RepoListItem>();

        //Set up custom list. This should change how it is generated... Just testing!!!!
        //This could very easily throw null pointers.
        if(repos != null) {
            for (int i = 0; i < repos.size(); i++) {
                items.add(new RepoListItem(list.getId(), repos.get(i), forks_count.get(i)));
            }
        }

        RepoAdapter adapter = new RepoAdapter(this, R.layout.repo_layout, items);

        list.setAdapter(adapter);
        list.invalidateViews();

        // Get some extra information if you click on a repo
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos, long id) {

            //If the repo has commits
            TextView tempView = (TextView) viewClicked.findViewById(R.id.forks_count_text);
            String forksCount = tempView.getText().toString();
            System.out.println("forks Count = " + forksCount);
            if(forksCount.compareTo("0") != 0) {
                TextView textView = (TextView) viewClicked.findViewById(R.id.repo_name_text);

                //Create a new activity
                Intent intent = new Intent(getApplicationContext(), CommitActivity.class);
                intent.putExtra("repo", textView.getText().toString());
                intent.putExtra("user", user);
                startActivity(intent);

                //String[] info = {user, textView.getText().toString()};
                //GetRepoStatTask task = new GetRepoStatTask();

                //task.execute(info);
            } else {
                // say there is nothing to display
                Toast.makeText(context, "This repo has no commits.", Toast.LENGTH_LONG).show();
            }
            }
        });
    }





    public class RepoListItem{
        public int resource;
        public String repo;
        public String fork_count;

        public RepoListItem(int resource, String repo, String fork_count){
            this.resource = resource;
            this.repo = repo;
            this.fork_count = fork_count;
        }

        public String getRepo(){
            return repo;
        }

        public String getForkCount(){
            return fork_count;
        }
    }

    private class GetRepoStatTask extends AsyncTask<String, Void, Void>{

        private String user;
        private String repo;
        private String stats;

        @Override
        protected Void doInBackground(String... info){
            //RepoInfoDialog dialog = new RepoInfoDialog(textView.getText().toString());
            //dialog.sho;
            this.user = info[0];
            this.repo = info[1];

            stats = new HttpHandler().getRepoStatistics(user, repo);
            System.out.println(stats);

            stats = formatStats(stats);

            return null;
        }

        @Override
        protected void onPostExecute(Void arg){
            super.onPostExecute(arg);

            dialog.setTitle(repo);
            textView1.setText(stats);

            dialog.setContentView(textView1);

            dialog.show();

        }

        private String formatStats(String stats){
            String findName = "\"message\"";
            String result = "";

            while (stats.contains(findName)) {
                stats = stats.substring(stats.indexOf(findName) + 11);
                result = stats.substring(0, stats.indexOf("\"")) + "    ";
            }

            return result;
        }
    }

    private class RepoInfoDialog extends DialogFragment {

        private String repo;

        public RepoInfoDialog(String repo){
            this.repo = repo;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(repo)
                    .setCancelable(false)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            return builder.create();
        }

    }
}
