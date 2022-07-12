package com.example.validacep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    EditText cnpj_validar;
    TextView cnpj_detalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cnpj_validar  = (EditText) findViewById(R.id.cnpj_validar);
        cnpj_detalhes = (TextView) findViewById(R.id.cnpj_detalhes);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    public void validaCnpj(View view)
    {
        trustEveryone();
        cnpj_detalhes.setText("testandio");
        String string_url="https://192.168.0.105/site2/";
        String string_json = "";

        BufferedReader url_buffer_reader = null;
        try {
            URL url = new URL(string_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                url_buffer_reader = new BufferedReader(new InputStreamReader(inputStream));

            } finally {
                urlConnection.disconnect();
                cnpj_detalhes.setText("disco");
            }
            string_json = BufferParaString(url_buffer_reader);
            cnpj_detalhes.setText("Array sem formatacao"+string_json);

        } catch (IOException e) {
            Log.e("Log error", "Não foi possível conectar: " + e.getMessage());
            cnpj_detalhes.setText("Não foi possível conectar:"+ e.getMessage());

            if (url_buffer_reader != null) {
                try {
                    url_buffer_reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    cnpj_detalhes.setText("Não foi possível conectar:");
                }
            }
        }

        try {
            JSONArray jObj = new JSONArray(string_json);
            String aux ="";
            for  (int i = 0; i<=jObj.length(); i++){
                aux += "\n * "+ jObj.getJSONObject(i).getString("usuario_nome") + " - "+jObj.getJSONObject(i).getString("usuario_email ");
                System.out.println(aux);
                cnpj_detalhes.setText("lista de usuarios  : " + aux.toString());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void chamaCpf(View view){
        String url = "https://servicos.receita.fazenda.gov.br/Servicos/CPF/ConsultaSituacao/ConsultaPublica.asp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
    private String BufferParaString(BufferedReader reader)
    {
        String linha;
        StringBuffer buffer = new StringBuffer();
        try {
            while ((linha = reader.readLine()) != null) {
                buffer.append(linha);
                buffer.append("\n");
            }

            return buffer.toString();
        } catch (IOException e)
        {
            Log.e("Erro:", "Erro durante a conversão do buffer para string:" + e.getMessage());
            return "";
        }
    }


}
