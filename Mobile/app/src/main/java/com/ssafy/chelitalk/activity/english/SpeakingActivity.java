package com.ssafy.chelitalk.activity.english;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.api.chat.Message;
import com.ssafy.chelitalk.api.chat.MessageAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeakingActivity extends AppCompatActivity {

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private TextView tv;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    private ImageButton recordButton;
    private ImageButton stopButton;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int WRITE_EXTERNAL_STORAGE = 200;
    private static final int READ_EXTERNAL_STORAGE = 200;

    private ProgressBar progressBar;
    private Handler progressHandler = new Handler();
    private int progressStatus = 0;

    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);
        initializeUI();

        progressBar = findViewById(R.id.progressBar);

        animationView = findViewById(R.id.lottie);
        animationView.setAnimation("loading_loop.json");
        animationView.setVisibility(View.GONE);

        //채팅 종료
        Button btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(SpeakingActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_chat_finish, null);
                dlg.setView(dialogView);

                TextView tvDialogContent = dialogView.findViewById(R.id.tvDialogContent);
                Button btnDialogOk = dialogView.findViewById(R.id.btnDialogOk);
                Button btnDialogCancel = dialogView.findViewById(R.id.btnDialogCancel);

                AlertDialog alertDialog = dlg.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_style);

                tvDialogContent.setText("정말 채팅을 끝내시겠어요?");

                btnDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnDialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SpeakingActivity.this, CheckActivity.class);
                        startActivity(intent);
                    }
                });

                alertDialog.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUI() {
        // 녹음 버튼을 초기화하고 클릭 리스너를 설정합니다.
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        recyclerView = findViewById(R.id.recyclerView);
        tv = findViewById(R.id.tv_keyword);
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        String userEmail = intent.getStringExtra("userEmail");
        tv.setText(keyword + "을(를) 주제로 스피킹을 진행합니다.");

        startChat(userEmail, keyword);

        recordButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            } else {
                startRecording();
            }
        });

        stopButton.setOnClickListener(v -> {
            if (isRecording) {
                try {
                    stopRecording(userEmail);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                File recordedFile = new File(getExternalFilesDir(null).getAbsolutePath(), "recording.wav");
            }
        });
    }

    private void startChat(String email, String keyword) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            String url = "https://k10b201.p.ssafy.io/cherry/api/chat/begin";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"content\":\"" + keyword + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("SpeakingActivity", "서버 통신 실패", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 서버로부터의 응답 처리
                        Log.d("SpeakingActivity", "메시지 전송 성공");
                        String responseMessage = response.body().string();
                        runOnUiThread(() -> {
                            adapter.addMessage(new Message(responseMessage, false));

                            // 여기에서 TTS api 호출
                            sendTextToServer(email, responseMessage);

                        });
                    } else {
                        Log.e("SpeakingActivity", "서버 오류 (start chat) : " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("SpeakingActivity", "///// OkHttp Client Error /////", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "권한이 거부되어 녹음을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        final int sampleRate = 44100;  // 샘플레이트
        final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);

        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
            recorder.startRecording();
            isRecording = true;
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressStatus = 0;

            recordingThread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile(bufferSize);
                }
            }, "AudioRecorder Thread");
            recordingThread.start();

            new Thread(() -> {
                int progressUpdateTime = 150;
                int totalDuration = 15000; // 총 녹음 시간을 15초로 설정
                int timesToUpdate = totalDuration / progressUpdateTime; // 업데이트 할 총 횟수

                while (isRecording && progressStatus < timesToUpdate) {
                    progressStatus++;

                    progressHandler.post(() -> progressBar.setProgress((int) (100.0 * progressStatus / timesToUpdate)));
                    try {
                        Thread.sleep(progressUpdateTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "녹음 시작 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeAudioDataToFile(int bufferSize) {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
            while (isRecording) {
                int read = recorder.read(data, 0, bufferSize);
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    os.write(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording(String userEmail) {
        try {
            if (null != recorder) {
                isRecording = false;
                recorder.stop();
                recorder.release();
                recorder = null;
                recordingThread = null;
                recordButton.setEnabled(true);
            }
            progressBar.setVisibility(View.GONE);

            String wavFile = getExternalFilesDir(null).getAbsolutePath() + "/recording.wav";

            System.out.println("wavFile = " + wavFile);

            copyWaveFile(getTempFilename(), wavFile);
            deleteTempFile();
            sendAudioToServer(wavFile, userEmail);
        } catch (Exception e) {
            Log.e("SpeakingActivity", "Error in stopRecording", e);
            Toast.makeText(this, "Error stopping recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getTempFilename() {
        return getExternalFilesDir(null).getAbsolutePath() + "/temp.raw";
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = 44100;  // 샘플레이트
        int channels = 1;  // 채널 수 (모노)
        long byteRate = 16 * longSampleRate * channels / 8;  // 16 비트 오디오

        int bufferSize = 4096;
        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;  // 오디오 데이터 길이 + 헤더의 나머지 부분

            // 헤더 작성
            writeWavHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            // 오디오 데이터 쓰기
            while (in.read(data) != -1) {
                out.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeWavHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long sampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1 for PCM
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private void sendAudioToServer(String filePath, String memberEmail) throws Exception {

        File file = new File(filePath);

        long fileSize = file.length();
        long MAX_SIZE = 10 * 1024 * 1024; // 최대 10MB로 설정

        if (fileSize > MAX_SIZE) {
            Toast.makeText(SpeakingActivity.this, "File is too large to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        showUploadingAnimation(); // 애니메이션 보여주기

        OkHttpClient client = getSecureOkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/wav"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audioFile", file.getName(), fileBody)
                .addFormDataPart("memberEmail", memberEmail)
                .build();

        Request request = new Request.Builder()
                .url("https://k10b201.p.ssafy.io/cherry/api/chat/stt")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideUploadingAnimation(); // 애니메이션 숨기기
                if (response.isSuccessful()) {
                    String transcribedText = response.body().string();  // 응답 받은 텍스트

                    runOnUiThread(() -> {
                        // 사용자가 보낸 메시지 채팅창에 띄우기
                        adapter.addMessage(new Message(transcribedText, true));

                        // 메세지에 대한 gpt api 호출
                        sendMessage(memberEmail, transcribedText);
                    });
                } else {
                    Log.e("SpeakingActivity", "서버 오류(called sendAudioToServer api) : " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                hideUploadingAnimation(); // 애니메이션 숨기기
                runOnUiThread(() -> {
                    Log.e("SpeakingActivity", "서버 통신 실패(called sendAudioToServer api)", e);
                });
            }
        });
    }

    private void sendTextToServer(String email, String content){
        try {

            OkHttpClient client = getSecureOkHttpClient();
            String url = "https://k10b201.p.ssafy.io/cherry/api/chat/tts";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"content\":\"" + content + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            System.out.println("requestBody : " + requestBody);

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("SpeakingActivity", "서버 통신 실패(called sendTextToServer api)", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {

                        byte[] audioBytes = response.body().bytes();

                        // 오디오 재생
                        runOnUiThread(() -> {
                            playAudioFromBytes(audioBytes);
                        });
                    } else {
                        // 서버 오류 처리
                        Log.e("SpeakingActivity", "서버 오류(called sendTextToServer api) : " + response.code());
                    }
                }
            });
        }catch (Exception e) {
            Log.e("SpeakingActivity", "///// OkHttp Client Error /////", e);
        }
    }

    private void playAudioFromBytes(byte[] audioBytes) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            System.out.println("오디오 재생 시도, audioBytes.length : " + audioBytes.length);

            // 임시 파일 생성
            File tempMp3 = File.createTempFile("response", "mp3", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(audioBytes);
            fos.close();

            mediaPlayer.setDataSource(getApplicationContext(), Uri.fromFile(tempMp3));
            mediaPlayer.prepare();  // 준비
            mediaPlayer.start();    // 재생 시작

            // 녹음 버튼 비활성화
            disableRecordingButtons();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();  // 리소스 해제
                enableRecordingButtons();  // 버튼 다시 활성화
            });
        } catch (IOException e) {
            Log.e("SpeakingActivity.this", "오디오 실행 불가 : " + e.getMessage());
        }
    }

    private void disableRecordingButtons() {
        runOnUiThread(() -> {
            recordButton.setEnabled(false);
            stopButton.setEnabled(false);

            recordButton.setAlpha(0.4f);
            stopButton.setAlpha(0.4f);
        });
    }

    private void enableRecordingButtons() {
        runOnUiThread(() -> {
            recordButton.setEnabled(true);
            stopButton.setEnabled(true);

            recordButton.setAlpha(1.0f);
            stopButton.setAlpha(1.0f);
        });
    }

    private void sendMessage(String email, String message) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            String url = "https://k10b201.p.ssafy.io/cherry/api/chat";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"content\":\"" + message + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("SpeakingActivity", "서버 통신 실패(called sendMessage api)", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 서버로부터의 응답 처리
                        Log.d("SpeakingActivity", "메시지 전송 성공");
                        String responseMessage = response.body().string();
                        runOnUiThread(() -> {
                            adapter.addMessage(new Message(responseMessage, false));

                            // TTS api 호출
                            sendTextToServer(email, responseMessage);
                        });
                    } else {
                        // 서버 오류 처리
                        Log.e("SpeakingActivity", "서버 오류(called sendMessage api) : " + response.code());
                    }
                }
            });
        }catch (Exception e) {
            Log.e("SpeakingActivity", "///// OkHttp Client Error /////", e);
        }
    }

    private OkHttpClient getSecureOkHttpClient() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.k10b201);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[]{trustManager}, null);

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void showUploadingAnimation() {
        runOnUiThread(() -> {
            recordButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
        });
    }

    private void hideUploadingAnimation() {
        runOnUiThread(() -> {
            recordButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.GONE);
            animationView.pauseAnimation();
        });
    }

}
