#!/bin/bash

# Run Code Coverage Script
# Usage: ./run-coverage.sh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Running Code Coverage Analysis...${NC}"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Clean and run tests with coverage
echo -e "${BLUE}🔨 Building project with coverage...${NC}"
mvn clean test jacoco:report

# Check if coverage report was generated
if [ -f "target/site/jacoco/index.html" ]; then
    echo -e "${GREEN}✅ Coverage report generated successfully!${NC}"
    
    # Extract coverage percentage
    COVERAGE=$(grep -o 'Total.*[0-9]\+\.[0-9]\+%' target/site/jacoco/index.html | grep -o '[0-9]\+\.[0-9]\+%' | head -1)
    
    if [ -n "$COVERAGE" ]; then
        echo -e "${GREEN}📊 Code Coverage: ${COVERAGE}${NC}"
        
        # Check if coverage meets thresholds
        COVERAGE_NUM=$(echo $COVERAGE | sed 's/%//')
        if (( $(echo "$COVERAGE_NUM >= 80" | bc -l) )); then
            echo -e "${GREEN}🎯 Coverage threshold (80%) met!${NC}"
        else
            echo -e "${YELLOW}⚠️  Coverage threshold (80%) not met. Current: ${COVERAGE}${NC}"
        fi
    fi
    
    echo -e "${BLUE}📁 Coverage report location: target/site/jacoco/index.html${NC}"
    echo -e "${BLUE}🌐 Open the HTML file in your browser to view detailed coverage${NC}"
    
    # Open coverage report if possible
    if command -v open &> /dev/null; then
        echo -e "${BLUE}🚀 Opening coverage report...${NC}"
        open target/site/jacoco/index.html
    elif command -v xdg-open &> /dev/null; then
        echo -e "${BLUE}🚀 Opening coverage report...${NC}"
        xdg-open target/site/jacoco/index.html
    fi
    
else
    echo -e "${RED}❌ Coverage report not generated${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Code coverage analysis completed!${NC}"
