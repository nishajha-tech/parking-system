FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy all project files
COPY . /app/

# Compile Java source files
RUN cd src && javac *.java

# Default port (Render will inject $PORT automatically)
ENV PORT=8095
EXPOSE 8095

# Run web server with src in classpath
CMD ["java", "-cp", "src", "ParkingWebServer"]
