package io.github.athingx.athing.dm.qatest.puppet;

import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmComp;
import io.github.athingx.athing.dm.api.annotation.ThDmParam;
import io.github.athingx.athing.dm.api.annotation.ThDmService;

import java.util.concurrent.CompletableFuture;

@ThDmComp(id = "echo")
public interface EchoComp extends ThingDmComp {

    @ThDmService
    Echo syncEcho(@ThDmParam("echo") Echo echo);

    @ThDmService(isSync = false)
    CompletableFuture<Echo> asyncEcho(@ThDmParam("echo") Echo echo);

    @ThDmService(isSync = false)
    CompletableFuture<Echo> asyncEchoWithException(@ThDmParam("echo") Echo echo);

    record Echo(String words) implements ThingDmData {

    }
}
