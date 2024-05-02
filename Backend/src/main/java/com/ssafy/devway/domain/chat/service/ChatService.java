package com.ssafy.devway.domain.chat.service;

import com.ssafy.devway.ChatGPT.GPTBlock;
import com.ssafy.devway.ChatGPT.GPTMode;
import com.ssafy.devway.domain.chat.dto.request.ChatCheckRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatDetailRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatRequestDto;
import com.ssafy.devway.domain.chat.dto.response.ChatListDetailResponse;
import com.ssafy.devway.domain.chat.dto.response.ChatListResponse;
import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.chat.entity.Sentence;
import com.ssafy.devway.domain.chat.repository.ChatRepository;
import com.ssafy.devway.domain.chat.repository.SentenceRepository;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final GPTBlock gptBlock = new GPTBlock(
        "sk-proj-xe7g0CQdvSdmKZPTJPbGT3BlbkFJoEfOvwHmhjcKdGVMFiB2");
    private final MemberRepository memberRepository;
    private final SentenceRepository sentenceRepository;
    private final ChatRepository chatRepository;


    /**
     * 대화 시작
     */
    public String beginChatting(ChatRequestDto dto) throws IOException {

        Member member = getMember(dto.getMemberEmail());

        // 키워드로 질문 시작
        String question = gptBlock.askQuestion(dto.getContent(),
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

        /**
         * 질문 저장, 답변 저장, 채팅에 추가
         * todo : 어떤 채팅인지 어떻게 구분해서 저장할까 -> 유저 닉네임, 날짜
         * 채팅 리스트 중에 맨 마지막거 가져와서 더하기?
         */

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
        if(index == -1) index = 0;

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
                GPTMode.GPT_ENGLISH_GRAMMER);
            allCorrections.append(correction).append("\n");
        }
        return allCorrections.toString();


    }

    public Member getMember(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail);
    }
}
