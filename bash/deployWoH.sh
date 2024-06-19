#!/bin/bash

# Define an array of paths
paths=(
    "/mnt/d/Rortos/hla/tool.jar"
    "/mnt/d/Rortos/aircombatcs/hla/tool.jar"
    "/mnt/d/Rortos/content-service/hla/tool.jar"
    "/mnt/d/Rortos/ac-common/hla/tool.jar"
    "/mnt/d/Rortos/gamelift-play-server/hla/tool.jar"
)

# Source path of the app.jar file
source_path="../app/build/libs/app.jar"

# Copy app.jar to each path in the array
for path in "${paths[@]}"; do
    cp "$source_path" "$path"
    echo "Copied $source_path to $path"
done
