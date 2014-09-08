package pl.michalkasza.r4stest;

import android.app.Fragment;
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

    String bad_MSG, final_URL, shrinked_URL, base_URL = "http://to.ly/api.php?&longurl=";
    EditText edited_URL;
    ImageButton button;
    MySQLiteHelper db;
    Toast m_currentToast;

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
        bad_MSG = "Invalid URL: " + edited_URL;

        AsyncRequestTask get = new AsyncRequestTask();
        get.execute(final_URL, bad_MSG);
        showToast("Przetwarzanie w trakcie... ");
//      AsyncDbWriteTask push = new AsyncDbWriteTask();
//      push.execute(edited_URL.getText().toString());
    }

    void showToast(String text)
    {
        if(m_currentToast == null)
        {
            m_currentToast = Toast.makeText(this.getActivity(), text, Toast.LENGTH_LONG);
        }
        m_currentToast.setText(text);
        m_currentToast.setDuration(Toast.LENGTH_LONG);
        m_currentToast.show();
    }

    private class AsyncDbWriteTask extends AsyncTask<String, Void, Void>{
        protected Void doInBackground(String... values) {
            String temped_URL = values[0];

            try {
                do{
                    if(shrinked_URL != null)
                        db.addLink(new Link(temped_URL, shrinked_URL));
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
            return null;
        }
    }
    private class AsyncRequestTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... values) {
            String query_URL = values[0];
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
                Log.e("AsyncHTTPRequest: ", "Error");
            }

            if(shrinked_URL.startsWith("Invalid")) {
//                showToast("Niepoprawny adres :(");
            }
            else {
                AsyncDbWriteTask push = new AsyncDbWriteTask();
                push.execute(edited_URL.getText().toString());
//                showToast("Skrót dodany do bazy! :) ");
            }
            return null;
        }
    }
}

