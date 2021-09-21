#!/usr/bin/env bash
base_dir=`dirname $0`
${base_dir}/ga-stop.sh
${base_dir}/ga-start.sh -daemon