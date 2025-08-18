# 🚀 Maven Central Publishing Guide

This guide will walk you through the complete process of publishing your `gqlex-path-selection-java` artifact to Maven Central.

## 📋 Prerequisites

Before you can publish to Maven Central, you need to complete these steps:

### 1. **Sonatype Account Setup**
- Go to [https://issues.sonatype.org/](https://issues.sonatype.org/)
- Create an account if you don't have one
- Create a new issue to request access to publish to Maven Central
- Provide your groupId: `com.intuit.gqlex`
- Wait for approval (usually 1-2 business days)

### 2. **GPG Key Setup**
You need a GPG key to sign your artifacts:

```bash
# Generate a new GPG key
gpg --gen-key

# List your keys
gpg --list-keys

# Export your public key
gpg --armor --export your-email@example.com > public-key.asc

# Export your private key (for CI/CD)
gpg --armor --export-secret-key your-email@example.com > private-key.asc
```

### 3. **Maven Settings Configuration**
Create or update your `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <!-- Sonatype Central Publishing -->
        <server>
            <id>central</id>
            <username>YOUR_SONATYPE_USERNAME</username>
            <password>YOUR_SONATYPE_PASSWORD</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>gpg</activeProfile>
    </activeProfiles>
</settings>
```

## 🔧 Project Configuration

Your `pom.xml` is already properly configured with:

✅ **Sonatype Central Publishing Plugin**  
✅ **Source JAR Plugin**  
✅ **Javadoc JAR Plugin**  
✅ **GPG Signing Plugin**  
✅ **Proper Project Metadata**  
✅ **Apache 2.0 License**  
✅ **SCM Information**  

## 🚀 Publishing Process

### Step 1: Prepare Your Release

```bash
# Clean and build
mvn clean compile test

# Run tests to ensure everything works
mvn test

# Create source and javadoc JARs
mvn source:jar javadoc:jar

# Package the project
mvn package
```

### Step 2: Sign and Deploy

```bash
# Deploy to Maven Central (this will sign and upload)
mvn deploy
```

### Step 3: Verify Deployment

After successful deployment, your artifact will be available at:
- **Maven Central**: https://search.maven.org/artifact/com.intuit.gqlex/gqlex-path-selection-java
- **Direct Download**: https://repo1.maven.org/maven2/com/intuit/gqlex/gqlex-path-selection-java/

## 📦 What Gets Published

Your deployment will include:
1. **Main JAR**: `gqlex-path-selection-java-3.1.0.jar`
2. **Source JAR**: `gqlex-path-selection-java-3.1.0-sources.jar`
3. **Javadoc JAR**: `gqlex-path-selection-java-3.1.0-javadoc.jar`
4. **POM**: `gqlex-path-selection-java-3.1.0.pom`
5. **GPG Signatures**: `.asc` files for each artifact

## 🔐 Security Requirements

Maven Central requires:
- **GPG Signing**: All artifacts must be digitally signed
- **Source JARs**: Must include source code
- **Javadoc JARs**: Must include API documentation
- **Valid POM**: Must contain all required metadata

## 🚨 Common Issues & Solutions

### Issue: GPG Signing Fails
```bash
# Check if GPG is available
gpg --version

# Verify your key is available
gpg --list-keys

# Test signing manually
echo "test" | gpg --clearsign
```

### Issue: Authentication Fails
- Verify your Sonatype credentials in `settings.xml`
- Ensure you have publishing permissions for your groupId
- Check that your account is approved for Maven Central

### Issue: Validation Fails
- Ensure all required fields are present in `pom.xml`
- Verify license and SCM information is correct
- Check that your artifact doesn't contain any proprietary code

## 📱 CI/CD Integration

For automated publishing, you can use GitHub Actions. Your project already has a workflow file at `.github/workflows/maven-publish.yml`.

### Environment Variables Needed:
```bash
SONATYPE_USERNAME=your_username
SONATYPE_PASSWORD=your_password
GPG_PRIVATE_KEY=your_private_key
GPG_PASSPHRASE=your_passphrase
```

## 🎯 Next Steps

1. **Complete Sonatype Account Setup**
2. **Generate and Configure GPG Keys**
3. **Update Maven Settings**
4. **Test Local Deployment**
5. **Deploy to Maven Central**
6. **Verify Publication**

## 📚 Additional Resources

- [Sonatype Central Documentation](https://central.sonatype.org/)
- [Maven Central Requirements](https://central.sonatype.org/pages/requirements.html)
- [GPG Key Management](https://central.sonatype.org/pages/working-with-pgp-signatures.html)

## 🆘 Getting Help

If you encounter issues:
1. Check the [Sonatype Central FAQ](https://central.sonatype.org/pages/faq.html)
2. Review the [Maven Central Requirements](https://central.sonatype.org/pages/requirements.html)
3. Create an issue in the [Sonatype JIRA](https://issues.sonatype.org/)

---

**Good luck with your Maven Central publication! 🚀**

