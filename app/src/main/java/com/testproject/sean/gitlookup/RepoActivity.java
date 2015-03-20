package com.testproject.sean.gitlookup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sean on 3/14/2015.
 */
public class RepoActivity extends Activity {

    private ArrayList<String> repos;
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
        dialog = new Dialog(RepoActivity.this);
        textView1 = new TextView(RepoActivity.this);

        showRepos();
    }

    public void showRepos() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.repo_layout, R.id.repo_name_text, repos);

        ListView list = (ListView) findViewById(R.id.repo_ListView);
        list.setAdapter(adapter);

        list.invalidateViews();

        // Get some extra information if you click on a repo
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos, long id) {
                TextView textView = (TextView) viewClicked.findViewById(R.id.repo_name_text);

                String[] info = {user, textView.getText().toString()};
                GetRepoStatTask task = new GetRepoStatTask();

                task.execute(info);
            }
        });
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
