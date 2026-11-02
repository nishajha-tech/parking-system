FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy source and web assets
COPY src/ /app/

# Compile Java code
RUN javac *.java

# Default port
ENV PORT=8095
EXPOSE 8095

# Run the embedded web server
CMD ["java", "-cp", ".", "ParkingWebServer"]
