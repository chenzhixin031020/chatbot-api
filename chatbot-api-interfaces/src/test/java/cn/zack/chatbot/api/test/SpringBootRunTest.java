package cn.zack.chatbot.api.test;

import cn.hutool.core.annotation.Alias;
import cn.zack.chatbot.api.domain.ai.IOpenAI;
import cn.zack.chatbot.api.domain.zsxq.IZsxqApi;
import cn.zack.chatbot.api.domain.zsxq.model.aqqregates.UnAnsweredQuestionsAggregates;
import cn.zack.chatbot.api.domain.zsxq.model.res.RespData;
import cn.zack.chatbot.api.domain.zsxq.model.vo.Topics;
import cn.zack.chatbot.api.domain.zsxq.service.ZsxqApi;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRunTest {

    private Logger logger = LoggerFactory.getLogger(SpringBootRunTest.class);

    @Value("${chatbot-api.groupId}")
    private String groupId;
    @Value("${chatbot-api.cookie}")
    private String cookie;

    @Resource
    private IZsxqApi zsxqApi;
    @Resource
    private IOpenAI openAI;

    @Test
    public void test_zsxqApi() throws IOException{
        UnAnsweredQuestionsAggregates unAnsweredQuestionsAggregates = zsxqApi.queryUnansweredQuestionsTopicId(groupId,cookie);
        logger.info("测试结果：{}", JSON.toJSONString(unAnsweredQuestionsAggregates));


        List<Topics> topics = unAnsweredQuestionsAggregates.getResp_data().getTopics();

        for (Topics topic : topics) {
            String topicId = topic.getTopic_id();
            String text = topic.getQuestion().getText();
            logger.info("topicId：{} text：{}", topicId, text);

            // 回答问题
            zsxqApi.answer(groupId, cookie, topicId, text, false);
        }
    }
    @Test
    public void test_openAi() throws IOException {
        String response = openAI.doChatGPT("帮我写一个冒泡排序，要求是java");
        logger.info("\n测试结果：{}\n", response);
    }
}
