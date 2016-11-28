package com.wsexampleapp.wsexampleapp;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, AdapterView.OnItemClickListener{
    Informacao informacao;

    AlertDialog.Builder dlg;

    TextView txtRequest = null ;
    Button btnRequest;
    Button btnSend;
    Button btnDelete;

    EditText edtString;
    EditText edtInt;
    EditText edtDate; // Utilizar Datepicker
    EditText edtDouble;

    ListView lstInformacoes;
    ArrayAdapter<Informacao> adpInformacoes;

    Map<String,String> parametros =  new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        edtString = (EditText) findViewById(R.id.edtString);
        edtInt = (EditText) findViewById(R.id.edtInt);
        edtDate = (EditText) findViewById(R.id.edtDate);
        edtDouble = (EditText) findViewById(R.id.edtDouble);

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        txtRequest = (TextView) findViewById(R.id.txtRequest);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(this);

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        lstInformacoes = (ListView)findViewById(R.id.lstInformacoes);
        adpInformacoes = new ArrayAdapter<Informacao>(this,android.R.layout.simple_list_item_1);
        lstInformacoes.setAdapter(adpInformacoes);
        lstInformacoes.setOnItemClickListener(this);

        dlg = new AlertDialog.Builder(this);
    }

    @Override
    public void onClick(View v) {
        txtRequest.setText("");
        if( v.getId() == btnRequest.getId()) {
            // Parte Request

            try {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://wsexamplegae.appspot.com/resources/informacaojson/getinformacao";

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                //txtRequest.setText("Response: " + response.toString());
                                jsonToList(response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                txtRequest.setText("Erro: " + error.networkResponse);
                            }
                        });

                queue.add(jsonArrayRequest);
            }
            catch(Exception e){
                e.printStackTrace();
                dlg.setMessage(e.toString());
                dlg.setNeutralButton("OK",null);
                dlg.show();
            }

        }
        else if(v.getId() == btnSend.getId() || v.getId() == btnDelete.getId()){
            // Parte Send

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            String url = "http://wsexamplegae.appspot.com/resources/informacaojson/setinformacao";

            //Preenchedo o map que será enviado por Json
            Map<String,String> params  = new HashMap<String,String>();

            // Verifica se há alguma chave e qual botão foi pressionado, se tiver key, significa que é uma atualização que será feita, senão é um cadastro novo
            if(v.getId() == btnSend.getId() && informacao== null) {
                // Cadastro novo
                params.put("key","");
                params.put("action","new");
            }
            else if(v.getId() == btnSend.getId() && informacao != null){
                // Update
                params.put("key",String.valueOf(informacao.getKeyInfo()));
                params.put("action","update");
            }
            else if(v.getId() == btnDelete.getId() && informacao != null) {
                //Delete
                params.put("key",String.valueOf(informacao.getKeyInfo()));
                params.put("action","delete");
            }
            params.put("string",edtString.getText().toString());
            params.put("int",edtInt.getText().toString());
            params.put("date", edtDate.getText().toString().replace("/","-"));
            params.put("double",edtDouble.getText().toString());

            parametros = params; // Apenas para verificar o que está sendo enviado

            JSONObject jsonObject = new JSONObject(params);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);

            try {
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                        (Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                jsonToList(response);
                                //txtRequest.setText("Response App: " + response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                txtRequest.setText("Erro App: " + error.getMessage());
                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                };
                queue.add(jsonArrayRequest);

                limpar();
                // Apenas para verificar o que está sendo enviado
                //dlg.setMessage(params.toString());
               // dlg.setNeutralButton("OK",null);
               // dlg.show();

            }catch (Exception e) {
                e.printStackTrace();
                dlg.setMessage(e.toString());
                dlg.setNeutralButton("OK",null);
                dlg.show();
            }
            // Limpar a informacao que está na memória
            informacao=null;
        }
    }
    public void jsonToList(JSONArray jsonArray){
        adpInformacoes.clear();
        // Método que adiciona na lista informacoes o JsonArray recebido do servidor
        JSONObject jsonObject;

        // Verifica se veio tem alguma informação cadastrada
        if(jsonArray.length() == 0) {
            dlg.setMessage("Lista Vazia");
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }
        else {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {

                    informacao = new Informacao();
                    jsonObject = new JSONObject(jsonArray.getString(i));

                    // Aqui ocorre a separação das listas que estão dentro de cada "linha" do Json
                    JSONObject joKey = jsonObject.getJSONObject("key");
                    informacao.setKeyInfo(Long.parseLong(joKey.getString("id")));

                    JSONObject joPMap = jsonObject.getJSONObject("propertyMap");
                    informacao.setStringInfo(joPMap.getString("string"));
                    informacao.setIntInfo(joPMap.getInt("int"));

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aaa", Locale.ENGLISH);
                    Date parsedDate = sdf.parse(joPMap.getString("date"));
                    informacao.setDateInfo(parsedDate);

                    informacao.setDoubleInfo(joPMap.getDouble("double"));

                    adpInformacoes.add(informacao);
                }
            } catch (Exception e) {
                e.printStackTrace();
                dlg.setMessage(e.toString());
                dlg.setNeutralButton("OK", null);
                dlg.show();
            }
            // Limpar a informacao que está na memória
            informacao = null;
            btnDelete.setVisibility(View.INVISIBLE);
        }
    }

    public void limpar(){
        edtString.getText().clear();
        edtInt.getText().clear();
        edtDate.getText().clear();
        edtDouble.getText().clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Comandos para o ArrayAdapter
        informacao = adpInformacoes.getItem(position);

        edtString.setText(informacao.getStringInfo());
        String dataString = new SimpleDateFormat("dd/MM/yyyy").format(informacao.getDateInfo());
        edtDate.setText(dataString);
        edtDouble.setText(Double.toString(informacao.getDoubleInfo()));
        edtInt.setText(String.valueOf(informacao.getIntInfo()));

        btnDelete.setVisibility(View.VISIBLE);
    }
}

