#!/bin/bash

for MODULE_PATH in $(find $PWD/target/dbvtk-?.?.?-RC/WEB-INF/classes/public/ -mindepth 1 -maxdepth 1 -type d); do
    MODULE_NAME=$(basename "$MODULE_PATH")
    mkdir -p "src/main/webapp/$MODULE_NAME/"
    find "$MODULE_PATH" -name '*.gwt.rpc' -exec cp -v {} "src/main/webapp/$MODULE_NAME/" \;
done