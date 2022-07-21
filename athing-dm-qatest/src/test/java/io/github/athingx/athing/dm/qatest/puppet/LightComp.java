package io.github.athingx.athing.dm.qatest.puppet;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.*;
import io.github.athingx.athing.dm.qatest.puppet.event.LightBrightChangedEvent;
import io.github.athingx.athing.dm.qatest.puppet.event.LightColorChangedEvent;
import io.github.athingx.athing.dm.qatest.puppet.event.LightStateChangedEvent;

import static io.github.athingx.athing.dm.qatest.puppet.LightComp.ID;

@ThDmComp(id = ID)
@ThDmEvent(id = LightStateChangedEvent.ID, type = LightStateChangedEvent.Data.class)
@ThDmEvent(id = LightColorChangedEvent.ID, type = LightColorChangedEvent.Data.class)
@ThDmEvent(id = LightBrightChangedEvent.ID, type = LightBrightChangedEvent.Data.class)
public interface LightComp extends ThingDmComp {

    String ID = "light";

    @ThDmProperty
    int getBright();

    @ThDmProperty
    State getState();

    @ThDmProperty
    Color getColor();

    void setColor(Color color);


    @ThDmService
    void changeBright(@ThDmParam("bright") int bright);

    @ThDmService
    void changeColor(@ThDmParam("color") Color color);

    @ThDmService
    void turnOn();

    @ThDmService
    void turnOff();

    enum State {
        TURN_ON,
        TURN_OFF
    }

    enum Color {
        RED,
        YELLOW,
        BLUE,
        PINK
    }

}
