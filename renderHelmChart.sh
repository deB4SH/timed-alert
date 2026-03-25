#!/bin/bash
for dir in ./src/test/helm/*     # list directories in the form "/tmp/dirname/"
do
    dir=${dir%*/}     
    echo "Templating next: ${dir##*/}"
    rm -rf ./src/test/helm/${dir##*/}/out
    helm template --debug --release-name timed-alert --output-dir ./src/test/helm/${dir##*/}/out ./src/helm    
done



