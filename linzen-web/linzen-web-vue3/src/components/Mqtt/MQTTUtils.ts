import * as mqtt from "mqtt/dist/mqtt.min";
import type { MqttClient, OnMessageCallback } from 'mqtt';
import { ref, onUnmounted } from 'vue';
// 连接字符串, 通过协议指定使用的连接方式
// ws 未加密 WebSocket 连接
// wss 加密 WebSocket 连接
// mqtt 未加密 TCP 连接
// mqtts 加密 TCP 连接
// wxs 微信小程序连接
// alis 支付宝小程序连接
export const deviceTopicPrefix = "linzer/+/linzer-driver-mqtt/data/";

class MQTT {
    topic: string[]; //订阅地址
    client!: MqttClient;
    constructor(topic: string[]) {
        this.topic = topic;
    }
    randomName(len) {
        var len = len || 16;
        var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhjkmnprstwxyz23456789';
        var maxPos = chars.length;
        var str = '';
        for (let i = 0; i < len; i++) {
            str += chars.charAt(Math.floor(Math.random() * maxPos));
        }
        return new Date().getTime() + str;
    }
    //初始化mqtt
    init() {
        const connection = {
            protocol: "ws",
            host: '127.0.0.1',
            port: 8083,
            endpoint: '/mqtt',
            clean: true, // 保留会话
            connectTimeout: 4000, // 超时时间
            reconnectPeriod: 4000, // 重连时间间隔
            // 认证信息
            // clientid长度限制 MQTT协议规定,clientid的最大长度为23个字符。
            // 这个限制是为了保证协议的性能和可靠性。
            // 如果clientid超过了这个长度限制,服务器可能会拒绝连接请求
            clientId: 'MQTT_' + this.randomName(16),
            username: 'admin',
            password: 'public',
        };
        const { protocol, host, port, endpoint, ...options } = connection;
        const connectUrl = `${protocol}://${host}:${port}${endpoint}`;
        console.info(connectUrl);
        this.client = mqtt.connect(connectUrl, options);

        this.client.on('error', (error: any) => {
            console.log("MQTT 连接错误", error);
        });
        this.client.on('reconnect', (error: Error) => {
            console.log("MQTT 重连", error);
        });
    }
    //取消订阅
    unsubscribes(topic: string[]) {
        topic.forEach((item) => {
            this.client.unsubscribe(deviceTopicPrefix + item, (error: Error) => {
                if (!error) {
                    console.log(deviceTopicPrefix + item, '取消订阅成功');
                } else {
                    console.log(deviceTopicPrefix + item, '取消订阅失败');
                }
            });
        })
    }
    //连接
    link(topic: string[]) {
        topic.forEach((item) => {
            this.client.on('connect', () => {
                this.client.subscribe(deviceTopicPrefix + item, (error: any) => {
                    if (!error) {
                        console.log('订阅成功', deviceTopicPrefix + item);
                    } else {
                        console.log('订阅失败:', deviceTopicPrefix + item);
                    }
                });
            });
        })
    }
    //收到的消息
    get(callback: OnMessageCallback) {
        this.client.on('message', callback);
    }
    //结束链接
    over() {
        this.client.end();
    }
}

export default function useMqtt() {
    let topicData: string[];
    const PublicMqtt = ref<MQTT | null>(null);

    const startMqtt = (val: string[], callback: OnMessageCallback) => {
        topicData = val;
        //设置订阅地址
        PublicMqtt.value = new MQTT(val);
        //初始化mqtt
        PublicMqtt.value.init();
        //链接mqtt
        PublicMqtt.value.link(val);
        getMessage(callback);
    };
    const getMessage = (callback: OnMessageCallback) => {
        // PublicMqtt.value?.client.on('message', callback);
        PublicMqtt.value?.get(callback);
    };
    onUnmounted(() => {
        //页面销毁结束订阅
        if (PublicMqtt.value) {
            PublicMqtt.value.unsubscribes(topicData);
            PublicMqtt.value.over();
        }
    });

    return {
        startMqtt,
    };
}

// 使用
// import useMqtt from '@/composables/utils/useMqtt';
// const { startMqtt } = useMqtt();
// startMqtt('主题topic', (topic, message) => {
//    const msg = JSON.parse(message.toString());
//    console.log(msg);
// });