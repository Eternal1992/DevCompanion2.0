#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to locate JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
  if [ -x /usr/libexec/java_home ]; then
    export JAVA_HOME="$(/usr/libexec/java_home)"
  fi
fi

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# Attempt to set MAX_FD (the maximum number of file descriptors)
if [ "$cygwin" = "false" ]; then
    MAX_FD_LIMIT=$(ulimit -H -n)
    if [ $? -eq 0 ]; then
        if [ "$MAX_FD_LIMIT" != "unlimited" ]; then
            ulimit -n "$MAX_FD_LIMIT"
            if [ $? -ne 0 ]; then
                warn "Could not set maximum file descriptor limit: $MAX_FD_LIMIT"
            fi
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVA_HOME/bin/java" $DEFAULT_JVM_OPTS \
    -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"