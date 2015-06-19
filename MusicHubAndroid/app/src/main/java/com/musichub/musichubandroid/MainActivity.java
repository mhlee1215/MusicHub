package com.musichub.musichubandroid;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.musichub.musichubandroid.CapitalizeClient;

public class MainActivity extends Activity {
    public final String LOG_TAG = "AUDIO_PLAY_EXAMPLE";
    public static final String TAG = "AUDIO_PLAY_EXAMPLE";

    private static int TIMEOUT_US = -1;
    Thread t;
    int sr = 44100;
    boolean isRunning = true;
    SeekBar fSlider;
    double sliderval;
    boolean sawInputEOS;

    MediaExtractor extractor;
    MediaFormat format;

    private class PlayerThread extends Thread {

        @Override
        public void run() {
            MediaExtractor extractor;
            MediaCodec codec;
            ByteBuffer[] codecInputBuffers;
            ByteBuffer[] codecOutputBuffers;

            AudioTrack mAudioTrack;

            mAudioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    8192 * 2,
                    AudioTrack.MODE_STREAM);

            extractor = new MediaExtractor();
            try
            {
                extractor.setDataSource("http://www.ics.uci.edu/~minhaenl/data/timetolove.wav");
                MediaFormat format = extractor.getTrackFormat(0);
                String mime = format.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, String.format("MIME TYPE: %s", mime));

                codec = MediaCodec.createDecoderByType(mime);
                codec.configure(
                        format,
                        null /* surface */,
                        null /* crypto */,
                        0 /* flags */ );
                codec.start();
                codecInputBuffers = codec.getInputBuffers();
                codecOutputBuffers = codec.getOutputBuffers();

                extractor.selectTrack(0); // <= You must select a track. You will read samples from the media from this track!

                boolean sawInputEOS = false;
                boolean sawOutputEOS = false;

                for (;;) {
                    int inputBufIndex = codec.dequeueInputBuffer(-1);
                    if (inputBufIndex >= 0) {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];

                        int sampleSize = extractor.readSampleData(dstBuf, 0);
                        long presentationTimeUs = 0;
                        if (sampleSize < 0) {
                            sawInputEOS = true;
                            sampleSize = 0;
                        } else {
                            presentationTimeUs = extractor.getSampleTime();
                        }

                        codec.queueInputBuffer(inputBufIndex,
                                0, //offset
                                sampleSize,
                                presentationTimeUs,
                                sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
                        if (!sawInputEOS) {
                            extractor.advance();
                        }

                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                        final int res = codec.dequeueOutputBuffer(info, -1);
                        if (res >= 0) {
                            int outputBufIndex = res;
                            ByteBuffer buf = codecOutputBuffers[outputBufIndex];

                            final byte[] chunk = new byte[info.size];
                            buf.get(chunk); // Read the buffer all at once
                            buf.clear(); // ** MUST DO!!! OTHERWISE THE NEXT TIME YOU GET THIS SAME BUFFER BAD THINGS WILL HAPPEN

                            mAudioTrack.play();

                            if (chunk.length > 0) {
                                mAudioTrack.write(chunk, 0, chunk.length);
                            }
                            codec.releaseOutputBuffer(outputBufIndex, false /* render */);

                            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                sawOutputEOS = true;
                            }
                        }
                        else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                        {
                            codecOutputBuffers = codec.getOutputBuffers();
                        }
                        else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                        {
                            final MediaFormat oformat = codec.getOutputFormat();
                            Log.d(TAG, "Output format has changed to " + oformat);
                            mAudioTrack.setPlaybackRate(oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        }
                    }
                }

            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class ProcessFacebookTask extends AsyncTask<Void, Void, Void> {

        String sourcePath = "http://www.ics.uci.edu/~minhaenl/data/ratherbe.mp3";//timetolove.wav";
        String mime = null;
        int sampleRate = 0, channels = 0, bitrate = 0;
        long presentationTimeUs = 0, duration = 0;
        @Override
        protected Void doInBackground(Void... params) {
            try
            {

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

//                // extractor gets information about the stream
//                extractor = new MediaExtractor();
//                // try to set the source, this might fail
//                try {
//                    Log.i(LOG_TAG, "source Path :"+sourcePath);
//                    if (sourcePath != null) extractor.setDataSource(this.sourcePath);
//                    Log.i(LOG_TAG, "Extractor!!");
////                    if (sourceRawResId != -1) {
////                        AssetFileDescriptor fd = mContext.getResources().openRawResourceFd(sourceRawResId);
////                        extractor.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
////                        fd.close();
////                    }
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, "exception:"+e.getMessage());
////                    if (events != null) handler.post(new Runnable() { @Override public void run() { events.onError();  } });
//                    return null;
//                }
//
//                MediaFormat format = null;
//                try {
//                    format = extractor.getTrackFormat(0);
//                    mime = format.getString(MediaFormat.KEY_MIME);
//                    sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
//                    channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
//                    // if duration is 0, we are probably playing a live stream
//                    duration = format.getLong(MediaFormat.KEY_DURATION);
//                    bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, "Reading format parameters exception:" + e.toString());
//
//                    // don't exit, tolerate this error, we'll fail later if this is critical
//                }

                String url = "http://www.ics.uci.edu/~minhaenl/data/timetolove.wav";
                InputStream is = null;
                try {
                    is = new URL(url).openStream();//new FileInputStream(new URL(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        44100,
                        AudioFormat.CHANNEL_OUT_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        8192 * 2,
                        AudioTrack.MODE_STREAM);
                //AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 20000, AudioTrack.MODE_STREAM);
                audioTrack.play();
                // Reading data.
                byte[] data = new byte[200];
                int n = 0;
                try {
                    while ((n = is.read(data)) != -1)
                        audioTrack.write(data, 0, n);
                }
                catch (IOException e) {
                    return null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }


    }

    private class ClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String myIP = null;
            try {
                myIP = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            CapitalizeClient client = new CapitalizeClient();
            try {
                EditText address = (EditText)findViewById(R.id.ipAddress);
                String sAddress = address.getText().toString();
                Log.i(TAG, "address : "+sAddress);
                client.connectToServer(sAddress);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.connect:
                    new ClientTask().execute(null, null, null);
                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.connect).setOnClickListener(mClickListener);

        //new ProcessFacebookTask().execute(null, null, null);
        //new PlayerThread().run();

//        OpenMXPlayer player = new OpenMXPlayer();
//        player.setDataSource(url);
//        player.play();
                // point the slider to thwe GUI widget

//        AssetFileDescriptor sampleFD = getResources().openRawResourceFd(R.raw.timetolove);
//
//        MediaExtractor extractor;
//        MediaCodec codec = null;
//        ByteBuffer[] codecInputBuffers;
//        ByteBuffer[] codecOutputBuffers;
//
//        extractor = new MediaExtractor();
//        try {
//            extractor.setDataSource(sampleFD.getFileDescriptor(), sampleFD.getStartOffset(), sampleFD.getLength());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.d(LOG_TAG, String.format("TRACKS #: %d", extractor.getTrackCount()));
//        MediaFormat format = extractor.getTrackFormat(0);
//        String mime = format.getString(MediaFormat.KEY_MIME);
//        Log.d(LOG_TAG, String.format("MIME TYPE: %s", mime));
//
//        try {
//            codec = MediaCodec.createDecoderByType(mime);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        codec.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
//        codec.start();
//        codecInputBuffers = codec.getInputBuffers();
//        codecOutputBuffers = codec.getOutputBuffers();
//
//        extractor.selectTrack(0); // <= You must select a track. You will read samples from the media from this track!
//
//        int inputBufIndex = codec.dequeueInputBuffer(TIMEOUT_US);
//        if (inputBufIndex >= 0) {
//            ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
//
//            int sampleSize = extractor.readSampleData(dstBuf, 0);
//            long presentationTimeUs = 0;
//            if (sampleSize < 0) {
//                sawInputEOS = true;
//                sampleSize = 0;
//            } else {
//                presentationTimeUs = extractor.getSampleTime();
//            }
//
//            codec.queueInputBuffer(inputBufIndex,
//                    0, //offset
//                    sampleSize,
//                    presentationTimeUs,
//                    sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
//            if (!sawInputEOS) {
//                extractor.advance();
//            }
//        }

//        final int res = codec.dequeueOutputBuffer(info, TIMEOUT_US);</pre>
//        if (res >= 0) {
//            int outputBufIndex = res;
//            ByteBuffer buf = codecOutputBuffers[outputBufIndex];
//
//            final byte[] chunk = new byte[info.size];
//            buf.get(chunk); // Read the buffer all at once
//            buf.clear(); // ** MUST DO!!! OTHERWISE THE NEXT TIME YOU GET THIS SAME BUFFER BAD THINGS WILL HAPPEN
//
//            if (chunk.length > 0) {
//                audioTrack.write(chunk, 0, chunk.length);
//            }
//            codec.releaseOutputBuffer(outputBufIndex, false /* render */);
//
//            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                sawOutputEOS = true;
//            }
//        } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//            codecOutputBuffers = codec.getOutputBuffers();
//        } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//            final MediaFormat oformat = codec.getOutputFormat();
//            Log.d(LOG_TAG, "Output format has changed to " + oformat);
//            mAudioTrack.setPlaybackRate(oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
//        }

//        fSlider = (SeekBar) findViewById(R.id.frequency);
//
//        // create a listener for the slider bar;
//        OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
//            public void onStopTrackingTouch(SeekBar seekBar) { }
//            public void onStartTrackingTouch(SeekBar seekBar) { }
//            public void onProgressChanged(SeekBar seekBar,
//                                          int progress,
//                                          boolean fromUser) {
//                if(fromUser) sliderval = progress / (double)seekBar.getMax();
//            }
//        };
//
//        // set the listener on the slider
//        fSlider.setOnSeekBarChangeListener(listener);
//
//        // start a new thread to synthesise audio
//        t = new Thread() {
//            public void run() {
//                // set process priority
//                setPriority(Thread.MAX_PRIORITY);
//                // set the buffer size
//                int buffsize = AudioTrack.getMinBufferSize(sr,
//                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
//                // create an audiotrack object
//                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                        sr, AudioFormat.CHANNEL_OUT_MONO,
//                        AudioFormat.ENCODING_PCM_16BIT, buffsize,
//                        AudioTrack.MODE_STREAM);
//
//                short samples[] = new short[buffsize];
//                int amp = 10000;
//                double twopi = 8.*Math.atan(1.);
//                double fr = 440.f;
//                double ph = 0.0;
//
//                // start audio
//                audioTrack.play();
//
//                // synthesis loop
//                while(isRunning){
//                    fr =  440 + 440*sliderval;
//                    for(int i=0; i < buffsize; i++){
//                        samples[i] = (short) (amp*Math.sin(ph));
//                        ph += twopi*fr/sr;
//                    }
//                    audioTrack.write(samples, 0, buffsize);
//                }
//                audioTrack.stop();
//                audioTrack.release();
//            }
//        };
//        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onDestroy(){
        super.onDestroy();
        isRunning = false;
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        t = null;
    }
}
