#!/bin/bash

(
    cd `dirname "$0"`

    java -cp lib/antlr-4.2-complete.jar:bin \
        org.antlr.v4.runtime.misc.TestRig trad.antlr.Leac "$@"
)
