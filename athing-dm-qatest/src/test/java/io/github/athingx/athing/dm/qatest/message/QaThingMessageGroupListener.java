package io.github.athingx.athing.dm.qatest.message;

import io.github.athingx.athing.platform.api.message.ThingMessage;
import io.github.athingx.athing.platform.api.message.ThingMessageListener;

public class QaThingMessageGroupListener implements ThingMessageListener {

    private final ThingMessageListener[] group;

    public QaThingMessageGroupListener(ThingMessageListener... group) {
        this.group = group;
    }

    @Override
    public void onMessage(ThingMessage message) throws Exception {
        for (ThingMessageListener listener : group) {
            listener.onMessage(message);
        }
    }

}
