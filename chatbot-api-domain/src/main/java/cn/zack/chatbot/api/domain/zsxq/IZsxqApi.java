package cn.zack.chatbot.api.domain.zsxq;

import cn.zack.chatbot.api.domain.zsxq.model.aqqregates.UnAnsweredQuestionsAggregates;

import java.io.IOException;

public interface IZsxqApi {

    UnAnsweredQuestionsAggregates queryUnansweredQuestionsTopicId(String groupId, String cookie) throws IOException;
    boolean answer(String groupId, String cookie, String topics, String text, boolean silenced) throws IOException;
}
