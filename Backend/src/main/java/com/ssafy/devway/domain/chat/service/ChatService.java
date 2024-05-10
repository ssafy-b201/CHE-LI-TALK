package com.ssafy.devway.domain.chat.service;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.ssafy.devway.GPT.GPTBlock;
import com.ssafy.devway.GPT.GPTMode;
import com.ssafy.devway.STT.STTBlock;
import com.ssafy.devway.TTS.TTSBlock;
import com.ssafy.devway.TTS.TTSCountry;
import com.ssafy.devway.domain.chat.dto.request.ChatCheckRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatDetailRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatConvertRequest;
import com.ssafy.devway.domain.chat.dto.response.ChatListDetailResponse;
import com.ssafy.devway.domain.chat.dto.response.ChatListResponse;
import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.chat.entity.Sentence;
import com.ssafy.devway.domain.chat.repository.ChatRepository;
import com.ssafy.devway.domain.chat.repository.SentenceRepository;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    Dotenv dotenv = Dotenv.load();
    String GPT_API_KEY = dotenv.get("GPT_API_KEY");

    private final GPTBlock gptBlock = new GPTBlock(GPT_API_KEY);
    private final MemberRepository memberRepository;
    private final SentenceRepository sentenceRepository;
    private final ChatRepository chatRepository;


    /**
     * 대화 시작
     */
    public String beginChatting(ChatRequestDto dto) throws IOException {

        Member member = getMember(dto.getMemberEmail());

        // 키워드로 질문 시작
        String question = gptBlock.startQuestion(dto.getContent(),
            GPTMode.GPT_TALK_START_ENGLISH);

        // chat 생성
        Chat newChat = Chat.builder()
            .member(member)
            .createdAt(LocalDateTime.now())
            .chatSentences(new ArrayList<>())
            .build();

        Chat savedChat = chatRepository.save(newChat);

        Sentence createdSentence = Sentence.builder()
            .sentenceSender("gpt")
            .sentenceContent(question)
            .chat(savedChat)
            .build();

        Sentence savedSentence = sentenceRepository.save(createdSentence);

        // 채팅에 문장 저장
        savedChat.getChatSentences().add(savedSentence);
        chatRepository.save(savedChat);

        return question;
    }

    /**
     * 채팅 진행
     */
    public String continueChatting(ChatRequestDto dto) throws IOException {

        Member member = getMember(dto.getMemberEmail());
        String sentenceFromUser = dto.getContent();
        String answerFromGpt = gptBlock.askQuestion(sentenceFromUser,
            GPTMode.GPT_TALK_CONTINUE_ENGLISH);

        // 유저의 채팅 리스트 중 가장 마지막
        int index = member.getMemberChats().size() - 1;
        if (index == -1) {
            index = 0;
        }

        Chat nowChat = member.getMemberChats().get(index);

        List<Sentence> sentences = List.of(

            Sentence.builder()
                .sentenceSender(member.getMemberNickname())
                .sentenceContent(sentenceFromUser)
                .chat(nowChat)
                .build(),

            Sentence.builder()
                .sentenceSender("gpt")
                .sentenceContent(answerFromGpt)
                .chat(nowChat)
                .build()
        );

        nowChat.getChatSentences().addAll(sentences);
        chatRepository.save(nowChat);

        return answerFromGpt;
    }

    public List<ChatListResponse> chatList(String memberEmail) {
        Member member = memberRepository.findByMemberEmail(memberEmail);
        List<Chat> chats = chatRepository.findAllByMember(member);

        if (chats != null) {
            List<ChatListResponse> responseList = chats.stream()
                .map(chat -> new ChatListResponse(chat.getCreatedAt()))
                .collect(Collectors.toList());
            return responseList;
        } else {
            return new ArrayList<>();
        }

    }

    public List<ChatListDetailResponse> chatDetail(ChatDetailRequestDto dto) {
        Member member = memberRepository.findByMemberEmail(dto.getMemberEmail());
        LocalDateTime createdAt = dto.getCreatedAt();

        Chat chat = chatRepository.findByMemberAndCreatedAt(member, createdAt);
        if (chat == null) {
            throw new IllegalArgumentException("채팅 정보가 없습니다");
        }

        List<ChatListDetailResponse> chatDetails = new ArrayList<>();
        List<Sentence> sentences = sentenceRepository.findByChatId(chat.getChatId());
        for (int i = 0; i < sentences.size(); i++) {
            chatDetails.add(new ChatListDetailResponse(sentences.get(i).getSentenceSender(),
                sentences.get(i).getSentenceContent()));
        }
        return chatDetails;
    }

    public String checkChat(ChatCheckRequestDto dto) throws IOException {
        Member member = getMember(dto.getMemberEmail());
        int index = member.getMemberChats().size() - 1;
        if (index == -1)
            index = 0;

        //가장 최근의 채팅
        Chat chat = member.getMemberChats().get(index);

        if (chat == null) {
            throw new IllegalArgumentException("채팅 정보가 유효하지 않습니다");
        }

        List<Sentence> chatList = chat.getChatSentences();
        StringBuilder allCorrections = new StringBuilder();

        for (Sentence sentence : chatList) {
            if (sentence.getSentenceSender().equals("gpt")) {
                continue;
            }
            String correction = gptBlock.askQuestion(sentence.getSentenceContent(),
                GPTMode.GPT_ENGLISH_GRAMMAR);
            allCorrections.append(correction).append("\n");
        }
        return allCorrections.toString();

    }

    public List<ChatListDetailResponse> getRecent(String memberEmail) {
        Member member = memberRepository.findByMemberEmail(memberEmail);

        int index = member.getMemberChats().size() - 1;
        if (index == -1)
            index = 0;

        //가장 최근의 채팅
        Chat chat = member.getMemberChats().get(index);

        if (chat == null) {
            throw new IllegalArgumentException("채팅 정보가 유효하지 않습니다.");
        }

        List<ChatListDetailResponse> chatDetails = new ArrayList<>();
        List<Sentence> sentences = sentenceRepository.findByChatId(chat.getChatId());
        for (int i = 0; i < sentences.size(); i++) {
            chatDetails.add(new ChatListDetailResponse(sentences.get(i).getSentenceSender(),
                sentences.get(i).getSentenceContent()));
        }
        return chatDetails;
    }

    public Member getMember(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail);
    }


    public Boolean likeChat(Long sentenceId) {
        Sentence sentence = sentenceRepository.findBySentenceId(sentenceId);

        if (sentence == null) {
            throw new IllegalArgumentException("해당하는 문장이 없습니다.");
        }

        Boolean isLike = sentence.getSentenceLikeStatus();

        if (isLike == null) {
            isLike = false;
            System.out.println("문장이 널널");
        } else {
            if (isLike) {
                isLike = false;
                System.out.println("문장 즐겨찾기 해제" + sentence.getSentenceLikeStatus());
            } else {
                isLike = true;
                System.out.println("문장 즐겨찾기" + sentence.getSentenceLikeStatus());
            }
        }
        return isLike;
    }

    public String convertToText(ChatConvertRequest request) throws Exception {

        List<String> transcribedText = transcribeAudioDirectly(request.getAudioFile());

        String content = "";

        for (String str : transcribedText) {
            content += str;
        }
        
        return content;
    }

    public byte[] convertToSpeech(ChatRequestDto request) {
        String audioFilePath = "C:/Users/SSAFY/Desktop";

        TTSBlock ttsBlock = new TTSBlock(audioFilePath);
        try {

            TTSCountry country = TTSCountry.US_C_FEMALE;
            String text = request.getContent();

            return ttsBlock.synthesizeText(text, country);

        } catch (Exception e) {
            throw new RuntimeException("TTS conversion failed", e);
        }

    }

    public List<String> transcribeAudioDirectly(MultipartFile audioFile) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            byte[] data = audioFile.getBytes();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(ByteString.copyFrom(data))
                .build();

            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setLanguageCode("en-US")
                .setSampleRateHertz(44100)
                .build();

            List<String> resultsText = new ArrayList<>();
            RecognizeResponse response = speechClient.recognize(config, audio);
            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                resultsText.add(alternative.getTranscript());
            }
            return resultsText;
        }
    }

    public String deleteHistory(String memberEmail) {
        Member member = getMember(memberEmail);

        if(member == null){
            return "멤버가 없습니다.";
        }

        List<Chat> chatList = chatRepository.findAllByMember(member);
        if(chatList.isEmpty()){
            return "회원의 채팅 정보가 없습니다.";
        }

        for(Chat chat : chatList){
            chatRepository.delete(chat);
        }
        return "해당 회원의 모든 챗이 삭제되었습니다";
    }
}
