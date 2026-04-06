# Use official Ubuntu and install everything manually
FROM ubuntu:20.04 AS build

# Prevent interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17 and Maven
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk wget curl unzip && \
    wget -q https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -O /tmp/maven.tar.gz && \
    tar -xzf /tmp/maven.tar.gz -C /opt && \
    mv /opt/apache-maven-3.8.6 /opt/maven && \
    rm /tmp/maven.tar.gz && \
    chmod +x /opt/maven/bin/* || true && \
    ln -sf /opt/maven/bin/mvn /usr/local/bin/mvn

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV MAVEN_HOME=/opt/maven
ENV PATH=$PATH:$MAVEN_HOME/bin:/usr/local/bin

# Verify Maven installation
RUN which mvn && mvn --version

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
RUN apt-get update && apt-get install -y openjdk-17-jdk curl && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
WORKDIR /app

COPY --from=build /app/target/smart-college-management-1.0.0.jar app.jar

EXPOSE 8082
CMD ["java", "-jar", "app.jar", "--server.port=8082"]
