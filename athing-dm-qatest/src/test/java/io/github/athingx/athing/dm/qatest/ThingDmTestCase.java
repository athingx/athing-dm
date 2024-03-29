package io.github.athingx.athing.dm.qatest;

import io.github.athingx.athing.dm.platform.message.ThingDmPostEventMessage;
import io.github.athingx.athing.dm.platform.message.ThingDmPostPropertyMessage;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import io.github.athingx.athing.dm.qatest.puppet.event.LightBrightChangedEvent;
import io.github.athingx.athing.dm.thing.dump.DumpTo;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static io.github.athingx.athing.dm.api.Identifier.toIdentifier;

public class ThingDmTestCase extends PuppetSupport {

    @Test
    public void test$thing$dm$dump() throws Exception {

        thingDm.dumpTo(DumpTo.toZip(new File("dump.zip")));
        
        final Map<String, String> dumpMap = thingDm.dumpTo(DumpTo.toMap());
        dumpMap.forEach((compId, json) -> System.out.printf("component-id:%s\n%s\n%n", compId, json));
        Assert.assertTrue(dumpMap.containsKey("echo"));
        Assert.assertTrue(dumpMap.containsKey("light"));
    }

    @Test
    public void test$thing$post_properties$success() throws Exception {
        final var brightId = toIdentifier("light", "bright");
        final var stateId = toIdentifier("light", "state");
        final var colorId = toIdentifier("light", "color");
        final String token = thingDm.properties(brightId, stateId, colorId).get().token();
        final ThingDmPostPropertyMessage message = waitingForPostMessageByToken(token);
        Assert.assertEquals(token, message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertTrue(message.getPropertyIds().contains(brightId.getIdentity()));
        Assert.assertTrue(message.getPropertyIds().contains(stateId.getIdentity()));
        Assert.assertTrue(message.getPropertyIds().contains(colorId.getIdentity()));
        Assert.assertTrue(message.getPropertySnapshot(brightId).getValue() instanceof Integer);
        Assert.assertTrue(message.getPropertySnapshot(stateId).getValue() instanceof LightComp.State);
        Assert.assertTrue(message.getPropertySnapshot(colorId).getValue() instanceof LightComp.Color);
    }

    @Test
    public void test$thing$post_event$success() throws Exception {
        final var event = new LightBrightChangedEvent(50, 100);
        final String token = thingDm.event(event).get().token();
        final ThingDmPostEventMessage message = waitingForPostMessageByToken(token);
        Assert.assertEquals(token, message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertTrue(message.getOccurTimestamp() > 0);
        Assert.assertTrue(message.getData() instanceof LightBrightChangedEvent.Data);
        if (message.getData() instanceof LightBrightChangedEvent.Data data) {
            Assert.assertEquals(50, data.from());
            Assert.assertEquals(100, data.to());
        }
    }

}
