package io.github.athingx.athing.dm.qatest;

import io.github.athingx.athing.dm.platform.ThingDmTemplate;
import io.github.athingx.athing.dm.platform.builder.ThingDmPlatformBuilder;
import io.github.athingx.athing.dm.platform.message.ThingDmPostMessage;
import io.github.athingx.athing.dm.qatest.message.QaThingMessageGroupListener;
import io.github.athingx.athing.dm.qatest.message.QaThingPostMessageListener;
import io.github.athingx.athing.dm.qatest.message.QaThingReplyMessageListener;
import io.github.athingx.athing.dm.qatest.puppet.EchoComp;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import io.github.athingx.athing.dm.qatest.puppet.impl.PuppetCompImpl;
import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.builder.ThingDmBuilder;
import io.github.athingx.athing.platform.api.ThingPlatform;
import io.github.athingx.athing.platform.api.message.ThingReplyMessage;
import io.github.athingx.athing.platform.builder.ThingPlatformBuilder;
import io.github.athingx.athing.platform.builder.client.AliyunThingPlatformClientFactory;
import io.github.athingx.athing.platform.builder.message.AliyunJmsConnectionFactory;
import io.github.athingx.athing.platform.builder.message.AliyunThingMessageConsumerFactory;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.ThingPath;
import io.github.athingx.athing.thing.builder.ThingBuilder;
import io.github.athingx.athing.thing.builder.client.DefaultMqttClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * 傀儡支撑
 */
public class PuppetSupport implements LoadingProperties {

    protected static volatile Thing thing;
    protected static volatile ThingPlatform platform;
    protected static volatile ThingDm thingDm;
    protected static volatile ThingDmTemplate thingDmTemplate;

    private static final QaThingPostMessageListener qaThingPostMessageListener = new QaThingPostMessageListener();
    private static final QaThingReplyMessageListener qaThingReplyMessageListener = new QaThingReplyMessageListener();

    @BeforeClass
    public static void _before_class() throws Exception {
        thing = new ThingBuilder(new ThingPath(PRODUCT_ID, THING_ID))
                .client(new DefaultMqttClientFactory()
                        .remote(THING_REMOTE)
                        .secret(THING_SECRET)
                )
                .build();
        platform = new ThingPlatformBuilder()
                .client(new AliyunThingPlatformClientFactory()
                        .identity(PLATFORM_IDENTITY)
                        .secret(PLATFORM_SECRET))
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

        setup(thing, platform);
    }

    private static void setup(Thing thing, ThingPlatform platform) throws Exception {
        thingDm = new ThingDmBuilder()
                .build(thing);

        new ThingDmPlatformBuilder()
                .product(PRODUCT_ID, EchoComp.class, LightComp.class)
                .build(platform);
        thingDm.load(new PuppetCompImpl(thingDm));
        thingDmTemplate = platform.genThingTemplate(ThingDmTemplate.class, PRODUCT_ID, THING_ID);
    }


    @AfterClass
    public static void _after_class() {
        thing.destroy();
        platform.destroy();
    }

    public <T extends ThingReplyMessage> T waitingForReplyMessageByToken(String token) throws InterruptedException {
        return qaThingReplyMessageListener.waitingForReplyMessageByToken(token);
    }

    public <T extends ThingDmPostMessage> T waitingForPostMessageByToken(String token) throws InterruptedException {
        return qaThingPostMessageListener.waitingForPostMessageByToken(token);
    }

}
