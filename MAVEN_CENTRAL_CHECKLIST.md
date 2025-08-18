# ✅ Maven Central Publishing Checklist

Use this checklist to ensure you've completed all necessary steps before publishing to Maven Central.

## 🔐 Account Setup
- [ ] **Sonatype Account**: Created account at [https://issues.sonatype.org/](https://issues.sonatype.org/)
- [ ] **GroupId Request**: Submitted issue requesting access for `com.intuit.gqlex`
- [ ] **Approval Received**: Got approval from Sonatype (1-2 business days)

## 🔑 GPG Key Setup
- [ ] **GPG Installed**: `gpg --version` works
- [ ] **Key Generated**: `gpg --gen-key` completed
- [ ] **Key Listed**: `gpg --list-keys` shows your key
- [ ] **Public Key Exported**: `gpg --armor --export your-email@example.com > public-key.asc`
- [ ] **Private Key Exported**: `gpg --armor --export-secret-key your-email@example.com > private-key.asc`

## ⚙️ Maven Configuration
- [ ] **Settings File**: `~/.m2/settings.xml` created/updated
- [ ] **Sonatype Credentials**: Username and password configured
- [ ] **GPG Profile**: GPG profile activated in settings
- [ ] **GPG Passphrase**: Passphrase configured (if using)

## 🏗️ Project Configuration
- [ ] **POM Validated**: `mvn validate` passes
- [ ] **Tests Pass**: `mvn test` passes
- [ ] **Build Works**: `mvn clean package` succeeds
- [ ] **Source JARs**: `mvn source:jar` works
- [ ] **Javadoc JARs**: `mvn javadoc:jar` works

## 🚀 Deployment
- [ ] **Local Test**: `mvn deploy` works locally
- [ ] **Artifacts Created**: All JARs, sources, javadoc, and signatures generated
- [ ] **Maven Central**: Artifact appears in Maven Central search
- [ ] **Download Test**: Artifact can be downloaded from Maven Central

## 📋 Post-Publication
- [ ] **README Updated**: Version information updated
- [ ] **GitHub Release**: Release tag created
- [ ] **Documentation**: Any version-specific docs updated
- [ ] **Dependencies**: Other projects can now use your artifact

---

## 🚨 Quick Commands

```bash
# Check GPG keys
gpg --list-keys

# Validate POM
mvn validate

# Run tests
mvn test

# Build project
mvn clean package

# Create source and javadoc JARs
mvn source:jar javadoc:jar

# Deploy to Maven Central
mvn deploy

# Check deployment script
./scripts/deploy-to-maven-central.sh
```

## 📚 Resources
- **Guide**: [MAVEN_CENTRAL_PUBLISHING_GUIDE.md](MAVEN_CENTRAL_PUBLISHING_GUIDE.md)
- **Script**: [scripts/deploy-to-maven-central.sh](scripts/deploy-to-maven-central.sh)
- **Sonatype**: [https://central.sonatype.org/](https://central.sonatype.org/)
- **Maven Central**: [https://search.maven.org/](https://search.maven.org/)

---

**Check each box as you complete the step! ✅**

