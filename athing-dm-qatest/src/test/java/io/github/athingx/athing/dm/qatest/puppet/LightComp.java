package io.github.athingx.athing.dm.qatest.puppet;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.annotation.*;
import io.github.athingx.athing.dm.qatest.puppet.event.LightBrightChangedEventData;
import io.github.athingx.athing.dm.qatest.puppet.event.LightColorChangedEventData;
import io.github.athingx.athing.dm.qatest.puppet.event.LightStateChangedEventData;

@ThDmComp(id = "light")
@ThDmEvent(id = "light_state_changed_event", type = LightStateChangedEventData.class)
@ThDmEvent(id = "light_color_changed_event", type = LightColorChangedEventData.class)
@ThDmEvent(id = "light_bright_changed_event", type = LightBrightChangedEventData.class)
public interface LightComp extends ThingDmComp {

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
