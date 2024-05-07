package com.ssafy.devway.TTS;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.common.collect.Lists;
import com.ssafy.devway.block.element.BlockElement;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import lombok.Data;


@Data
public class TTSBlock implements BlockElement {

    private String filePath = "";
    private String model = "en-US-Standard-C";

    public TTSBlock(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getName() {
        return "TTS";
    }

    public byte[] synthesizeText(String text, TTSCountry country) throws Exception {

        model = country.getTextMode();

        try (TextToSpeechClient textToSpeechClient = initializeTextToSpeechClient()) {
            SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(model.substring(0, 5))
                .setName(model)
                .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                audioConfig);
            ByteString audioContents = response.getAudioContent(); // 오디오 내용을 ByteString 형태로 받음

            // 오디오 내용을 바이트 배열로 반환
            return audioContents.toByteArray();
        }
    }

    public TextToSpeechClient initializeTextToSpeechClient() throws Exception {
        // 인증 파일 경로 지정
        String jsonPath = "/home/ubuntu/MyGC.json";

        // 파일에서 인증 정보 로드
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        // 클라이언트 설정에 인증 정보 적용
        TextToSpeechSettings textToSpeechSettings = TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();

        // 설정을 사용하여 TextToSpeechClient 생성
        return TextToSpeechClient.create(textToSpeechSettings);
    }
}