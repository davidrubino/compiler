#!/bin/bash

(
    cd `dirname "$0"`
    java -jar lib/antlr-4.2-complete.jar Leac.g4 \
        -o src/trad/antlr -package trad.antlr -visitor -no-listener "$@"
)
