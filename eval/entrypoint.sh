#!/bin/bash

set -e


echo "Waiting for ${ACC_TEXT_URL}/health"

while [[ "$(curl --insecure -s -o /dev/null -w ''%{http_code}'' ${ACC_TEXT_URL}/health)" != "200" ]]; do sleep 1; done

exec "$@"
