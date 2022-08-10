# 设备物模型

![License](https://img.shields.io/badge/license-MIT-brightgreen)
![Language](https://img.shields.io/badge/language-java-brightgreen)

## 模块简介

通过API和注解简化了物模型的定义和操作，避免了繁琐的阿里云平台配置和编码时JSON解析工作。

### 关键注解

|注解|解释|
|---|---|
|@ThDmComp|物模型组件|
|@ThDmEvent|物模型事件|
|@ThDmProperty|物模型属性|
|@ThDmService|物模型服务|

### 物模型定义注解示例

```java
@ThDmComp(id = ID)
public interface LightComp extends ThingDmComp {

    String ID = "light";

    @ThDmProperty
    int getBright();

    @ThDmService
    void changeBright(@ThDmParam("bright") int bright);

}
```

### 设备端注册服务

```java
// 初始化设备端
final var thing = new ThingBuilder(new ThingPath(PRODUCT_ID, THING_ID))
                .client(new AliyunMqttClientFactory()
                        .remote(THING_REMOTE)
                        .secret(THING_SECRET))
                .build();

// 构建设备模型
final var thingDm = new ThingDmBuilder()
                .build(thing)
                .get();

// 注册服务
thingDm.load(new LightCompImpl());                
```

### 平台端注册服务

```java
// 初始化平台端
final var platform = new ThingPlatformBuilder()
                // 阿里云IoT客户端
                .client(new AliyunIAcsClientFactory()
                        .identity(PLATFORM_IDENTITY)
                        .secret(PLATFORM_SECRET))
                // 阿里云消息消费者
                .consumer(new AliyunThingMessageConsumerFactory()
                        .queue(PLATFORM_JMS_GROUP)
                        .connection(new AliyunJmsConnectionFactory()
                                .queue(PLATFORM_JMS_GROUP)
                                .remote(PLATFORM_REMOTE)
                                .identity(PLATFORM_IDENTITY)
                                .secret(PLATFORM_SECRET))
                        .listener(new QaThingMessageGroupListener(
                                qaThingPostMessageListener,
                                qaThingReplyMessageListener
                        )))
                .build();

// 生成设备模型模板
final var thingDmTemplate = platform.genThingTemplate(ThingDmTemplate.class, PRODUCT_ID, THING_ID);

// 获取组件&并调用服务
final var lightComp = thingDmtemplate.getThingDmComp("light", LightComp.class);
lightComp.changeBright(100);
```