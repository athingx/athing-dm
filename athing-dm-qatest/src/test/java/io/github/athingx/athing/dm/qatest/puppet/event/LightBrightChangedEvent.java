package io.github.athingx.athing.dm.qatest.puppet.event;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;

public class LightBrightChangedEvent extends ThingDmEvent<LightBrightChangedEvent.Data> {

    public static final String ID = "light_bright_changed_event";

    public LightBrightChangedEvent(int from, int to) {
        super(Identifier.toIdentifier(LightComp.ID, ID), new Data(from, to));
    }

    public static class Data implements ThingDmData {

        private final int from;
        private final int to;

        public Data(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

    }

}
