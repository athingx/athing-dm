package io.github.athingx.athing.dm.qatest.message;

import io.github.athingx.athing.platform.api.message.ThingMessage;
import io.github.athingx.athing.platform.api.message.ThingMessageListener;
import io.github.athingx.athing.platform.api.message.ThingReplyMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 测试用设备消息监听器
 */
public class QaThingReplyMessageListener implements ThingMessageListener {

    private final ConcurrentHashMap<String, Waiter> tokenWaiterMap = new ConcurrentHashMap<>();

    @Override
    public void onMessage(ThingMessage message) {
        if (!(message instanceof final ThingReplyMessage replyMsg)) {
            return;
        }
        final Waiter existed, current = new Waiter(replyMsg);
        if ((existed = tokenWaiterMap.putIfAbsent(replyMsg.getToken(), current)) != null) {
            existed.message = replyMsg;
            existed.latch.countDown();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ThingReplyMessage> T waitingForReplyMessageByToken(String token) throws InterruptedException {
        final Waiter existed, current = new Waiter();
        final Waiter waiter = (existed = tokenWaiterMap.putIfAbsent(token, current)) != null
                ? existed
                : current;
        waiter.latch.await();
        return (T) waiter.message;
    }

    private static class Waiter {

        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile ThingReplyMessage message;

        public Waiter() {
        }

        public Waiter(ThingReplyMessage message) {
            this.message = message;
            this.latch.countDown();
        }

    }

}
