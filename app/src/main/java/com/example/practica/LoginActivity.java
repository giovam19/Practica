package com.example.practica;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText pass;
    private Button loginButton;
    private TextView regsitrationButton;
    private String accessToken;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.emailLogin);
        pass = (EditText) findViewById(R.id.passLogin);

        loginButton = (Button) findViewById(R.id.nextLoginButton);
        regsitrationButton = (TextView) findViewById(R.id.registrationButton);
        accessToken = null;
        User.setInstance(null);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser, passUser;

                emailUser = email.getText().toString();
                passUser = pass.getText().toString();

                loginRequest(emailUser, passUser);

            }
        });

        regsitrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginRequest(String emailUser, String passUser) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://puigmal.salle.url.edu/api/login/";

        JSONObject params = new JSONObject();
        try {
            params.put("email", emailUser);
            params.put("password", passUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest or = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    accessToken = response.getString("accessToken");
                    Intent intent = new Intent(LoginActivity.this, ListEvents.class);
                    userBuilder(passUser);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(LoginActivity.this, "Usuario o Contraseña\nno validos", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 40);
                toast.show();

                System.out.println(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-type", "application/json");
                return params;
            }
        };

        queue.add(or);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void userBuilder(String pass) {
        try {
            DecodedJWT jwt = JWT.decode(accessToken);
            int id = jwt.getClaim("id").asInt();
            String name = jwt.getClaim("name").asString();
            String lastname = jwt.getClaim("last_name").asString();
            String email = jwt.getClaim("email").asString();
            String image = jwt.getClaim("image").asString();

            user = User.getInstance(id, name, lastname, email, pass, image, accessToken);
        } catch (JWTDecodeException e) {
            System.out.println("Error extrayendo user");
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        User.setInstance(null);
    }
}