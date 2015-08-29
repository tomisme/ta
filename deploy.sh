#!/usr/bin/bash
echo "Cleaning up compile targets."
lein clean
lein cljsbuild once min
echo "Deploying with firebase."
firebase deploy
