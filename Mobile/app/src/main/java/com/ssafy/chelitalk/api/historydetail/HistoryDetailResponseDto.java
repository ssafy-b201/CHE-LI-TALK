package com.ssafy.chelitalk.api.historydetail;

public class HistoryDetailResponseDto {
    private String sentenceSender;
    private String sentenceContent;

    public HistoryDetailResponseDto(String sentenceSender, String sentenceContent) {
        this.sentenceSender = sentenceSender;
        this.sentenceContent = sentenceContent;
    }

    public String getSentenceSender() {
        return sentenceSender;
    }


    public String getSentenceContent() {
        return sentenceContent;
    }

}
