#!/bin/bash

# Constants
API_URL="https://api.github.com/repos/bratek20/hla-tool/releases/latest"
JAR_NAME="hla-tool.jar"

# Fetch latest release data silently
latest_release=$(curl -s "$API_URL")

# Get the URL of the JAR file from the latest release
jar_url=$(echo $latest_release | grep -oP '"browser_download_url": "\K(.*?'"$JAR_NAME"')(?=")')

# Get the tag name (version) from the latest release
version=$(echo $latest_release | grep -oP '"tag_name": "\K(.*?)(?=")')

# Download the JAR file silently
curl -sL $jar_url -o tool.jar

# Save the version to toolVersion.txt
echo $version > toolVersion.txt

echo "Hla tool updated to the latest version ($version)!"
