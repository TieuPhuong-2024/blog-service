# Blog Service

A Spring Boot application for blog management with optimized Docker builds.

**Java Version**: 25
**Spring Boot**: 3.4.3

## Building and Running

### Optimized Docker Build (Recommended)

This project uses Google Jib for optimized Docker image creation with automatic layer caching.

```bash
# Build optimized Docker image with Jib
./gradlew jibDockerBuild

# Or build to a registry
./gradlew jib --image=your-registry/blog-service:latest
```

### Traditional Docker Build

If you prefer traditional Docker builds:

```bash
# Build the JAR
./gradlew bootJar

# Build Docker image
docker build -t blog-service .

# Run the application
docker run -p 8080:8080 blog-service
```

### Development

```bash
# Run with Docker Compose (includes database)
docker-compose up

# Run locally
./gradlew bootRun
```

## Performance Optimizations

- **Jib Plugin**: Automatic layer optimization for faster Docker builds
- **Gradle Daemon**: Enabled for faster incremental builds
- **Parallel Builds**: Multiple tasks run in parallel
- **Build Cache**: Caches build outputs between builds
- **JVM Memory**: Optimized heap settings for containers
