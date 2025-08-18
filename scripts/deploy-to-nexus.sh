#!/bin/bash

# Deploy to Nexus/JFrog Artifactory Script
# Usage: ./deploy-to-nexus.sh [version] [repository-type]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
VERSION=${1:-"3.1.0"}
REPO_TYPE=${2:-"snapshot"} # snapshot or release

# Configuration
PROJECT_NAME="gqlex-path-selection-java"
GROUP_ID="com.intuit.gqlex"
ARTIFACT_ID="gqlex-path-selection-java"

# Maven settings file
MAVEN_SETTINGS="${HOME}/.m2/settings.xml"

echo -e "${BLUE}🚀 Starting deployment of ${PROJECT_NAME} v${VERSION} to ${REPO_TYPE} repository${NC}"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Check if Maven settings file exists
if [ ! -f "$MAVEN_SETTINGS" ]; then
    echo -e "${YELLOW}⚠️  Maven settings file not found at ${MAVEN_SETTINGS}${NC}"
    echo -e "${YELLOW}   Please ensure your Nexus/JFrog credentials are configured${NC}"
fi

# Clean and build the project
echo -e "${BLUE}🔨 Building project...${NC}"
mvn clean compile test-compile

# Run tests (excluding benchmarks for faster builds)
echo -e "${BLUE}🧪 Running tests...${NC}"
mvn test -Pfast

# Create source and javadoc JARs
echo -e "${BLUE}📦 Creating source and javadoc JARs...${NC}"
mvn source:jar javadoc:jar

# Package the project
echo -e "${BLUE}📦 Packaging project...${NC}"
mvn package -DskipTests

# Deploy to repository
echo -e "${BLUE}🚀 Deploying to ${REPO_TYPE} repository...${NC}"

if [ "$REPO_TYPE" = "snapshot" ]; then
    echo -e "${YELLOW}📤 Deploying SNAPSHOT version...${NC}"
    mvn deploy -DskipTests -Pdeploy-snapshot
elif [ "$REPO_TYPE" = "release" ]; then
    echo -e "${YELLOW}📤 Deploying RELEASE version...${NC}"
    mvn deploy -DskipTests -Pdeploy-release
else
    echo -e "${RED}❌ Invalid repository type: ${REPO_TYPE}. Use 'snapshot' or 'release'${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo -e "${GREEN}📦 Artifact: ${GROUP_ID}:${ARTIFACT_ID}:${VERSION}${NC}"
echo -e "${GREEN}🏠 Repository: ${REPO_TYPE}${NC}"

# Optional: Display deployed artifacts
echo -e "${BLUE}📋 Deployed artifacts:${NC}"
ls -la target/*.jar
