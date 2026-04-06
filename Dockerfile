# Use Ubuntu with Maven from package manager
FROM ubuntu:20.04 AS build

# Prevent interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17 and Maven from package manager (more reliable)
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

# Verify installations
RUN java -version && mvn --version

# Set working directory
WORKDIR /app

# Copy and build
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

COPY backend/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/smart-college-management-1.0.0.jar app.jar

EXPOSE 8082
CMD ["java", "-jar", "app.jar", "--server.port=8082"]
