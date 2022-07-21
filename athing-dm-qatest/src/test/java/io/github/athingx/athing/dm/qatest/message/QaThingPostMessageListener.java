package io.github.athingx.athing.dm.qatest.message;

import io.github.athingx.athing.dm.platform.message.ThingDmPostMessage;
import io.github.athingx.athing.platform.api.message.ThingMessage;
import io.github.athingx.athing.platform.api.message.ThingMessageListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class QaThingPostMessageListener implements ThingMessageListener {

    private final ConcurrentHashMap<String, Waiter> tokenWaiterMap = new ConcurrentHashMap<>();

    @Override
    public void onMessage(ThingMessage message) {

        if (!(message instanceof final ThingDmPostMessage postMsg)) {
            return;
        }
        final Waiter existed, current = new Waiter(postMsg);
        if ((existed = tokenWaiterMap.putIfAbsent(postMsg.getToken(), current)) != null) {
            existed.message = postMsg;
            existed.latch.countDown();
        }

    }

    @SuppressWarnings("unchecked")
    public <T extends ThingDmPostMessage> T waitingForPostMessageByToken(String token) throws InterruptedException {
        final Waiter existed, current = new Waiter();
        final Waiter waiter = (existed = tokenWaiterMap.putIfAbsent(token, current)) != null
                ? existed
                : current;
        waiter.latch.await();
        return (T) waiter.message;
    }

    private static class Waiter {

        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile ThingDmPostMessage message;

        public Waiter() {
        }

        public Waiter(ThingDmPostMessage message) {
            this.message = message;
            this.latch.countDown();
        }
    }

}
