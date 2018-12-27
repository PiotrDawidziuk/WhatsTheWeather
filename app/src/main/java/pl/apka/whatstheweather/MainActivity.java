package pl.apka.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);

    }

    public void getWeather(View view){
        try {
            String city = editText.getText().toString();
            String encodedCityName = URLEncoder.encode(city, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather:(", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current  = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather:(", Toast.LENGTH_SHORT).show();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                String mainInfo = jsonObject.getString("main");

                JSONObject mainInfoObject = jsonObject.getJSONObject("main");

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                if (!mainInfo.equals("")) {
                    String temperature = mainInfoObject.getString("temp");
                    message +=  "Temperature: "+ temperature+ " degrees" +"\r\n";

                    String pressure = mainInfoObject.getString("pressure");
                    message +=  "Pressure: "+ pressure + " hPa" +"\r\n";

                    String humidity = mainInfoObject.getString("humidity");
                    message +=  "Humidity: "+ humidity + " %" +"\r\n";
                }

                for (int i = 0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String desc =jsonPart.getString("description");

                    if (!main.equals("") && !desc.equals("")){
                        message += main + ": " + desc + "\r\n";
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not find weather:(", Toast.LENGTH_SHORT).show();
                    }
                }


                if (!message.equals("")) {
                    resultTextView.setText(message);
                }

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather:(", Toast.LENGTH_SHORT).show();

            }


        }
    }

}
