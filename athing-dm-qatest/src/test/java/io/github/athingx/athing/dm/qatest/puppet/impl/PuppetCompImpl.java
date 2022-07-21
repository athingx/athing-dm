package io.github.athingx.athing.dm.qatest.puppet.impl;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.qatest.puppet.EchoComp;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import io.github.athingx.athing.dm.qatest.puppet.event.LightBrightChangedEventData;
import io.github.athingx.athing.dm.qatest.puppet.event.LightColorChangedEventData;
import io.github.athingx.athing.dm.qatest.puppet.event.LightStateChangedEventData;
import io.github.athingx.athing.dm.thing.ThingDm;

import java.util.concurrent.CompletableFuture;

public class PuppetCompImpl implements EchoComp, LightComp {

    private final ThingDm thingDm;

    private int bright = 100;
    private Color color = Color.RED;
    private State state = State.TURN_ON;

    public PuppetCompImpl(ThingDm thingDm) {
        this.thingDm = thingDm;
    }

    @Override
    public Echo syncEcho(Echo echo) {
        return echo;
    }

    @Override
    public CompletableFuture<Echo> asyncEcho(Echo echo) {
        return CompletableFuture.completedFuture(echo);
    }

    @Override
    public int getBright() {
        return bright;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void changeBright(int bright) {
        final int from = this.bright;
        this.bright = bright;
        thingDm.event(ThingDmEvent.event(
                Identifier.toIdentifier("light", "light_bright_changed_event"),
                new LightBrightChangedEventData(from, bright)
        ));
    }

    @Override
    public void changeColor(Color color) {
        final Color from = this.color;
        this.color = color;
        thingDm.event(ThingDmEvent.event(
                Identifier.toIdentifier("light", "light_color_changed_event"),
                new LightColorChangedEventData(from, color)
        ));
    }

    @Override
    public void turnOn() {
        this.state = State.TURN_OFF;
        thingDm.event(ThingDmEvent.event(
                Identifier.toIdentifier("light", "light_state_changed_event"),
                new LightStateChangedEventData(State.TURN_OFF)
        ));
    }

    @Override
    public void turnOff() {
        this.state = State.TURN_ON;
        thingDm.event(ThingDmEvent.event(
                Identifier.toIdentifier("light", "light_state_changed_event"),
                new LightStateChangedEventData(State.TURN_ON)
        ));
    }

}
