#!/bin/bash

projects[i++]="io.github.athingx.athing.dm:athing-dm-api"
projects[i++]="io.github.athingx.athing.dm:athing-dm-common"
projects[i++]="io.github.athingx.athing.dm:athing-dm-platform"
projects[i++]="io.github.athingx.athing.dm:athing-dm-thing"

mvn clean install \
  -f ../pom.xml \
  -pl "$(printf "%s," "${projects[@]}")" -am
