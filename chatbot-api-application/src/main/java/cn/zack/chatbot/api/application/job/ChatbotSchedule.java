package cn.zack.chatbot.api.application.job;

import cn.zack.chatbot.api.domain.ai.IOpenAI;
import cn.zack.chatbot.api.domain.zsxq.IZsxqApi;
import cn.zack.chatbot.api.domain.zsxq.model.aqqregates.UnAnsweredQuestionsAggregates;
import cn.zack.chatbot.api.domain.zsxq.model.vo.Topics;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

//问答任务
@EnableScheduling
@Configuration
public class ChatbotSchedule {
    private Logger logger = LoggerFactory.getLogger(ChatbotSchedule.class);

    @Value("${chatbot-api.groupId}")
    private String groupId;
    @Value("${chatbot-api.cookie}")
    private String cookie;

    @Resource
    private IZsxqApi zsxqApi;
    @Resource
    private IOpenAI openAI;

    @Scheduled(cron = "0/10 * * * * ?")
    public void run(){
        try{
            if(new Random().nextBoolean()){
                logger.info("系统繁忙中。。。");
                return;
            }
            GregorianCalendar calendar = new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour > 24 || hour < 6){
                logger.info("休息时间，AI暂停工作");
                return;
            }

            //检索问题
            UnAnsweredQuestionsAggregates unAnsweredQuestionsAggregates = zsxqApi.queryUnansweredQuestionsTopicId(groupId,cookie);
            logger.info("测试结果：{}", JSON.toJSONString(unAnsweredQuestionsAggregates));
            List<Topics> topics = unAnsweredQuestionsAggregates.getResp_data().getTopics();
            if(null == topics || topics.isEmpty()){
                logger.info("本次检索未查询到待回答问题");
                return;
            }

            //AI回答
            Topics topic = topics.get(0);
            String answer = openAI.doChatGPT(topic.getQuestion().getText().trim());

            //问题回复
            boolean status = zsxqApi.answer(groupId,cookie,topic.getTopic_id(),answer,false);
            logger.info("编号：{} 问题：{} 回答：{} 状态：{}",topic.getTopic_id(),topic.getQuestion(),answer,status);

        }catch(IOException e){
            logger.error("自动回复问题异常",e);
        }


    }

}
