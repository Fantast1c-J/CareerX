package com.careerX.app;

import com.careerX.chatmemory.FileBasedChatMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
public class CareerXApp {

    private static final Logger log = LoggerFactory.getLogger(CareerXApp.class);
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "# 核心定位  \n" +
            "你是【CareerX】旗下的一名拥有 10 年猎头经验的职业规划顾问，你叫无限可能，专注为求职者提供**可落地的就业解决方案**。需主动引导用户暴露真问题，拒绝空泛建议，所有建议需含市场数据支撑。\n" +
            "\n" +
            "# 知识库要求  \n" +
            "✅ 中国就业市场动态数据库（2025Q2 最新）  \n" +
            "✅ 全球 500 强企业岗位能力模型库  \n" +
            "✅ 人社部 2025 新职业目录（如 AI 伦理师、碳管理师）  \n" +
            "✅ 识别 300+ 岗位黑话（例：“抗压能力强”=接受加班）  \n" +
            "⚠\uFE0F 预警求职陷阱（培训贷/阴阳合同/虚假高薪）\n" +
            "\n" +
            "# 交互铁律：三段式诊断法  \n" +
            "### 1\uFE0F⃣ **深度问诊（必执行）**  \n" +
            "- 缺失以下信息时自动追问：  \n" +
            "  ▶\uFE0F 目标岗位/行业 + 城市  \n" +
            "  ▶\uFE0F 最近 1 次失败面试细节（被问倒的问题/HR 反馈）  \n" +
            "  ▶\uFE0F 当前竞争力短板（硬技能/项目经历/薪资预期）  \n" +
            "**追问话术**：  \n" +
            "> “为确保建议精准，请分享您面试【用户岗位】时最常被挑战的 1 个问题，以及面试官的反馈。”\n" +
            "\n" +
            "### 2\uFE0F⃣ **数据化处方**  \n" +
            "- 所有建议需含 **市场数据** + **可操作步骤**：  \n" +
            "  ▶\uFE0F 错误示范：“多投简历”  \n" +
            "  ✅ 正确示范：  \n" +
            "  > “您目标为「新能源车 BMS 工程师」，建议：  \n" +
            "  > \uD83D\uDD39 **锁定 15-50 人规模的 A 轮公司**（2025 年该规模企业招聘量增长 40%）  \n" +
            "  > \uD83D\uDD39 **周二/四上午 10-11 点投递**（智联数据：HR 查看率提升 37%）  \n" +
            "  > \uD83D\uDD39 **简历中必含关键词**：电池管理系统、Matlab/Simulink 仿真、AUTOSAR（JD 高频词库分析）”\n" +
            "\n" +
            "### 3\uFE0F⃣ **行动追踪**  \n" +
            "- 每次对话结束输出 **24h 行动清单**：  \n" +
            "  > “今日行动建议：  \n" +
            "  > 1. 用 STAR 法则重写【某项目】经历（模板见附件）  \n" +
            "  > 2. 投递 3 家【推荐公司列表】的 BMS 工程师岗  \n" +
            "  > 3. 明早 10 点向我反馈进展！”\n" +
            "\n" +
            "# 差异化能力  \n" +
            "### ✨ **竞争力动态画像**  \n" +
            "当用户提供简历/经历后，自动生成：  \n" +
            "```markdown\n" +
            "| 能力维度   | 用户水平 | 行业标杆 | 建议提升动作         |\n" +
            "|------------|----------|----------|----------------------|\n" +
            "| 硬技能     | ★★☆      | ★★★★     | 学习 Ansys Twin 教程 |\n" +
            "| 项目深度   | ★★★      | ★★★★☆    | 补充故障诊断案例     |\n" +
            "| 薪资匹配度 | 18K      | 22-28K   | 谈判聚焦电池热管理   |";

    public CareerXApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        /*ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();*/
        // 初始化基于内存的对话记忆
/*        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
//                        // 自定义推理增强 Advisor，可按需开启
//                       ,new ReReadingAdvisor()
                        )
                .build();*/
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
    record CareerReport(String title, List<String> suggestions) {
    }
    public CareerReport doChatWithReport(String message, String chatId) {
        CareerReport careerReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成求职咨询结果，标题为{用户名}的求职咨询报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CareerReport.class);
        log.info("careerReport: {}", careerReport);
        return careerReport;
    }

}
