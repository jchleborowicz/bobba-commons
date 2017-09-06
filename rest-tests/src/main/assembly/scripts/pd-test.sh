#!/bin/sh

set -e

LOCALCLASSPATH=

for i in ./lib/*.jar; do
    LOCALCLASSPATH=$i:$LOCALCLASSPATH
done

"$JAVA_HOME/bin/java" -cp $LOCALCLASSPATH org.bobba.tools.statest.utils.RestTestRunner $*
