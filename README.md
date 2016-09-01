# About Abacus

[![CircleCI](https://circleci.com/gh/cliixtech/abacus.svg?style=svg)](https://circleci.com/gh/cliixtech/abacus)

Abacus is a library to register metrics and publish them periodically to a external source,
with file caching and aiming to run on Android.

It stores all the metrics on a file-backed cache, and try to send them in a background thread on fixed intervals.

The cache layer is powered by [Tape](https://github.com/square/tape).

Currently, it supports publishing metrics to (InfluxDB)[https://influxdata.com/time-series-platform/influxdb/], but it's build with flexibility in mind, so adding new services should be very simple.

# Building

Run ``./gradlew check`` to check code formatting and run all the tests.

In case of any **format violations**, run ``./gradlew spotlessApply`` to format the code accordingly.

To avoid getting this violation messages, you can configure eclipse to use the same formatting
rules - just point your project's java formatter use yakko.eclipseformat.xml as formatting spec
file.

## Running tests continuously

To run all tests on every change you make, use ``./gradlew -t test``
