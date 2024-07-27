#!/bin/bash

# Fetch latest release data silently
LATEST_RELEASE=$(curl -s "https://api.github.com/repos/bratek20/hla-tool/releases/latest")

# Get the URL of the JAR file from the latest release
JAR_URL=$(echo $LATEST_RELEASE | grep -oP '"browser_download_url": "\K(.*?hla-tool.jar)(?=")')

# Get the tag name (version) from the latest release
VERSION=$(echo $LATEST_RELEASE | grep -oP '"tag_name": "\K(.*?)(?=")')

# Download the JAR file silently
curl -sL $JAR_URL -o tool.jar

echo "Hla tool updated to the latest version ($VERSION)!"
