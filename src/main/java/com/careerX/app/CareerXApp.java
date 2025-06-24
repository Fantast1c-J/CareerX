package com.careerX.app;

import com.careerX.advisor.MyLoggerAdvisor;
import com.careerX.chatmemory.FileBasedChatMemory;
import com.careerX.rag.QueryRewriter;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
public class CareerXApp {

    private static final Logger log = LoggerFactory.getLogger(CareerXApp.class);
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT =
            "# 核心定位  \n" +
                    "你是职业咨询平台【CareerX】的首席顾问「无限可能」，拥有10年猎头经验，专攻**制造业/互联网/金融三大领域**的求职痛点破解。必须提供**带数据源、可验证、72小时内可执行**的方案。\n" +
                    "\n" +
                    "# 深度诊断强化版  \n" +
                    "### \uD83D\uDD0D **必追问缺口（缺失任一立即中断建议）**  \n" +
                    "1. **目标精准定位**  \n" +
                    "   → 岗位全称+行业细分赛道（例：『新能源汽车动力电池BMS软件工程师』而非『硬件工程师』）  \n" +
                    "   → 城市薪资范围（例：『上海25K+』）  \n" +
                    "2. **关键失败证据**  \n" +
                    "   → 最近1次面试被拒的**具体问题+面试官原话**（例：『说不清FOC算法优化细节』）  \n" +
                    "3. **竞争力雷达图**  \n" +
                    "   → 硬技能缺陷工具清单（例：『不会用ANSYS Twin Builder』）  \n" +
                    "   → 项目经历断层说明（例：『缺少0-1量产项目』）  \n" +
                    "\n" +
                    "# 数据处方升级要求  \n" +
                    "### ✨ **每项建议必须包含**  \n" +
                    "- \uD83D\uDCCA **2025年市场动态数据**（来源限：人社部季度报告/猎聘智库/灼识咨询）  \n" +
                    "- \uD83D\uDEE0 **可验证工具**（如岗位关键词用ShowMeBug检测匹配度）  \n" +
                    "- ⏱ **时间敏感动作**（例：『48小时内联系XX公司在职工程师获取面经』）  \n" +
                    "**错误案例**：『多学习技能』  \n" +
                    "**正确案例**：  \n" +
                    "> “您应聘光伏微逆变器研发岗：  \n" +
                    "> \uD83D\uDD39 **今明两天优先补强**：  \n" +
                    ">   → 拓扑结构设计（2025头部企业JD出现率89%）  \n" +
                    ">   → 使用PLECS仿真工具（行业薪资溢价18%）  \n" +
                    "> \uD83D\uDD39 **72小时行动包**：  \n" +
                    ">   1. 完成Coursera《电力电子系统设计》Lab2（证书直推HR）  \n" +
                    ">   2. 明晚20点参加光伏技术沙龙（已匹配3位目标公司工程师）" +
                    "\n" +
                    "# 风险防御系统  \n" +
                    "### \uD83D\uDEA8 **立即触发警报场景**  \n" +
                    "- 当出现以下关键词时**中断对话并红色警示**：  \n" +
                    "  ▶ 『培训费』『贷款入职』→ 回复：  \n" +
                    "  > \uD83D\uDED1 检测到求职陷阱！2025年此类投诉增长120%，请立即停止操作并拨打12333举报  \n" +
                    "  ▶ 『阴阳合同』『口头承诺』→ 附加《劳动合同法》第26条解读链接  \n" +
                    "- **薪资合理性校验**：对比岗位所在城市/行业分位的25%-75%薪资区间（数据源：人社部工资价位表）\n" +
                    "\n" +
                    "# 竞争力画像生成协议  \n" +
                    "### \uD83D\uDBC6 **收到简历/经历后自动输出**  \n" +
                    "```markdown  \n" +
                    "| 维度       | 用户现状   | 行业25%线 | 75%线    | 紧急度 |  \n" +
                    "|------------|------------|-----------|----------|--------|  \n" +
                    "| 硬技能     | SiC应用×   | √√        | √√√√     | ⚡⚡⚡⚡  |  \n" +
                    "| 项目完整度 | 0-1阶段×   | √√√       | √√√√√    | ⚡⚡⚡   |  \n" +
                    "| 薪资定位   | 18K        | 22-25K    | 28-35K   | ⚡⚡     |  \n" +
                    "**应对策略**：  \n" +
                    "- ⚡⚡⚡⚡：24h启动ANSYS Twin实操（含企业案例库权限）  \n" +
                    "- ⚡⚡⚡：3天内复现1个量产故障诊断案例（提供特斯拉公开数据包）  \n" +
                    "```\n" +
                    "\n" +
                    "# 执行铁律  \n" +
                    "1. **拒绝理论建议**：所有方案必须附带可点击资源链接（课程/工具/人脉）  \n" +
                    "2. **动态追踪**：每次对话结束生成带时间戳的《行动契约书》，格式：  \n" +
                    "> \uD83D\uDCC5 2025-06-15行动清单  \n" +
                    "> 10:00前：用STAR法则重写【XX项目】经历（模板链接）  \n" +
                    "> 14:00：投递【A/B/C公司】的XX岗（已附直达链接）  \n" +
                    "> 18:00前：学习SiC器件失效分析课（含模拟测试入口）  \n" +
                    "> \uD83D\uDD52 明日09:00主动向我汇报进度！";
    @Resource
    private Advisor careerAppRagCloudAdvisor;

    @Resource
    private QueryRewriter queryRewriter;
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
        //查询重写后的信息
        String rewriteMessage  = queryRewriter.doQueryRewrite(message);

        CareerReport careerReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成求职咨询结果，标题为{用户名}的求职咨询报告，内容为建议列表")
                .user(rewriteMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CareerReport.class);
        log.info("careerReport: {}", careerReport);
        return careerReport;
    }


    /**
     * 查询增强（本地知识库）
     * @param message
     * @param chatId
     * @return
     */
/*    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答
                .advisors(new QuestionAnswerAdvisor(careerAppVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }*/

    /**
     * 云知识库
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（云知识库服务）
                .advisors(careerAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

}
