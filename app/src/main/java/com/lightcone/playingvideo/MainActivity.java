package com.lightcone.playingvideo;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity
        implements OnCompletionListener, OnPreparedListener {

    // Video source file
    private static final String fileName = "LIGOchirp.mp4";

    private VideoView videoPlayer;
    private static final String TAG="VIDEO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign a VideoView object to the video player and set its properties.  It
        // will be started by the onPrepared(MediaPlayer vp) callback below when the
        // file is ready to play.

        videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
        videoPlayer.setOnPreparedListener(this);
        videoPlayer.setOnCompletionListener(this);
        videoPlayer.setKeepScreenOn(true);

        // Find and log the root of the external storage file system.  We assume file system is
        // mounted and writable (see the project WriteSDCard for ways to check this). This is
        // to display information only, since we will find the full path to the external files
        // directory below using getExternalFilesDir(null).

        File root = Environment.getExternalStorageDirectory();
        Log.i(TAG, "Root external storage="+root);

        /**
         Must store video file in external storage.  See
         http://developer.android.com/reference/android/content
         /Context.html#getExternalFilesDir(java.lang.String)
         File stored with
         adb -d push /home/guidry/Bawwlllll.mp4 /storage/emulated/0/Android/data
         /com.lightcone.playingvideo/files
         or equivalently
         adb -d push /home/guidry/Bawwlllll.mp4 /sdcard/Android/data
         /com.lightcone.playingvideo/files
         Can also be transferred with a graphical interface using WiFi Explorer
         (https://play.google.com/store/apps/details?id=com.dooblou.WiFiFileExplorer)/
         */

        // Get path to external video file and point videoPlayer to that file
        String externalFilesDir = getExternalFilesDir(null).toString();
        Log.i(TAG,"External files directory = "+externalFilesDir);
        String videoResource = externalFilesDir +"/" + fileName;
        Log.i(TAG,"videoPath="+videoResource);
        videoPlayer.setVideoPath(videoResource);

        MediaMetadataRetriever mdr = new MediaMetadataRetriever();

        // For reference, find the orientation of the video (if API 17 or later)
        mdr.setDataSource(videoResource);
        if (Build.VERSION.SDK_INT >= 17) {
            String orient = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            Log.i(TAG, "Orientation="+orient+" degrees");
        }
    }

    /** This callback will be invoked when the file is ready to play */
    @Override
    public void onPrepared(MediaPlayer vp) {

        // Don't start until ready to play.  The arg of seekTo(arg) is the start point in
        // milliseconds from the beginning. Normally we would start at the beginning but,
        // for purposes of illustration, in this example we start playing 1/5 of
        // the way through the video if the player can do forward seeks on the video.

        if(videoPlayer.canSeekForward()) videoPlayer.seekTo(videoPlayer.getDuration()/5);
        videoPlayer.start();
    }

    /** This callback will be invoked when the file is finished playing */
    @Override
    public void onCompletion(MediaPlayer  mp) {
        // Statements to be executed when the video finishes.
        this.finish();
    }

    /**  Use screen touches to toggle the video between playing and paused. */
    @Override
    public boolean onTouchEvent (MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(videoPlayer.isPlaying()){
                videoPlayer.pause();
            } else {
                videoPlayer.start();
            }
            return true;
        } else {
            return false;
        }
    }
}
