package cn.zack.chatbot.api.domain.ai;

import java.io.IOException;

public interface IOpenAI {
    String doChatGPT(String question) throws IOException;
 }
