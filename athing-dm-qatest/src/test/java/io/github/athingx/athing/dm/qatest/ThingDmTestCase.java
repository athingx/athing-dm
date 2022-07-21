package io.github.athingx.athing.dm.qatest;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.platform.message.ThingDmPostPropertyMessage;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import io.github.athingx.athing.dm.thing.dump.DumpToFn;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ThingDmTestCase extends PuppetSupport {

    @Test
    public void test$thing$dm$dump() throws Exception {
        final Map<String, String> dumpMap = new HashMap<>();
        thingDm.dump()
                .dumpTo(dumpMap::putAll)
                .dumpTo(new DumpToFn.ToMap(map -> map.forEach((compId, json) -> System.out.printf("component-id:%s\n%s\n%n", compId, json))))
                .dumpTo(new DumpToFn.ToZip(new File("dump.zip")));
        Assert.assertTrue(dumpMap.containsKey("echo"));
        Assert.assertTrue(dumpMap.containsKey("light"));
    }

    @Test
    public void test$thing$post_properties$success() throws Exception {
        final var brightId = Identifier.toIdentifier("light", "bright");
        final var stateId = Identifier.toIdentifier("light", "state");
        final var colorId = Identifier.toIdentifier("light", "color");
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



}
