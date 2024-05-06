package com.ssafy.devway.TTS;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.ssafy.devway.block.element.BlockElement;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.protobuf.ByteString;
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

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
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
}