package com.example.practica;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView contactButton;
    private TextView eventTitle;
    private Button apuntarse;
    private Button desapuntarse;
    private ImageView imageEvent;
    private TextView monthStart;
    private TextView monthEnd;
    private TextView dayStart;
    private TextView dayEnd;
    private TextView description;
    private TextView ubication;
    private TextView maxPpl;
    private TextView type;

    private int ownerID;
    private int eventID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_layout);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.light_blue));

        backButton = (ImageView) findViewById(R.id.backButtonEvent);
        contactButton = (TextView) findViewById(R.id.contact);
        eventTitle = (TextView) findViewById(R.id.eventTitle);
        apuntarse = (Button) findViewById(R.id.añadir_event);
        desapuntarse = (Button) findViewById(R.id.quitar_event);
        imageEvent = (ImageView) findViewById(R.id.image_event);
        monthStart = (TextView) findViewById(R.id.monthTextStart);
        monthEnd = (TextView) findViewById(R.id.monthTextEnd);
        dayStart = (TextView) findViewById(R.id.dayTextStart);
        dayEnd = (TextView) findViewById(R.id.dayTextEnd);
        description = (TextView) findViewById(R.id.descriptionText);
        ubication = (TextView) findViewById(R.id.ubicationText);
        maxPpl = (TextView) findViewById(R.id.maxPeopleText);
        type = (TextView) findViewById(R.id.typeText);

        String title = getIntent().getStringExtra("eventTilte");
        String descript = getIntent().getStringExtra("eventDescription");
        String loc = getIntent().getStringExtra("eventUbi");
        String maxppl = getIntent().getStringExtra("eventMaxppl");
        String tpe = getIntent().getStringExtra("eventType");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");
        String image = getIntent().getStringExtra("image");
        ownerID = getIntent().getIntExtra("eventOwner", -1);
        eventID = getIntent().getIntExtra("eventID", -1);

        eventTitle.setText(title);
        description.setText(descript);
        ubication.setText(loc);
        maxPpl.setText(maxppl);
        type.setText(tpe);
        if (image.contains("http://") || image.contains("https://"))
            Picasso.get().load(image).into(imageEvent);
        else
            Picasso.get().load(User.defaultImage).into(imageEvent);

        try {
            separateDateInitEnd(startDate, endDate);
        } catch (Exception e) {
            Toast toast = Toast.makeText(EventActivity.this, "Error loading event date!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOwner();
            }
        });

        apuntarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAssistances();
            }
        });

        desapuntarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNonAssistances();
            }
        });
    }

    private void separateDateInitEnd(String start, String end) {
        start = start.replace("T", " ");
        String[] camps = start.split(" ");
        String[] date = camps[0].split("-");

        date[1] = date[1].replace("-", "");
        String month = createMonth(date[1]);

        end = end.replace("T", " ");
        String[] camps2 = end.split(" ");
        String[] date2 = camps2[0].split("-");

        date2[1] = date2[1].replace("-", "");
        String month2 = createMonth(date2[1]);

        monthStart.setText(month);
        monthEnd.setText(month2);

        dayStart.setText(date[2]);
        dayEnd.setText(date2[2]);
    }

    private String createMonth(String date) {
        switch (date) {
            case "01":
                return "Jan";
            case  "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
            default:
                return "";
        }
    }

    private void makeAssistances() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://puigmal.salle.url.edu/api/events/"+eventID+"/assistances";

        JSONObject params = new JSONObject();
        try {
            params.put("puntuation", 1);
            params.put("comentary", "null");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest or = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Toast toast = Toast.makeText(EventActivity.this, "Confirmed assistance", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();

                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(EventActivity.this, "Error making the assistance", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + User.getInstance().getToken());
                return params;
            }
        };

        queue.add(or);
    }

    private void makeNonAssistances() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://puigmal.salle.url.edu/api/events/"+eventID+"/assistances";

        JsonObjectRequest or = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Toast toast = Toast.makeText(EventActivity.this, "Confirmed Non Assistance", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();

                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(EventActivity.this, "Error making the request", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + User.getInstance().getToken());
                return params;
            }
        };

        queue.add(or);
    }

    private void getOwner() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://puigmal.salle.url.edu/api/users/"+ownerID;

        JsonArrayRequest or = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Intent intent = new Intent(EventActivity.this, ChatActivity.class);
                    intent.putExtra("userID", response.getJSONObject(0).getString("id"));
                    intent.putExtra("userName", response.getJSONObject(0).getString("name"));
                    intent.putExtra("userLastName", response.getJSONObject(0).getString("last_name"));
                    intent.putExtra("userImage", response.getJSONObject(0).getString("image"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(EventActivity.this, "Error contacting the user!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + User.getInstance().getToken());
                return params;
            }
        };
        queue.add(or);
    }
}