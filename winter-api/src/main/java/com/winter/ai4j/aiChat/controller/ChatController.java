package com.winter.ai4j.aiChat.controller;

import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ClassName: ChatLlamaController
 * <blockquote><pre>
 * Description: [基本会话的控制层，由业务层指向不同的模型实现]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/8/12 下午5:10
 * @version 1.0.0
 * @since 1.0.0
 */
@Api(tags = "Coze模块")
@Slf4j
@RestController
@RequestMapping(value = "/system/chat")
public class ChatController {

    @Autowired
    @Qualifier("chatByCozeServiceImpl")
    private ChatService chatByCoseService;

    @Autowired
    @Qualifier("chatByLlamaServiceImpl")
    private ChatService chatByLlamaService;


    /**
     * Coze-创建会话
     * @param userId 用户ID
     * @return 创建chat 结果
     */
    @ApiOperation(value = "chat-创建会话", notes = "创建对话")
    @PostMapping(value = "/create")
    public Result<String> createCoze(@RequestBody String userId) {
        String chat = chatByCoseService.createChat();
        return Result.ok(chat);
    }


    /**
     * Coze-进行对话
     * @return 进行对话结果
     */
    @ApiOperation(value = "chat-进行对话", notes = "进行对话")
    @PostMapping(value = "/question")
    public SseEmitter chatByCoze(@RequestBody QuestionDTO question) {
        // TODO 后期载入分布式锁，防止用户发起多次提问

        // 创建SseEmitter对象，注意这里的timeout是发送时间，不是超时时间，网上的文档有问题
        SseEmitter emitter = new SseEmitter(1800000L);
        emitter.onCompletion(() -> {});
        emitter.onTimeout(() -> {});
        chatByCoseService.question(emitter, question);
        // chatByLlamaService.questionDTO(emitter); // ollama 存在问题，先不要用
        return emitter;
    }




}
