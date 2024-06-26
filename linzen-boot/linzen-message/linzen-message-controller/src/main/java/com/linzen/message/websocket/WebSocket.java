package com.linzen.message.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.linzen.base.PageModel;
import com.linzen.base.UserInfo;
import com.linzen.base.service.SysconfigService;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.message.entity.ImContentEntity;
import com.linzen.message.entity.MessageEntity;
import com.linzen.message.entity.MessageReceiveEntity;
import com.linzen.message.model.ImUnreadNumModel;
import com.linzen.message.model.message.PaginationMessage;
import com.linzen.message.model.websocket.onconnettion.OnConnectionModel;
import com.linzen.message.model.websocket.onconnettion.OnLineModel;
import com.linzen.message.model.websocket.receivemessage.ReceiveMessageModel;
import com.linzen.message.model.websocket.savafile.ImageMessageModel;
import com.linzen.message.model.websocket.savafile.VoiceMessageModel;
import com.linzen.message.model.websocket.savamessage.SavaMessageModel;
import com.linzen.message.service.ImContentService;
import com.linzen.message.service.MessageService;
import com.linzen.message.service.UserDeviceService;
import com.linzen.message.util.*;
import com.linzen.message.util.unipush.UinPush;
import com.linzen.model.BaseSystemInfo;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息聊天
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
@ServerEndpoint(value = "/api/message/websocket/{token}")
@Scope("prototype")
public class WebSocket {

    private UserProvider userProvider;
    private ImContentService imContentService;
    private MessageService messageService;
    private ConfigValueUtil configValueUtil;
    private UserInfo userInfo;
    private UserService userService;
    private SysconfigService sysConfigService;
    private UinPush uinPush;
    private UserDeviceService userDeviceService;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.init();
        this.userInfo = userProvider.get(token);
        if (this.userInfo.getUserId() == null) {
            try{
                OnlineUserProvider.closeFrontWs(null, session);
                session.close();
            }catch (Exception e){}
            log.info("WS建立链接, TOKEN无效:{}, {}", session.getId(), token);
        }else {
            log.info("WS建立链接:{}, {}", session.getId(), token);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        OnlineUserModel user =  OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().orElse(null);
        if (user != null) {
            OnlineUserProvider.removeWebSocketByToken(user.getToken());
            log.info("WS连接断开: {}, {}, {}, {}", user.getTenantId(), user.getUserId(), session.getId(), user.getToken());
        }else{
            log.debug("WS连接断开, 无用户信息: {}", session.getId());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try{
            processMessage(message, session);
        }finally {
            //多租户切换后清除缓存
            UserProvider.clearLocalUser();
            TenantDataSourceUtil.clearLocalTenantInfo();
        }
    }

    private void processMessage(String message, Session session){
        log.debug("WS消息内容: {}, {}", session.getId(), message);
        JSONObject receivedMessage = JSONObject.parseObject(message);
        String receivedMethod = receivedMessage.getString(MessageParameterEnum.PARAMETER_METHOD.getValue());
        String receivedToken = receivedMessage.getString(MessageParameterEnum.PARAMETER_TOKEN.getValue());
        //验证TOKEN
        this.userInfo = userProvider.get(receivedMessage.getString(MessageParameterEnum.PARAMETER_TOKEN.getValue()));
        if (this.userInfo.getUserId() == null) {
            log.info("WSToken无效: {}, {}", session.getId(), message);
            OnlineUserProvider.closeFrontWs(null, session);
            return;
        }
        //判断是否为多租户
        if(!isMultiTenancy()){
            log.info("WS切库失败: {}, {}, {}, {}", userInfo.getTenantId(), userInfo.getUserId(), session.getId(), receivedToken);
            //切库失败
            OnlineUserProvider.closeFrontWs(null, session);
        }
        switch (receivedMethod) {
            case ConnectionType.CONNECTION_ONCONNECTION:
                //建立连接
                log.info("WS开启连接: {}, {}, {}, {}", userInfo.getTenantId(), userInfo.getUserId(), session.getId(), receivedToken);
                if(OnlineUserProvider.getOnlineUserList().stream().anyMatch(t -> t.getWebSocket().getId().equals(session.getId()))){
                    //WS已存在
                    log.info("WS已存在: {}, {}, {}, {}", userInfo.getTenantId(), userInfo.getUserId(), session.getId(), receivedToken);
                    return;
                }
                //Token已存在, 关闭之前的WebSocket, 继续执行后续代码添加新的WebSocket
                List<OnlineUserModel> tokenList = OnlineUserProvider.getOnlineUserList().stream().filter(t-> {
                    if(receivedToken.equals(t.getToken())){
                        OnlineUserProvider.closeFrontWs(t, t.getWebSocket());
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                OnlineUserProvider.getOnlineUserList().removeAll(tokenList);

                //app-true, PC-false
                Boolean isMobileDevice = receivedMessage.getBoolean(MessageParameterEnum.PARAMETER_MOBILEDEVICE.getValue());
                if (userInfo != null && userInfo.getUserId() != null) {
                    OnlineUserModel model = new OnlineUserModel();
                    model.setConnectionId(session.getId());
                    model.setUserId(userInfo.getUserId());
                    model.setTenantId(userInfo.getTenantId());
                    model.setIsMobileDevice(isMobileDevice);
                    model.setWebSocket(session);
                    model.setToken(receivedToken);
                    model.setSystemId(model.getIsMobileDevice() ? userInfo.getAppSystemId() : userInfo.getSystemId());
                    BaseSystemInfo sysInfo = sysConfigService.getSysInfo();
                    //判断是否在线
                    isOnLine(sysInfo, model);

                    List<OnlineUserModel> onlineUserList = OnlineUserProvider.getOnlineUserList().stream().filter(q -> !q.getUserId().equals(userInfo.getUserId()) && q.getTenantId().equals(userInfo.getTenantId())).collect(Collectors.toList());
                    //反馈信息给登录者
                    List<String> onlineUsers = onlineUserList.stream().map(t -> t.getUserId()).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                    List<ImUnreadNumModel> unreadNums = imContentService.getUnreadList(userInfo.getUserId());
                    int unreadNoticeCount = messageService.getUnreadCount(userInfo.getUserId(), 1);
                    int unreadMessageCount = messageService.getUnreadCount(userInfo.getUserId(), 2);
                    int unreadScheduleCount = messageService.getUnreadCount(userInfo.getUserId(),4);
                    int unreadSystemMessageCount = messageService.getUnreadCount(userInfo.getUserId(), 3);
                    MessageEntity noticeDefaultText = messageService.getInfoDefault(1);
                    PaginationMessage pagination = new PaginationMessage();
                    pagination.setCurrentPage(1);
                    pagination.setPageSize(1);
                    List<MessageReceiveEntity> list = messageService.getMessageList(pagination, pagination.getType(),userInfo.getUserId());
                    MessageReceiveEntity messageDefaultText = new MessageReceiveEntity();
                    if(list.size()>0){
                        messageDefaultText = list.get(0);
                    }
                    String noticeText = noticeDefaultText.getTitle() != null ? noticeDefaultText.getTitle() : "";
                    String messageText = messageDefaultText.getTitle() != null ? messageDefaultText.getTitle() : "";
                    Long noticeTime = noticeDefaultText.getCreatorTime() != null ? noticeDefaultText.getCreatorTime().getTime() : 0;
                    Long messageTime = messageDefaultText.getCreatorTime() != null ? messageDefaultText.getCreatorTime().getTime() : 0;
                    //转model后上传到mq服务器上
                    OnConnectionModel onConnectionModel = new OnConnectionModel();
                    onConnectionModel.setMethod(MessageChannelType.CHANNEL_INITMESSAGE);
                    onConnectionModel.setOnlineUsers(onlineUsers);
                    onConnectionModel.setUnreadNums(JsonUtil.listToJsonField(unreadNums));
                    onConnectionModel.setUnreadNoticeCount(unreadNoticeCount);
//                    onConnectionModel.setNoticeDefaultText(noticeText);
                    onConnectionModel.setUnreadMessageCount(unreadMessageCount);
                    onConnectionModel.setUnreadSystemMessageCount(unreadSystemMessageCount);
                    onConnectionModel.setUnreadScheduleCount(unreadScheduleCount);
                    onConnectionModel.setMessageDefaultText(messageText);
                    onConnectionModel.setMessageDefaultTime(messageTime);
                    onConnectionModel.setUserId(userInfo.getUserId());
                    int total = unreadNoticeCount+unreadMessageCount+unreadSystemMessageCount+unreadScheduleCount;
                    onConnectionModel.setUnreadTotalCount(total);
                    OnlineUserProvider.sendMessage(session, onConnectionModel);
                    //通知所有在线用户，有用户在线
                    for (OnlineUserModel item : onlineUserList) {
                        if (!item.getUserId().equals(userInfo.getUserId())) {
                            //创建模型
                            OnLineModel remindUserModel = new OnLineModel(MessageChannelType.CHANNEL_ONLINE, userInfo.getUserId());
                            OnlineUserProvider.sendMessage(item, remindUserModel);
                        }
                    }

                }
                break;
            case ConnectionType.CONNECTION_SENDMESSAGE:
                //发送消息
                String toUserId = receivedMessage.getString(MessageParameterEnum.PARAMETER_TOUSERID.getValue());
                //text/voice/image
                String messageType = receivedMessage.getString(MessageParameterEnum.PARAMETER_MESSAGETYPE.getValue());
                String messageContent = receivedMessage.getString(MessageParameterEnum.PARAMETER_MESSAGECONTENT.getValue());
                String tenantId = userProvider.get(receivedMessage.getString(MessageParameterEnum.PARAMETER_TOKEN.getValue())).getTenantId();

                String fileName = "";
                if (!SendMessageTypeEnum.MESSAGE_TEXT.getMessage().equals(messageType)) {
                    JSONObject object = JSONObject.parseObject(messageContent);
                    fileName = object.getString("name");
                }
                List<OnlineUserModel> user = OnlineUserProvider.getOnlineUserList().stream().filter(q -> String.valueOf(q.getUserId()).equals(String.valueOf(userInfo.getUserId())) && String.valueOf(q.getTenantId()).equals(tenantId)).collect(Collectors.toList());
                OnlineUserModel onlineUser = user.size() > 0 ? user.get(0) : null;
                List<OnlineUserModel> toUser = OnlineUserProvider.getOnlineUserList().stream().filter(q -> String.valueOf(q.getTenantId()).equals(String.valueOf(onlineUser.getTenantId())) && String.valueOf(q.getUserId()).equals(String.valueOf(toUserId))).collect(Collectors.toList());
                if (user.size() != 0) {
                    //saveMessage
                    if (SendMessageTypeEnum.MESSAGE_TEXT.getMessage().equals(messageType)) {
                        messageContent = XSSEscape.escape(messageContent);
                        imContentService.sendMessage(onlineUser.getUserId(), toUserId, messageContent, messageType);
                    } else if (SendMessageTypeEnum.MESSAGE_IMAGE.getMessage().equals(messageType)) {
                        JSONObject image = new JSONObject();
                        image.put("path", UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                        image.put("width", JSONObject.parseObject(messageContent).getString("width"));
                        image.put("height", JSONObject.parseObject(messageContent).getString("height"));
                        imContentService.sendMessage(onlineUser.getUserId(), toUserId, image.toJSONString(), messageType);
                    } else if (SendMessageTypeEnum.MESSAGE_VOICE.getMessage().equals(messageType)) {
                        JSONObject voice = new JSONObject();
                        voice.put("path", UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                        voice.put("length", JSONObject.parseObject(messageContent).getString("length"));
                        imContentService.sendMessage(onlineUser.getUserId(), toUserId, voice.toJSONString(), messageType);
                    }
                    for (int i = 0; i < user.size(); i++) {
                        OnlineUserModel model = user.get(i);
                        //组装model
                        SavaMessageModel savaMessageModel = new SavaMessageModel();
                        savaMessageModel.setMethod(MessageChannelType.CHANNEL_SENDMESSAGE);
                        savaMessageModel.setUserId(model.getUserId());
                        savaMessageModel.setToUserId(toUserId);
                        savaMessageModel.setDateTime(DateUtil.getNowDate().getTime());
                        //头像
                        savaMessageModel.setHeadIcon(UploaderUtil.uploaderImg(userInfo.getUserIcon()));
                        //最新消息
                        savaMessageModel.setLatestDate(DateUtil.getNowDate().getTime());
                        //用户姓名
                        savaMessageModel.setRealName(userInfo.getUserName());
                        savaMessageModel.setAccount(userInfo.getUserAccount());
                        //对方的名称账号头像
                        Map<String, String> headers = ImmutableMap.of(Constants.AUTHORIZATION.toLowerCase(), receivedToken);
                        SysUserEntity entity = userService.getInfo(toUserId);
                        savaMessageModel.setToAccount(entity.getAccount());
                        savaMessageModel.setToRealName(entity.getRealName());
                        savaMessageModel.setToHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));

                        if (SendMessageTypeEnum.MESSAGE_TEXT.getMessage().equals(messageType)) {
                            savaMessageModel.setMessageType(messageType);
                            savaMessageModel.setToMessage(messageContent);
                        } else if (SendMessageTypeEnum.MESSAGE_IMAGE.getMessage().equals(messageType)) {
                            //构建图片模型
                            ImageMessageModel messageModel = getImageModel(messageContent, UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                            savaMessageModel.setToMessage(messageModel);
                            savaMessageModel.setMessageType(messageType);
                        } else if (SendMessageTypeEnum.MESSAGE_VOICE.getMessage().equals(messageType)) {
                            //构建语音模型
                            VoiceMessageModel messageModel = getVoiceMessageModel(messageContent, UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                            savaMessageModel.setMessageType(messageType);
                            savaMessageModel.setToMessage(messageModel);
                        }
                        /*List<String> cidList = userDeviceService.getCidList(toUserId);
                        if(cidList != null && cidList.size()>0){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type","3");
                            jsonObject.put("name",userInfo.getUserName() + "/"+userInfo.getUserAccount());
                            jsonObject.put("formUserId",userInfo.getUserId());
                            jsonObject.put("headIcon",UploaderUtil.uploaderImg(userInfo.getUserIcon()));
//                            jsonObject.put("title",userInfo.getUserName() + "/"+userInfo.getUserAccount());
                            String text = JSONObject.toJSONString(jsonObject);
                            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                            text = Base64.getEncoder().encodeToString(bytes);
                            uinPush.sendUniPush(cidList, userInfo.getUserName(), "你有一条看聊天消息", "3", text);
                        }*/
                        OnlineUserProvider.sendMessage(model, savaMessageModel);
                    }
                }

                //接受消息
                ReceiveMessageModel receiveMessageModel = new ReceiveMessageModel();
                receiveMessageModel.setMethod(MessageChannelType.CHANNEL_RECEIVEMESSAGE);
                receiveMessageModel.setFormUserId(onlineUser.getUserId());
                receiveMessageModel.setDateTime(DateUtil.getNowDate().getTime());
                //头像
                receiveMessageModel.setHeadIcon(UploaderUtil.uploaderImg(userInfo.getUserIcon()));
                //最新消息
                receiveMessageModel.setLatestDate(DateUtil.getNowDate().getTime());
                //用户姓名
                receiveMessageModel.setRealName(userInfo.getUserName());
                receiveMessageModel.setAccount(userInfo.getUserAccount());
                receiveMessageModel.setUserId(toUserId);
                if (toUser.size() != 0) {
                    for (int i = 0; i < toUser.size(); i++) {
                        OnlineUserModel onlineToUser = toUser.get(i);
                        if (SendMessageTypeEnum.MESSAGE_TEXT.getMessage().equals(messageType)) {
                            receiveMessageModel.setMessageType(messageType);
                            receiveMessageModel.setFormMessage(messageContent);
                        } else if (SendMessageTypeEnum.MESSAGE_IMAGE.getMessage().equals(messageType)) {
                            //构建图片模型
                            ImageMessageModel messageModel = getImageModel(messageContent, UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                            receiveMessageModel.setMessageType(messageType);
                            receiveMessageModel.setFormMessage(messageModel);
                        } else if (SendMessageTypeEnum.MESSAGE_VOICE.getMessage().equals(messageType)) {
                            //构建语音模型
                            VoiceMessageModel messageModel = getVoiceMessageModel(messageContent, UploaderUtil.uploaderImg("/api/file/Image/IM/", fileName));
                            receiveMessageModel.setMessageType(messageType);
                            receiveMessageModel.setFormMessage(messageModel);
                        }
                        OnlineUserProvider.sendMessage(onlineToUser, receiveMessageModel);
                    }
                }
                break;
            case "UpdateReadMessage":
                //更新已读
                String formUserId = receivedMessage.getString("formUserId");
                onlineUser = OnlineUserProvider.getOnlineUserList().stream().filter(q -> String.valueOf(q.getConnectionId()).equals(String.valueOf(session.getId()))).findFirst().orElse(new OnlineUserModel());
                if (onlineUser != null) {
                    imContentService.readMessage(formUserId, onlineUser.getUserId());
                }
                break;
            case "MessageList":
                //获取消息列表
                String sendUserId = receivedMessage.getString("toUserId");
                String receiveUserId = receivedMessage.getString("formUserId");
                PageModel pageModel = new PageModel();
                pageModel.setPage(receivedMessage.getInteger("currentPage"));
                pageModel.setRows(receivedMessage.getInteger("pageSize"));
                pageModel.setSord(receivedMessage.getString("sord"));
                pageModel.setKeyword(receivedMessage.getString("keyword"));
                List<ImContentEntity> data = imContentService.getMessageList(sendUserId, receiveUserId, pageModel).stream().sorted(Comparator.comparing(ImContentEntity::getSendTime)).collect(Collectors.toList());
                JSONObject object = new JSONObject();
                object.put("method", "messageList");
                object.put("list", JsonUtil.createListToJsonArray(data));
                JSONObject pagination = new JSONObject();
                pagination.put("total", pageModel.getRecords());
                pagination.put("currentPage", pageModel.getPage());
                pagination.put("pageSize", receivedMessage.getInteger("pageSize"));
                object.put("pagination", pagination);
                OnlineUserProvider.sendMessage(session, object);
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否在线
     *
     * @param model
     */
    private void isOnLine(BaseSystemInfo systemInfo, OnlineUserModel model) {
        // 不允许多人登录
        if ("1".equals(String.valueOf(systemInfo.getSingleLogin()))) {
            Long userAll = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getUserId().equals(userInfo.getUserId()) && t.getTenantId().equals(userInfo.getTenantId())).count();
            Long userAllMobile = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getUserId().equals(userInfo.getUserId()) && t.getTenantId().equals(userInfo.getTenantId()) && t.getIsMobileDevice().equals(true)).count();
            Long userAllWeb = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getUserId().equals(userInfo.getUserId()) && t.getTenantId().equals(userInfo.getTenantId()) && t.getIsMobileDevice().equals(false)).count();
            //都不在线
            if (userAll == 0) {
                OnlineUserProvider.addModel(model);
            }
            //手机在线
            else if (userAllMobile != 0 && userAllWeb == 0) {
                if (!model.getIsMobileDevice()) {
                    OnlineUserProvider.addModel(model);
                }
            }
            //电脑在线
            else {
                if (model.getIsMobileDevice()) {
                    OnlineUserProvider.addModel(model);
                }
            }
        } else {
            //同时登录不限制
            OnlineUserProvider.addModel(model);
        }
    }

    /**
     * 判断是否为多租户
     *
     */
    private boolean isMultiTenancy() {
        if (configValueUtil.isMultiTenancy()) {
            //多租户需要切库
            if (StringUtil.isNotEmpty(userInfo.getTenantId())) {
                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 构建图片消息模型
     *
     * @param messageContent
     * @param fileName
     * @return
     */
    private ImageMessageModel getImageModel(String messageContent, String fileName) {
        String width = JSONObject.parseObject(messageContent).getString("width");
        String height = JSONObject.parseObject(messageContent).getString("height");
        return new ImageMessageModel(width, height, fileName);
    }

    /**
     * 构建语音模型
     *
     * @param messageContent
     * @param fileName
     * @return
     */
    private VoiceMessageModel getVoiceMessageModel(String messageContent, String fileName) {
        String length = JSONObject.parseObject(messageContent).getString("length");
        return new VoiceMessageModel(length, fileName);
    }

    @OnError
    public void onError(Session session, Throwable error) {
//        OnlineUserModel user = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().isPresent() ? OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().get() : null;
//        if (user != null) {
//            log.error("调用onError,租户：" + user.getTenantId() + ",用户：" + user.getUserId());
//        }
        try {
            onClose(session);
        } catch (Exception e) {
//            log.error("发生error,调用onclose失败，session为：" + session);
        }
        if (error.getMessage() != null) {
            OnlineUserModel user = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().isPresent() ? OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().get() : null;
            if(user != null){
                log.error("WS发生错误: {}, {}, {}, {}, {}", user.getTenantId(), user.getUserId(), session.getId(), error.getMessage(), user.getToken());
            }else{
                log.error("WS发生错误", error);
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        messageService = SpringContext.getBean(MessageService.class);
        imContentService = SpringContext.getBean(ImContentService.class);
        configValueUtil = SpringContext.getBean(ConfigValueUtil.class);
        userProvider = SpringContext.getBean(UserProvider.class);
        userService = SpringContext.getBean(UserService.class);
        sysConfigService = SpringContext.getBean(SysconfigService.class);
        uinPush = SpringContext.getBean(UinPush.class);
        userDeviceService = SpringContext.getBean(UserDeviceService.class);
    }

}
