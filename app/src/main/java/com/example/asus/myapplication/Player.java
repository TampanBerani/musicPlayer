package com.example.asus.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    static int position = 0;
    Uri u;
    final ArrayList<File> mySongs2 = findSongs(Environment.getExternalStorageDirectory());
    Thread updateSeekbar;

    SeekBar sb;
    Button btPly,btFF,btFB,btNxt,btPrv;
    TextView tvSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btPly = (Button) findViewById(R.id.btPly);
        btFF = (Button) findViewById(R.id.btFF);
        btFB = (Button) findViewById(R.id.btFB);
        btNxt = (Button) findViewById(R.id.btNxt);
        btPrv = (Button) findViewById(R.id.btPrv);
        tvSongName = (TextView) findViewById(R.id.tvSongName);

        btPly.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btFB.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPrv.setOnClickListener(this);

        sb =(SeekBar) findViewById(R.id.seekBar);
        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                sb.setMax(totalDuration);
                while (currentPosition < totalDuration){
                    try{
                        sleep(1000);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);

                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };


        if(mp!=null){
            mp.stop();
            mp.release();
        }
//        if (mp.isPlaying()){
//            mp.stop();
//            mp.release();
//        }
        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos", 0);
        play(position);
//        tvSongName.setText(mySongs2.get(position).getName().toString());
//        toast(mySongs2.get(0).getName().toString());
//        u = Uri.parse(mySongs2.get(position).toString());
//        mp = MediaPlayer.create(getApplicationContext(),u);
//        mp.start();
//        sb.setMax(mp.getDuration());
        updateSeekbar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btPly:
                if (mp.isPlaying()){
                    btPly.setBackgroundResource(R.drawable.play);
                    //btPly.setText(">");
                    mp.pause();
                }
                else {
                    mp.start();
                    btPly.setBackgroundResource(R.drawable.pause);
                    //btPly.setText("||");
                }
                break;
            case R.id.btFF:
                mp.seekTo(mp.getCurrentPosition()+5000);
                break;
            case R.id.btFB:
                mp.seekTo(mp.getCurrentPosition()-5000);
                break;
            case R.id.btNxt:
                mp.stop();
                mp.release();
                position = (position+1)%mySongs.size();
//                u = Uri.parse(mySongs2.get(position).toString());
//                mp = MediaPlayer.create(getApplicationContext(),u);
//                mp.start();
//                sb.setMax(mp.getDuration());
                play(position);
                break;
            case R.id.btPrv:
                mp.stop();
                mp.release();
                position = (position-1 <0)? mySongs.size()-1 : position-1;
                //position = (position-1)%mySongs.size();
                play(position);

                break;
        }

    }
    public void play (int position){

        tvSongName.setText(mySongs2.get(position).getName().toString());
        //toast(mySongs2.get(position).getName().toString());
        u = Uri.parse(mySongs2.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());

    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
    public ArrayList<File> findSongs (File root){
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                al.addAll(findSongs(singleFile));

            }else{
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    al.add(singleFile);
                }

            }
        }
        return al;
    }
}
