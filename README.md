```text
      _____ _     _
  __ /__   \ |__ (_)_ __   __ _
 / _` |/ /\/ '_ \| | '_ \ / _` |
| (_| / /  | | | | | | | | (_| |
 \__,_\/   |_| |_|_|_| |_|\__, |
                          |___/

Just a Thing
```

## 框架使用

### 添加仓库

```xml
<!-- pom.xml增加仓库 -->
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/athingx/athing-dm</url>
    </repository>
</repositories>
```

### 构建客户端

```xml
<!-- pom.xml增加引用 -->
<dependency>
    <groupId>io.github.athingx.athing</groupId>
    <artifactId>athing-dm-thing</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```java
// 定义设备模型接口
@ThDmComp(id = ID)
public interface LightComp extends ThingDmComp {

    String ID = "light";

    @ThDmProperty
    int getBright();
    
    @ThDmService
    void changeBright(@ThDmParam("bright") int bright);
    
}

// 构建设备模型
final var thingDm = new ThingDmBuilder()
        .build(thing)
        .get();

// 注册服务
thingDm.load(new LightCompImpl());  
```

### 构建服务端

```xml
<!-- pom.xml增加引用 -->
<dependency>
    <groupId>io.github.athingx.athing</groupId>
    <artifactId>athing-dm-platform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```java
// 生成设备模型模板
final var thingDmTemplate = platform.genThingTemplate(ThingDmTemplate.class, PRODUCT_ID, THING_ID);

// 获取组件&并调用服务
final var lightComp = thingDmTemplate.getThingDmComp("light", LightComp.class);
lightComp.changeBright(100);
```
