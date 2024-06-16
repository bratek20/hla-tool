#!/bin/bash

wohHlaPath="/mnt/d/Rortos/hla/tool.jar"
wohPfPath="/mnt/d/Rortos/aircombatcs/hla/tool.jar"
wohCsPath="/mnt/d/Rortos/content-service/hla/tool.jar"
wohWmPath="/mnt/d/Rortos/ac-common/hla/tool.jar"

cp ../app/build/libs/app.jar $wohHlaPath
cp ../app/build/libs/app.jar $wohPfPath
cp ../app/build/libs/app.jar $wohCsPath
cp ../app/build/libs/app.jar $wohWmPath

