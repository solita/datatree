#!/bin/bash
set -eu
: ${1:? Usage: $0 DESCRIPTION}
DESCRIPTION="$1"
set -x

mvn nexus-staging:release \
    --errors \
    -DaltStagingDirectory=staging \
    -DstagingDescription="$DESCRIPTION"

git push origin HEAD
git push origin --tags
