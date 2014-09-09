package pl.michalkasza.r4stest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.michalkasza.r4stest.db.MySQLiteHelper;
import pl.michalkasza.r4stest.model.Link;

public class ShrinkFragment extends Fragment implements View.OnClickListener {

    String final_URL, base_URL = "http://to.ly/api.php?&longurl=";
    EditText edited_URL;
    ImageButton button;
    MySQLiteHelper db;
    Toast mToast;
    boolean online = true;

    public ShrinkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shrink, container, false);
        edited_URL = (EditText) rootView.findViewById(R.id.textEdit);

        button = (ImageButton) rootView.findViewById(R.id.imageButton);
        button.setOnClickListener(this);

        db = new MySQLiteHelper(getActivity());
        return rootView;
    }

    @Override
    public void onClick(View v) {
        final_URL = base_URL + edited_URL.getText();

        online = isOnline();
        if (online == true) {
            AsyncRequestTask get = new AsyncRequestTask();
            get.execute(final_URL, edited_URL.getText().toString());
            showToast("Przetwarzanie w trakcie...");
        } else {
            new AlertDialog.Builder(this.getActivity()).setTitle("Brak połączenia z internetem").setMessage("Akcja została przerwana").setIcon(R.drawable.ic_alert).setNeutralButton("Zamknij", null).show();
        }
    }

    void showToast(String text) {
        if(mToast == null) {
            mToast = Toast.makeText(this.getActivity(), text, Toast.LENGTH_LONG);
        }
        mToast.setText(text);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class AsyncDbWriteTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... values) {
            String editedtmp_URL = values[0];
            String shrinked_URL = values[1];

            try {
                do{
                    if(shrinked_URL != null)
                        db.addLink(new Link(editedtmp_URL, shrinked_URL));
                    else
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {
                            Log.e("AsyncDbWrite", "Sleep'n Error");
                        }
                }while(shrinked_URL == null);
            }catch(Exception e){
                Log.e("AsyncDbWrite", "Add'n Error");
            }
            return editedtmp_URL;
        }
        protected void onPostExecute(String editedtmp_URL) {
            showToast("Skrót " + editedtmp_URL + " dodany do bazy! :)");
        }

    }
    private class AsyncRequestTask extends AsyncTask<String, Void, Boolean> {
        String editedtmp_URL, shrinked_URL;
        protected Boolean doInBackground(String... values) {
            boolean shrink_success = false;
            String query_URL = values[0];
            editedtmp_URL = values[1];
            try {
                ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
                HttpClient http_Client = new DefaultHttpClient();
                HttpPost http_Post = new HttpPost(query_URL);
                http_Post.setEntity(new UrlEncodedFormEntity(nvp));
                HttpResponse http_Response = http_Client.execute(http_Post);
                HttpEntity http_Entity = http_Response.getEntity();
                InputStream input_Stream = http_Entity.getContent();

                BufferedReader br = new BufferedReader(new InputStreamReader(input_Stream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                input_Stream.close();
                shrinked_URL = sb.toString();
                Log.v("Getter:", "Shrinked link: " + shrinked_URL);
            } catch (Exception e) {
                Log.e("AsyncHTTPRequest:", "Error");
            }

            if(shrinked_URL.startsWith("Invalid")) {
                shrink_success = false;
            }
            else {
                shrink_success = true;
            }
            return shrink_success;
        }
        protected void onPostExecute(Boolean shrink_success){
            if (shrink_success == false) {
                showToast("Nieprawidłowy link :(");
            } else {
                AsyncDbWriteTask push = new AsyncDbWriteTask();
                push.execute(editedtmp_URL, shrinked_URL);
            }
        }
    }
}

