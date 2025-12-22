# GitHub Actions & Artifacts Guide

## Overview

This repository uses GitHub Actions to automatically build, test, and publish Maven artifacts to GitHub Packages. The artifacts are accessible by other repositories within the same GitHub organization.

## Workflows

### Build Workflow (`.github/workflows/build.yml`)

This workflow runs automatically on:
- **Push** to `main`, `master`, or `develop` branches
- **Pull Requests** to `main`, `master`, or `develop` branches
- **Manual trigger** via GitHub Actions UI (with optional GitHub Release creation)

#### What it does:

1. **Build Job**:
   - Checks out the code
   - Sets up JDK 8
   - Builds the project with Maven
   - Runs tests
   - Uploads JAR artifacts (available for 30 days)
   - Uploads test results

2. **Publish Job** (only on main/master push or manual trigger):
   - Publishes artifacts to GitHub Packages (Maven repository)
   - Optionally creates a GitHub Release with JAR files attached

## Version Management

**You manage versions manually in `pom.xml`** - the workflow simply builds and publishes whatever version is defined there.

### Releasing a New Version

1. **Update version in `pom.xml`**:
   - For releases: Change version to `1.0.1.X` (without `-SNAPSHOT`)
   - For snapshots: Keep version as `1.0.1.X-SNAPSHOT`

2. **Commit and push**:
   ```bash
   git add pom.xml
   git commit -m "Bump version to 1.0.1.X"
   git push
   ```

3. **Create a GitHub Release** (optional):
   - Go to **Actions** tab in GitHub
   - Select **Build and Publish** workflow
   - Click **Run workflow**
   - Check the **Create a GitHub Release** checkbox
   - Click **Run workflow**

   This will create a GitHub Release with the JAR files attached and a tag `v1.0.1.X`.

4. **Prepare next development version**:
   ```bash
   # Update pom.xml to next snapshot version
   # e.g., 1.0.1.10-SNAPSHOT
   git add pom.xml
   git commit -m "Prepare next development version 1.0.1.10-SNAPSHOT"
   git push
   ```

## What Gets Published

### Automatic Publishing (on every push to main/master):

- **Artifacts to GitHub Packages**: Whatever version is in `pom.xml` gets published
- **SNAPSHOT versions**: Continuously updated as you push changes
- **Release versions**: Published once when you push the release version

### Manual Release Creation:

- **GitHub Release**: Creates a release with tag `vX.Y.Z` and attaches JAR files
- **Release Notes**: Auto-generated from commits since last release

## Consuming Artifacts from Other Repositories

### For Maven Projects

To use this library in another Maven project within your organization:

1. **Add GitHub Packages repository to your `pom.xml`**:

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/YOUR_ORG/binance-java-api</url>
  </repository>
</repositories>
```

2. **Add the dependency**:

```xml
<dependency>
  <groupId>com.binance.api</groupId>
  <artifactId>binance-api-client</artifactId>
  <version>1.0.1.9</version>
</dependency>
```

3. **Configure authentication** in `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

### For GitHub Actions in Other Repositories

To access artifacts from another repository's GitHub Actions workflow:

```yaml
- name: Set up JDK 8
  uses: actions/setup-java@v4
  with:
    java-version: '8'
    distribution: 'temurin'
    cache: 'maven'
    server-id: github
    server-username: MAVEN_USERNAME
    server-password: MAVEN_PASSWORD

- name: Build with Maven
  env:
    MAVEN_USERNAME: ${{ github.actor }}
    MAVEN_PASSWORD: ${{ secrets.GITHUB_TOKEN }}
  run: mvn clean install
```

**Note**: The `GITHUB_TOKEN` automatically has read access to packages in the same organization.

### For Gradle Projects

Add to `build.gradle`:

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/YOUR_ORG/binance-java-api")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation 'com.binance.api:binance-api-client:1.0.1.9'
}
```

## Authentication

### Personal Access Token (PAT)

To download packages from GitHub Packages, you need a Personal Access Token with `read:packages` scope:

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with these scopes:
   - `read:packages` (to download packages)
   - `write:packages` (if you need to publish)
3. Use this token in your Maven `settings.xml` or as environment variable

### In GitHub Actions

GitHub Actions automatically provides `GITHUB_TOKEN` with appropriate permissions. No additional configuration needed for repositories in the same organization.

## Available Artifacts

After each build, the following artifacts are available:

1. **GitHub Packages** (Maven repository):
   - Permanent storage for all published versions
   - SNAPSHOT versions are continuously updated
   - Release versions are immutable
   - Accessible via Maven/Gradle dependency management

2. **GitHub Actions Artifacts** (temporary):
   - JAR files (30-day retention)
   - Test results (30-day retention)
   - Downloadable from Actions runs

3. **GitHub Releases** (optional):
   - Created manually via workflow dispatch
   - Contains JAR files as release assets
   - Permanent storage with release notes
   - Tagged with version number (e.g., `v1.0.1.9`)

## Workflow Comparison

### Old Approach (Over-complicated):
❌ Workflow tries to manage versions  
❌ Automatic git commits and tags  
❌ Tag conflicts and push failures  
❌ Complex version bumping logic  

### New Approach (Simple):
✅ You manage versions in `pom.xml` manually  
✅ Workflow just builds and publishes  
✅ No git manipulation (except optional GitHub Release)  
✅ Maven handles all artifact management  

## Best Practices

### Development Workflow:

1. **Work on SNAPSHOT versions**: `1.0.1.X-SNAPSHOT`
2. **Push frequently**: Each push publishes the SNAPSHOT to GitHub Packages
3. **Other developers get updates**: Pull latest SNAPSHOT version

### Release Workflow:

1. **Remove `-SNAPSHOT` from version** in `pom.xml`
2. **Commit and push**: Publishes release version
3. **Run workflow with "Create Release"**: Creates GitHub Release
4. **Bump to next SNAPSHOT**: Update `pom.xml` to `1.0.1.(X+1)-SNAPSHOT`

### Using SNAPSHOT vs Release:

- **SNAPSHOT**: For active development, constantly changing
- **Release**: Stable, immutable version for production use

## Troubleshooting

### Authentication Issues

If you get 401 Unauthorized:
- Verify your GitHub token has `read:packages` scope
- Ensure token hasn't expired
- Check repository visibility settings

### Package Not Found

- Verify the package version exists in GitHub Packages
- Check repository URL in your configuration
- Ensure you have access to the organization

### Build Failures

- Check GitHub Actions logs for detailed error messages
- Verify Java version compatibility
- Ensure all dependencies are accessible

### Tag Already Exists Error

This is no longer an issue! The simplified workflow doesn't create tags automatically. Only manual "Create Release" creates tags, and you can choose whether to create a release or not.
