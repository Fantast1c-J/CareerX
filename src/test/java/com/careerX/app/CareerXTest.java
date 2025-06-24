package com.careerX.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CareerXTest {

    @Resource
    private CareerXApp careerXApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是余生军";
        String answer = careerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想找工作";
        answer = careerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我叫什么，我想干什么";
        answer = careerXApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是余生军，我想找一份工作，但我不知道该怎么做";
        Assertions.assertNotNull(careerXApp.doChatWithReport(message, chatId));
    }
    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我是25届应届毕业生，我还没有找到工作，怎么办？";
        String answer =  careerXApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
    @Test
    void doChatWithTools() {
// 测试联网搜索问题的答案
        testMessage("周末想去上海参加招聘会，推荐几个适合应届生的小众优质企业？");

// 测试网页抓取：职场经验案例分析
        testMessage("最近和同事在工作上产生了分歧，看看知乎网站（https://zhuanlan.zhihu.com/p/714780598）的职场达人是怎么处理团队矛盾的？");

// 测试资源下载：图片下载
        testMessage("直接下载一张适合做电脑桌面的职业规划思维导图为文件");

// 测试终端操作：执行代码（保持原技术测试不变）
        testMessage("执行 Python3 脚本来生成数据分析报告");

// 测试文件操作：保存用户档案
        testMessage("保存我的职业技能档案为文件");

// 测试 PDF 生成
        testMessage("生成一份‘求职冲刺计划’PDF，包含目标公司清单、面试时间表和技能提升清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = careerXApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
