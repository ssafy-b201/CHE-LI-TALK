package com.ssafy.devway.GPT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GPTMode {
  GPT_NORMAL("."),
  GPT_TALK_START_ENGLISH("Let's start a conversation in English about the topic mentioned above. Please ask interesting questions related to this."),
  GPT_TALK_CONTINUE_ENGLISH("Please continue the conversation based on the previous message. How would you respond? please answer in english."),
  GPT_ENGLISH_GRAMMAR("앞의 영어 문장에 대한 문법 체크 후 틀렸다면 어떤 부분이 틀렸는지 한글로 알려주시고, 틀린 부분이 없으면 문법이 맞다고 보내주세요! 이외의 대답은 필요 없습니다."),
  GPT_TALK_START_KOREAN("안녕하세요, 한글 대화 연습을 위해 한글로 일상 대화를 시작할 수 있는 질문을 한 문장 해주세요. 주제는 앞의 키워드와 같습니다! 이 문장에 대답은 안하셔도 됩니다."),
  GPT_TALK_CONTINUE_KOREAN("앞의 문장을 이전의 대화내용과 이어나가서 한글로 대화를 이어가주세요."),
  GPT_KOREAN_GRAMMAR("앞의 한글 문장에 대한 문법 체크 후 틀렸다면 어떤 부분이 틀렸는지 알려주시고, 틀린 부분이 없으면 문법이 맞다고 보내주세요! 이외의 대답은 필요 없습니다.");

  public final String textMode;
}
