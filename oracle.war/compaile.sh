#!/bin/sh

jlib=/opt/jboss-as-7.1.1.Final/modules/javax/servlet/api/main/jboss-servlet-api_3.0_spec-1.0.0.Final.jar
javac -cp $jlib src/lw/app/*.java;\cp src/lw/app/*.class WEB-INF/classes/lw/app/

## restart jboss
