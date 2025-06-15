package com.careerX;

import cn.hutool.core.lang.UUID;
import com.careerX.app.CareerXApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CareerXTest {

    @Resource
    private CareerXApp CareerXApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是余生军";
        String answer = CareerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想找工作";
        answer = CareerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我叫什么，我想干什么";
        answer = CareerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是余生军，我想找一份工作，但我不知道该怎么做";
        Assertions.assertNotNull(CareerXApp.doChatWithReport(message, chatId));
    }
    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我是25届应届毕业生，我还没有找到工作，怎么办？";
        String answer =  CareerXApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
