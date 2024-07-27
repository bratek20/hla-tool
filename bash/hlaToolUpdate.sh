#!/bin/bash

URL="https://github.com/bratek20/hla-tool/releases/download/v1.0.0/hla-tool-1.0.0.jar"
echo "Url = ${URL}"

# Download the jar file
curl -L -o "tool.jar" "${URL}" --verbose

echo "Hla tool update done!"
