#!/bin/bash

hlaFolderPath=../external/hla
profileName=utils
moduleName=$1

java -jar tool.jar update $hlaFolderPath $profileName $moduleName


