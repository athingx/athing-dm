# athing-dm

设备物模型

## 模块简介

通过API和注解简化了物模型的定义和操作，避免了繁琐的阿里云平台配置和编码时JSON解析工作。

- **关键注解**

  |注解|解释|
  |---|---|
  |@ThDmComp|物模型组件|
  |@ThDmEvent|物模型事件|
  |@ThDmProperty|物模型属性|
  |@ThDmService|物模型服务|

- **物模型定义注解示例**

  ```java
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
  ```