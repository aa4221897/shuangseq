#!/bin/bash
COVERAGE_THRESHOLD=80
COVERAGE_FILE="app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"

if [ ! -f "$COVERAGE_FILE" ]; then
    ./gradlew jacocoTestReport
fi

COVERAGE=$(xmllint --xpath 'string(//report/counter[@type="INSTRUCTION"]/@missed)' $COVERAGE_FILE)
TOTAL=$(xmllint --xpath 'string(//report/counter[@type="INSTRUCTION"]/@covered)' $COVERAGE_FILE)
COVERAGE_RATE=$(( 100 * $TOTAL / ($COVERAGE + $TOTAL) ))

if [ $COVERAGE_RATE -lt $COVERAGE_THRESHOLD ]; then
    echo "代码覆盖率不足$COVERAGE_THRESHOLD%，当前为$COVERAGE_RATE%"
    exit 1
else
    echo "代码覆盖率达标：$COVERAGE_RATE%"
fi