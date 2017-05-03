package org.xellossryan.akatean;

import android.os.Environment;

import java.io.File;

/**
 * Created by Liang on 2017/5/3.
 */
public class RecordConstants {
    public static String MP3_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android"
            + File.separator
            + Environment.DIRECTORY_MUSIC
            + File.separator;
}
