package com.ama.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnSinHilos;
    private Button btnHilo;
    private Button btnAsyncTask;
    private Button btnCancelar;
    private Button btnAsyncDialog;
    private ProgressBar pbProgreso;
    private ProgressDialog pbDialog;
    private MiTareaAsincrona tarea1;
    private MiTareaAsincronaDialog tarea2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSinHilos = (Button)findViewById(R.id.btnSinHilos);
        btnHilo = (Button)findViewById(R.id.btnHilo);
        btnAsyncTask = (Button) findViewById(R.id.btnAsyncTask);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnAsyncDialog = (Button) findViewById(R.id.btnAsyncTaskDialog);
        pbProgreso = (ProgressBar) findViewById(R.id.pbProgreso);

        btnSinHilos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbProgreso.setMax(100);
                pbProgreso.setProgress(0);
                for(int i=1; i<=10; i++){
                    tareaLarga();
                    pbProgreso.incrementProgressBy(10);
                }
                Toast.makeText(MainActivity.this, "Se acabo la tarea larga, bro", Toast.LENGTH_SHORT).show();
            }
        });

        btnHilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       pbProgreso.post(new Runnable() {
                           @Override
                           public void run() {
                               pbProgreso.setProgress(0);
                           }
                       });

                       for(int i=1; i<=10; i++){
                           tareaLarga();
                           pbProgreso.post(new Runnable() {
                               @Override
                               public void run() {
                                   pbProgreso.incrementProgressBy(10);
                               }
                           });
                       }
                   }
               });
            }
        });

        btnAsyncTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tarea1 = new MiTareaAsincrona();
                tarea1.execute();
            }

        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tarea1.cancel(true);
            }
        });

        btnAsyncDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pbDialog = new ProgressDialog(MainActivity.this);
                pbDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pbDialog.setMessage("Procesando...");
                pbDialog.setCancelable(true);
                pbDialog.setMax(100);

                tarea2 = new MiTareaAsincronaDialog();
                tarea2.execute();
            }
        });
    }

    private void tareaLarga() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
        }
    }

    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i=1; i<=10; i++) {
                tareaLarga();

                publishProgress(i*10);

                if(isCancelled())
                    break;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            pbProgreso.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            pbProgreso.setMax(100);
            pbProgreso.setProgress(0);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                Toast.makeText(MainActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }

    private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i=1; i<=10; i++) {
                tareaLarga();

                publishProgress(i*10);

                if(isCancelled())
                    break;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            pbDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {

            pbDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MiTareaAsincronaDialog.this.cancel(true);
                }
            });

            pbDialog.setProgress(0);
            pbDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                pbDialog.dismiss();
                Toast.makeText(MainActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        MenuItem switchItem = menu.findItem(R.id.menu_switch);
        switchItem.setActionView(R.layout.switch_item);
        final Context context = this;
        Switch switchMenu = switchItem.getActionView().findViewById(R.id.switch_item);
        switchMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, "Seleccionastes el switch item, bro", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_item:
                Toast.makeText(this, "Seleccionastes el menu item, bro", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                String[] direcciones = {"salgadoa@uabc.edu.mx"};
                intent.putExtra(Intent.EXTRA_EMAIL, direcciones);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Titulo");
                intent.putExtra(Intent.EXTRA_TEXT, "Que onda!");
                intent.setType("text/plain");

                Intent share = intent.createChooser(intent, null);

                startActivity(share);
                break;
            case R.id.menu_search:
                Toast.makeText(this, "Seleccionastes el search item, bro", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_switch:
                Toast.makeText(this, "Seleccionastes el switch item, bro", Toast.LENGTH_SHORT).show();
                break;
        }



        return super.onOptionsItemSelected(item);
    }
}
