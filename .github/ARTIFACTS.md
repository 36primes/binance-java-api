# GitHub Actions & Artifacts Guide

## Overview

This repository uses GitHub Actions to automatically build, test, and publish Maven artifacts to GitHub Packages. The artifacts are accessible by other repositories within the same GitHub organization.

## Workflows

### Build Workflow (`.github/workflows/build.yml`)

This workflow runs automatically on:
- **Push** to `main`, `master`, or `develop` branches
- **Pull Requests** to `main`, `master`, or `develop` branches
- **Manual trigger** via GitHub Actions UI (with optional release creation)

#### What it does:

1. **Build Job**:
   - Checks out the code
   - Sets up JDK 8
   - Builds the project with Maven
   - Runs tests
   - Uploads JAR artifacts (available for 30 days)
   - Uploads test results

2. **Publish Job** (only on main/master push or manual release):
   - Publishes artifacts to GitHub Packages
   - Creates a GitHub Release (if triggered manually with release flag)
   - Automatically updates version numbers
   - Creates git tags

## Creating a Release

### Option 1: Manual Workflow Dispatch

1. Go to **Actions** tab in GitHub
2. Select **Build and Publish** workflow
3. Click **Run workflow**
4. Set parameters:
   - `release`: `true`
   - `release_version`: (optional) e.g., `1.0.1.8` - if not provided, removes `-SNAPSHOT` from current version
5. Click **Run workflow**

The workflow will:
- Update `pom.xml` to the release version
- Build and publish to GitHub Packages
- Create a GitHub Release with the JAR file
- Tag the release (e.g., `v1.0.1.8`)
- Automatically bump to next development version (e.g., `1.0.1.9-SNAPSHOT`)
- Push changes and tags back to the repository

### Option 2: Automatic Publishing (Snapshot)

When you push to `main` or `master` branch, the workflow automatically:
- Builds the project
- Publishes the SNAPSHOT version to GitHub Packages

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
  <version>1.0.1.8</version>
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
    implementation 'com.binance.api:binance-api-client:1.0.1.8'
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
   - Permanent storage for releases
   - SNAPSHOT versions
   - Accessible via Maven/Gradle dependency management

2. **GitHub Actions Artifacts** (temporary):
   - JAR files (30-day retention)
   - Test results (30-day retention)
   - Downloadable from Actions runs

3. **GitHub Releases**:
   - Created for tagged releases
   - Contains JAR files as release assets
   - Permanent storage with release notes

## Version Management

- **Development**: `X.Y.Z.N-SNAPSHOT` (auto-published on main branch push)
- **Release**: `X.Y.Z.N` (created via manual workflow dispatch)
- **Next Development**: Automatically incremented to `X.Y.Z.(N+1)-SNAPSHOT`

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

