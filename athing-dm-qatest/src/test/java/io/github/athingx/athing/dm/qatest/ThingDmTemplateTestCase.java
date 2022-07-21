package io.github.athingx.athing.dm.qatest;

import io.github.athingx.athing.dm.platform.helper.OpReturn;
import io.github.athingx.athing.dm.platform.helper.OpReturnHelper;
import io.github.athingx.athing.dm.platform.message.ThingDmReplyServiceReturnMessage;
import io.github.athingx.athing.dm.qatest.puppet.EchoComp;
import org.junit.Assert;
import org.junit.Test;

public class ThingDmTemplateTestCase extends PuppetSupport {

    @Test
    public void test$template$service$invoke$success() throws Exception {

        final var echoComp = thingDmTemplate.getThingDmComp("echo", EchoComp.class);
        final var req = new EchoComp.Echo("ECHO-ECHO:" + System.currentTimeMillis());

        final var syncResp = echoComp.syncEcho(req);
        Assert.assertEquals(req.words(), syncResp.words());

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() -> {
            final var future = echoComp.asyncEcho(req);
            Assert.assertFalse(future.isCancelled());
            Assert.assertFalse(future.isCompletedExceptionally());
        });
        final ThingDmReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
        Assert.assertEquals(opReturn.getToken(), message.getToken());
        Assert.assertEquals(PRODUCT_ID, message.getProductId());
        Assert.assertEquals(THING_ID, message.getThingId());
        Assert.assertTrue(message.getTimestamp() > 0);
        Assert.assertTrue(message.getData() instanceof EchoComp.Echo);
        if(message.getData() instanceof EchoComp.Echo asyncResp) {
            Assert.assertEquals(req.words(), asyncResp.words());
        }

    }

}
