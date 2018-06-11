package org.xellossryan.akatean;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xellossryan.lame.MP3Lame;
import org.xellossryan.lame.MP3LameProxy;
import org.xellossryan.output.FrameEncodeQueue;
import org.xellossryan.recorder.AudioInput;
import org.xellossryan.recorder.AudioOutputProxy;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    AudioInput input = null;

    String recordFilePath = "";

    private TextView sampletext;
    private Button record;
    private TextView filepath;

    boolean isRecording = false;
    private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.filepath = findViewById(R.id.filepath);
        this.record = findViewById(R.id.record);
        this.sampletext = findViewById(R.id.sample_text);
        play = findViewById(R.id.play);

        record.setText("开始录制");



        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stop();
                } else {
                    start();
                }
            }
        });
    }

    public void start() {
        /////////////As a HearAid
        FrameEncodeQueue queue = new FrameEncodeQueue(new AudioOutputProxy());
        queue.setAllowWritingToFile(false);
        //////////////

        /////////////As a mp3 encoder
        //FrameEncodeQueue queue = new FrameEncodeQueue(new MP3LameProxy(MP3Lame.getInstance()));
        //queue.setOnEncodingEnd(new FrameEncodeQueue.OnEncodingEnd() {
        //    @Override
        //    public void onEnd(File audioFile) {
        //        // 中间的参数 authority 可以随意设置.
        //        Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", audioFile);
        //        Intent intent = new Intent();
        //        intent.setAction(Intent.ACTION_SEND);
        //        intent.setType("audio/*");
        //        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //        startActivity(Intent.createChooser(intent, "发送音频文件"));
        //    }
        //});
        input = new AudioInput(queue);
        String version = input.version();
        sampletext.setText(version);



        isRecording = true;
        recordFilePath = RecordConstants.MP3_DIR_PATH + System.currentTimeMillis() + ".mp3";
        record.setText("停止录制");
        filepath.setText(String.format("%s  正在录制", recordFilePath));
        input.setStorePath(recordFilePath);
        input.startRecording();

    }

    public void stop() {
        isRecording = false;
        record.setText("开始录制");
        filepath.setText(String.format("%s  录制停止", recordFilePath));

        input.stopRecording();
        input.release();
        input.close();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(recordFilePath);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        input.close();
    }
}
