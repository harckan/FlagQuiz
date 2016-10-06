package nfcomp.com.flagquiz;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[]       nomesArquivos;
    private String[]       nomesBandeiras;
    private List<Integer>  randId;
    private List<Integer>  randBtnId;
    private Random         randomico;
    private int            cont;
    private int            totalBandeiras;
    private int            placar;
    private MediaPlayer    mediaPlayer;

    private TextView    contadorTexto;
    private TextView    resultado;
    private ImageView   imgBandeira;
    private Button      btn1;
    private Button      btn2;
    private Button      btn3;
    private Button      btnProximo;
    private ProgressBar progressBar;

    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalBandeiras = 20;

        // Recupera os nomes das bandeiras do strings.xml
        nomesBandeiras = getResources().getStringArray(R.array.flagsNomes);

        // Recupera os nomes dos arquivos do strings.xml
        nomesArquivos = getResources().getStringArray(R.array.drawbleNomes);

        randomico = new Random();

        contadorTexto   = (TextView) findViewById(R.id.jogoContadorId);
        resultado       = (TextView) findViewById(R.id.flagNomeId);
        imgBandeira     = (ImageView) findViewById(R.id.flagImagemId);
        btn1            = (Button) findViewById(R.id.botao1Id);
        btn2            = (Button) findViewById(R.id.botao2Id);
        btn3            = (Button) findViewById(R.id.botao3Id);
        btnProximo      = (Button) findViewById(R.id.botaoProximoId);
        progressBar     = (ProgressBar) findViewById(R.id.progressBarId);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btnProximo.setOnClickListener(this);

        reset();

    }

    private void reset() {

        this.cont = 0;
        this.placar = 0;

        this.randId = new ArrayList(); // Lista randomica com os Id's das bandeiras
        randomico = new Random();
        int randTmp;

        // Gera lista com 11 Id randomico das bandeiras

        while (this.randId.size() < 11) {
            randTmp = randomico.nextInt(this.totalBandeiras);

            if (!this.randId.contains(randTmp))
                this.randId.add(randTmp);
        }

        proximo();

        Log.i("NUMEROS", "RANDOMICOS: " + this.randId);

    }

    private void proximo() {
        this.cont++;

        contadorTexto.setText("Bandeira: " + cont + " de 10");
        resultado.setText("");
        progressBar.setProgress(cont);

        // Exibe a proxima bandeira a partir do Id 1
        int imgId = getResources().getIdentifier(nomesArquivos[this.randId.get(cont)], "drawable", getPackageName());
        imgBandeira.setImageResource(imgId);

        // Nomes nos botoes
        int randTmp;
        this.randBtnId = new ArrayList();

        this.randBtnId.clear();
        this.randBtnId.add(this.randId.get(this.cont)); // Adiciona bandeira atual aos botoes

        while (randBtnId.size() < 3) {
            randTmp = randomico.nextInt(this.totalBandeiras);

            if (!this.randBtnId.contains(randTmp))
                this.randBtnId.add(randTmp);
        }

        Collections.shuffle(this.randBtnId); // Embaralha Id dos nomes nos botoes

        btn1.setText(nomesBandeiras[this.randBtnId.get(0)]);
        btn2.setText(nomesBandeiras[this.randBtnId.get(1)]);
        btn3.setText(nomesBandeiras[this.randBtnId.get(2)]);

        ativaBotoes();

    }

    @Override
    public void onClick(View v) {

        switch ( v.getId()) {
            case R.id.botao1Id:
                verifica(this.randBtnId.get(0));
                desativaBotoes();

                break;

            case R.id.botao2Id:
                verifica(this.randBtnId.get(1));
                desativaBotoes();

                break;

            case R.id.botao3Id:
                verifica(this.randBtnId.get(2));
                desativaBotoes();

                break;

            case R.id.botaoProximoId:
                proximo();

                break;

        }

    }

    private void verifica(int id) {

        if (id == randId.get(cont)) {
            this.placar++;
            resultado.setText(nomesBandeiras[id]);

            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.certo);
            tocarSom();
        } else {
            resultado.setText("Incorreto");

            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.errado);
            tocarSom();
        }

        if (cont == 10)
            alerta();
    }

    private void alerta() {

        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Game Over");
        dialog.setMessage("Você fez " + placar + " pontos.");

        dialog.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
            }
        });

        dialog.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.create().show();

        if (placar == 10) {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.aplauso);
            tocarSom();
        }

    }

    private void tocarSom() {
        if ( mediaPlayer != null)
            mediaPlayer.start();
    }


    // Menu de opcoes

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuReset:
                reset();
                break;

            case R.id.menuSair:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void desativaBotoes() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btnProximo.setEnabled(true);
    }

    private void ativaBotoes() {
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btnProximo.setEnabled(false);
    }

}
