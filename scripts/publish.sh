#!/bin/bash
set -eu
: ${1:? Usage: $0 DESCRIPTION}
DESCRIPTION="$1"
set -x

# For some reason nexus-staging:release does not read the stagingRepositoryId
# from the properties file, so we must read it ourselves.
REPOSITORY_ID=`sed -n -r 's/stagingRepository\.id=(\w+)/\1/p' "staging/ff5044adfb72.properties"`

mvn nexus-staging:release \
    --errors \
    -DaltStagingDirectory=staging \
    -DstagingRepositoryId="$REPOSITORY_ID" \
    -DstagingDescription="$DESCRIPTION"

git push origin HEAD
git push origin --tags
