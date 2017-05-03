package org.xellossryan.akatean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xellossryan.lame.MP3Lame;
import org.xellossryan.lame.MP3LameProxy;

public class MainActivity extends AppCompatActivity {
    MP3LameProxy lame = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);

        lame = new MP3LameProxy(MP3Lame.getInstance());

        String version = lame.version();
        tv.setText(version);

        lame.initEncoder();
    }
}
