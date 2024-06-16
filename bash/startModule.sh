#!/bin/bash

hlaFolderPath=../hla
profileName=hla
moduleName=$1

java -jar tool.jar start $hlaFolderPath $profileName $moduleName

