#!/bin/bash
MIN_COVERAGE=80
COVERAGE_FILE="app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"

ACTUAL_COVERAGE=$(grep -oPm1 '(?<=<counter type="INSTRUCTION" missed=")\d+' "$COVERAGE_FILE" | 
                  awk '{print ($2/($1+$2))*100}')

if (( $(echo "$ACTUAL_COVERAGE < $MIN_COVERAGE" | bc -l) )); then
  echo "❌ 覆盖率不足 ${ACTUAL_COVERAGE}% < ${MIN_COVERAGE}%"
  exit 1
else
  echo "✅ 覆盖率达标 ${ACTUAL_COVERAGE}% ≥ ${MIN_COVERAGE}%"
fi