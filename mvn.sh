#!/bin/sh

# This script define the build version and send it to mvn version
# This script can be used exactly like mvn command
#
# Example of versioning
# | tag   | dist | HEAD sha | dirty | version                        |
# | ----- | ---- | -------- | ----- | ------------------------------ |
# | 1.0.0 | 0    | -        | No    | 1.0.0                          |
# | 1.0.0 | 0    | 1234abcd | Yes   | 1.0.0+0-1234abcd+20140707-1030 |
# | 1.0.0 | 3    | 1234abcd | No    | 1.0.0+3-1234abcd               |
# | 1.0.0 | 3    | 1234abcd | Yes   | 1.0.0+3-1234abcd+20140707-1030 |


if [[ $# -eq 0 ]]; then
    echo "Not enough arguments"
    exit 1
fi

if [[ -n "$(git status --porcelain)" ]]; then
    dirty_ext=$(date +%Y%m%d-%H%M) # 20240706-1200
    version="$(git describe --long --tags --abbrev=8 --always)-$dirty_ext"
else
    version=$(git describe --tags --abbrev=8 --always)
fi

mvn -Drevision=$version $@
