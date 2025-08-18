#!/bin/bash

# Deploy to Maven Central Script
# Usage: ./deploy-to-maven-central.sh [version]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
VERSION=${1:-"3.1.0"}

# Configuration
PROJECT_NAME="gqlex-path-selection-java"
GROUP_ID="com.intuit.gqlex"
ARTIFACT_ID="gqlex-path-selection-java"

# Maven settings file
MAVEN_SETTINGS="${HOME}/.m2/settings.xml"

echo -e "${BLUE}🚀 Starting deployment of ${PROJECT_NAME} v${VERSION} to Maven Central${NC}"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Check if Maven settings file exists
if [ ! -f "$MAVEN_SETTINGS" ]; then
    echo -e "${YELLOW}⚠️  Maven settings file not found at ${MAVEN_SETTINGS}${NC}"
    echo -e "${YELLOW}   Please configure your Sonatype credentials first${NC}"
    echo -e "${YELLOW}   See MAVEN_CENTRAL_PUBLISHING_GUIDE.md for details${NC}"
    exit 1
fi

# Check if GPG is available
if ! command -v gpg &> /dev/null; then
    echo -e "${RED}❌ GPG is not installed or not in PATH${NC}"
    echo -e "${YELLOW}   GPG is required for signing artifacts for Maven Central${NC}"
    exit 1
fi

# Check if GPG keys are available
if ! gpg --list-keys &> /dev/null; then
    echo -e "${RED}❌ No GPG keys found${NC}"
    echo -e "${YELLOW}   Please generate and configure GPG keys first${NC}"
    exit 1
fi

echo -e "${GREEN}✅ GPG keys found${NC}"

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

# Deploy to Maven Central
echo -e "${BLUE}🚀 Deploying to Maven Central...${NC}"
echo -e "${YELLOW}📤 This will sign and upload your artifacts${NC}"
echo -e "${YELLOW}📤 Make sure you have Sonatype credentials configured${NC}"

# Confirm deployment
read -p "Are you sure you want to deploy to Maven Central? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}❌ Deployment cancelled${NC}"
    exit 1
fi

# Deploy
echo -e "${BLUE}🚀 Deploying...${NC}"
mvn deploy -DskipTests

echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo -e "${GREEN}📦 Artifact: ${GROUP_ID}:${ARTIFACT_ID}:${VERSION}${NC}"
echo -e "${GREEN}🏠 Repository: Maven Central${NC}"

# Display deployed artifacts
echo -e "${BLUE}📋 Deployed artifacts:${NC}"
ls -la target/*.jar

echo -e "${BLUE}📋 Deployed signatures:${NC}"
ls -la target/*.asc

echo -e "${GREEN}🎉 Your artifact is now being processed by Maven Central!${NC}"
echo -e "${YELLOW}⏳ It may take up to 10 minutes to appear in search results${NC}"
echo -e "${BLUE}🔍 Check: https://search.maven.org/artifact/${GROUP_ID}/${ARTIFACT_ID}${NC}"

# Optional: Display next steps
echo -e "${BLUE}📚 Next steps:${NC}"
echo -e "${BLUE}   1. Wait for Maven Central to process your artifact${NC}"
echo -e "${BLUE}   2. Verify publication at the URL above${NC}"
echo -e "${BLUE}   3. Update your README.md with the new version${NC}"
echo -e "${BLUE}   4. Create a GitHub release tag${NC}"

