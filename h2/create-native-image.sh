#!/bin/bash

wget https://github.com/h2database/h2database/releases/download/version-2.2.224/h2-2023-09-17.zip

unzip h2-2023-09-17.zip

/opt/graalvm-jdk-21.0.2+13.1/bin/native-image --no-fallback -cp h2-2.2.224.jar -H:Name=application -H:Class=org.h2.tools.Server --verbose  --allow-incomplete-classpath -H:ReflectionConfigurationFiles=reflectconfig.json -H:IncludeResources=".*/data.zip$"