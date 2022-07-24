package io.github.athingx.athing.dm.qatest.puppet.impl;

import io.github.athingx.athing.dm.qatest.puppet.EchoComp;
import io.github.athingx.athing.dm.qatest.puppet.LightComp;
import io.github.athingx.athing.dm.qatest.puppet.event.LightBrightChangedEvent;
import io.github.athingx.athing.dm.qatest.puppet.event.LightColorChangedEvent;
import io.github.athingx.athing.dm.qatest.puppet.event.LightStateChangedEvent;
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
        thingDm.event(new LightBrightChangedEvent(from, bright));
    }

    @Override
    public void changeColor(Color color) {
        final Color from = this.color;
        this.color = color;
        thingDm.event(new LightColorChangedEvent(from, color));
    }

    @Override
    public void turnOn() {
        this.state = State.TURN_OFF;
        thingDm.event(new LightStateChangedEvent(state));
    }

    @Override
    public void turnOff() {
        this.state = State.TURN_ON;
        thingDm.event(new LightStateChangedEvent(state));
    }

}
