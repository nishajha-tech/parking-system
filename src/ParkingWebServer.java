import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ParkingWebServer
 * 
 * Embedded HTTP REST & Static Server for Parking Lot Management System.
 * Built using Java's native Standard Library (com.sun.net.httpserver.HttpServer).
 * Supports 24 Commercial Parking Bays (Bike Bays 1-6, Car Bays 7-18, EV Bays 19-24).
 */
public class ParkingWebServer {

    public static final int DEFAULT_PORT = 8095;
    private final int port;
    private final ParkingLot parkingLot;
    private final String webRoot;
    private HttpServer server;

    public ParkingWebServer(int port, String webRoot) {
        this.port = port;
        this.webRoot = webRoot;
        this.parkingLot = new ParkingLot(24); // Upgraded to 24 commercial bays
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/api/status", this::handleStatus);
        server.createContext("/api/park", this::handlePark);
        server.createContext("/api/exit", this::handleExit);
        server.createContext("/api/history/download", this::handleHistoryDownload);
        server.createContext("/download-zip", this::handleZipDownload);

        // Static Web Dashboard
        server.createContext("/", this::handleStatic);

        server.setExecutor(null);
        server.start();

        System.out.println("============================================================");
        System.out.println("  Smart Parking Lot Web Server started successfully!");
        System.out.println("  Capacity: " + parkingLot.getTotalCapacity() + " Commercial Bays");
        System.out.println("  Port: " + port);
        System.out.println("  Local URL: http://localhost:" + port);
        System.out.println("============================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // ================= API Handlers =================

    private void handleStatus(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
            return;
        }

        List<ParkingSlot> slots = parkingLot.getAllSlots();
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalCapacity\":").append(parkingLot.getTotalCapacity()).append(",");
        json.append("\"occupiedSlots\":").append(parkingLot.getOccupiedSlotCount()).append(",");
        json.append("\"availableSlots\":").append(parkingLot.getAvailableSlotCount()).append(",");
        json.append("\"totalFeesCollected\":").append(parkingLot.getTotalFeesCollected()).append(",");
        json.append("\"totalVehiclesServed\":").append(parkingLot.getTotalVehiclesServed()).append(",");
        json.append("\"carRevenue\":").append(parkingLot.getCarRevenue()).append(",");
        json.append("\"bikeRevenue\":").append(parkingLot.getBikeRevenue()).append(",");
        json.append("\"evRevenue\":").append(parkingLot.getEvRevenue()).append(",");
        json.append("\"slots\":[");

        for (int i = 0; i < slots.size(); i++) {
            ParkingSlot s = slots.get(i);
            json.append("{");
            json.append("\"slotId\":").append(s.getSlotId()).append(",");
            json.append("\"slotType\":\"").append(escapeJson(s.getSlotType().name())).append("\",");
            json.append("\"slotDisplayName\":\"").append(escapeJson(s.getSlotType().getDisplayName())).append("\",");
            json.append("\"occupied\":").append(s.isOccupied());

            if (s.isOccupied() && s.getParkedVehicle() != null) {
                Vehicle v = s.getParkedVehicle();
                json.append(",\"vehicleNumber\":\"").append(escapeJson(v.getVehicleNumber())).append("\"");
                json.append(",\"vehicleType\":\"").append(escapeJson(v.getVehicleType())).append("\"");
                json.append(",\"isVip\":").append(v.isVipMember());
                json.append(",\"entryTime\":\"").append(escapeJson(v.getFormattedEntryTime())).append("\"");
                json.append(",\"hourlyRate\":").append(v.getHourlyRate());

                boolean isCharging = (v instanceof EV) && ((EV) v).isChargingRequested();
                json.append(",\"chargingRequested\":").append(isCharging);
            } else {
                json.append(",\"vehicleNumber\":\"\"");
                json.append(",\"vehicleType\":\"\"");
                json.append(",\"isVip\":false");
                json.append(",\"entryTime\":\"\"");
                json.append(",\"hourlyRate\":0.0");
                json.append(",\"chargingRequested\":false");
            }
            json.append("}");
            if (i < slots.size() - 1) {
                json.append(",");
            }
        }
        json.append("]}");

        sendResponse(exchange, 200, json.toString(), "application/json");
    }

    private void handlePark(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
            return;
        }

        String body = readRequestBody(exchange);
        String type = extractJsonField(body, "type");
        String regNumber = extractJsonField(body, "regNumber");
        int pastHours = extractJsonInt(body, "pastHours", 0);
        int pastMins = extractJsonInt(body, "pastMins", 0);
        boolean isVip = extractJsonBoolean(body, "isVip");
        boolean chargingNeeded = extractJsonBoolean(body, "chargingNeeded");

        if (regNumber.isEmpty()) {
            sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Registration number is required\"}", "application/json");
            return;
        }

        LocalDateTime entryTime = LocalDateTime.now();
        if (pastHours > 0 || pastMins > 0) {
            entryTime = entryTime.minusHours(pastHours).minusMinutes(pastMins);
        }

        Vehicle vehicle;
        if ("Bike".equalsIgnoreCase(type)) {
            vehicle = new Bike(regNumber, entryTime, isVip);
        } else if ("EV".equalsIgnoreCase(type)) {
            vehicle = new EV(regNumber, chargingNeeded, entryTime, isVip);
        } else {
            vehicle = new Car(regNumber, entryTime, isVip);
        }

        try {
            ParkingSlot assigned = parkingLot.parkVehicle(vehicle);
            String resp = String.format(
                "{\"success\": true, \"slotId\": %d, \"regNumber\": \"%s\", \"vehicleType\": \"%s\", \"slotType\": \"%s\", \"isVip\": %s, \"entryTime\": \"%s\"}",
                assigned.getSlotId(),
                escapeJson(vehicle.getVehicleNumber()),
                escapeJson(vehicle.getVehicleType()),
                escapeJson(assigned.getSlotType().getDisplayName()),
                isVip,
                escapeJson(vehicle.getFormattedEntryTime())
            );
            sendResponse(exchange, 200, resp, "application/json");
        } catch (ParkingException e) {
            String resp = String.format("{\"success\": false, \"message\": \"%s\"}", escapeJson(e.getMessage()));
            sendResponse(exchange, 400, resp, "application/json");
        }
    }

    private void handleExit(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
            return;
        }

        String body = readRequestBody(exchange);
        String regNumber = extractJsonField(body, "regNumber");

        if (regNumber.isEmpty()) {
            sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Registration number is required\"}", "application/json");
            return;
        }

        try {
            ParkingReceipt r = parkingLot.processVehicleExit(regNumber);
            String resp = String.format(
                "{\"success\": true, \"receipt\": {" +
                "\"vehicleNumber\": \"%s\"," +
                "\"vehicleType\": \"%s\"," +
                "\"slotId\": %d," +
                "\"entryTime\": \"%s\"," +
                "\"exitTime\": \"%s\"," +
                "\"duration\": \"%s\"," +
                "\"chargeableHours\": %d," +
                "\"hourlyRate\": %.2f," +
                "\"baseFee\": %.2f," +
                "\"chargingFee\": %.2f," +
                "\"isVip\": %s," +
                "\"vipDiscount\": %.2f," +
                "\"totalFee\": %.2f" +
                "}}",
                escapeJson(r.getVehicleNumber()),
                escapeJson(r.getVehicleType()),
                r.getSlotId(),
                escapeJson(r.getEntryTime().format(Vehicle.FORMATTER)),
                escapeJson(r.getExitTime().format(Vehicle.FORMATTER)),
                escapeJson(r.getFormattedDuration()),
                r.getChargeableHours(),
                r.getHourlyRate(),
                r.getBaseFee(),
                r.getChargingFee(),
                r.isVip(),
                r.getVipDiscount(),
                r.getTotalFee()
            );
            sendResponse(exchange, 200, resp, "application/json");
        } catch (VehicleNotFoundException e) {
            String resp = String.format("{\"success\": false, \"message\": \"%s\"}", escapeJson(e.getMessage()));
            sendResponse(exchange, 404, resp, "application/json");
        }
    }

    private void handleHistoryDownload(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
            return;
        }

        String csvData = parkingLot.getFileStorageManager().getRawCsvContent();
        byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/csv");
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"parking_history.csv\"");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleZipDownload(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed", "text/plain");
            return;
        }

        Path zipPath = Paths.get("../ParkingLotManagementSystem.zip");
        if (!Files.exists(zipPath)) {
            zipPath = Paths.get("ParkingLotManagementSystem.zip");
        }
        if (!Files.exists(zipPath)) {
            zipPath = Paths.get("C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem.zip");
        }

        if (Files.exists(zipPath)) {
            byte[] bytes = Files.readAllBytes(zipPath);
            exchange.getResponseHeaders().set("Content-Type", "application/zip");
            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"ParkingLotManagementSystem.zip\"");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            sendResponse(exchange, 404, "ZIP file not found", "text/plain");
        }
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String pathStr = exchange.getRequestURI().getPath();
        if (pathStr.equals("/") || pathStr.isEmpty()) {
            pathStr = "/index.html";
        }

        Path filePath = Paths.get(webRoot, pathStr.substring(1));
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            byte[] bytes = Files.readAllBytes(filePath);
            String contentType = "text/html; charset=UTF-8";
            if (filePath.toString().endsWith(".css")) contentType = "text/css";
            else if (filePath.toString().endsWith(".js")) contentType = "application/javascript";
            else if (filePath.toString().endsWith(".png")) contentType = "image/png";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            sendResponse(exchange, 404, "404 Not Found", "text/plain");
        }
    }

    // ================= Helper Utilities =================

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText, String contentType)
            throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toString(StandardCharsets.UTF_8.name());
    }

    private String extractJsonField(String json, String field) {
        if (json == null || field == null) return "";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "[\"']?" + java.util.regex.Pattern.quote(field) + "[\"']?\\s*:\\s*[\"']?([^\"',}\\s]+)[\"']?",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim().toUpperCase();
        }
        return "";
    }

    private int extractJsonInt(String json, String field, int fallback) {
        if (json == null || field == null) return fallback;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "[\"']?" + java.util.regex.Pattern.quote(field) + "[\"']?\\s*:\\s*[\"']?(\\d+)[\"']?",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception ignored) {}
        }
        return fallback;
    }

    private boolean extractJsonBoolean(String json, String field) {
        if (json == null || field == null) return false;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "[\"']?" + java.util.regex.Pattern.quote(field) + "[\"']?\\s*:\\s*(true|false)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return false;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // ================= Main Entry Point =================

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String webRoot = "src/web";

        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (Exception ignored) {}
        }

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignored) {}
        }
        if (args.length >= 2) {
            webRoot = args[1];
        } else if (!new File(webRoot).exists()) {
            if (new File("web").exists()) {
                webRoot = "web";
            }
        }

        try {
            ParkingWebServer server = new ParkingWebServer(port, webRoot);
            server.start();

            // Pre-seed demo vehicles across Bike, Car, and EV bays
            // Slot 1: Bike
            server.parkingLot.parkVehicle(new Bike("DL02BI1001", LocalDateTime.now().minusMinutes(50), false));
            // Slot 2: Bike with VIP pass
            server.parkingLot.parkVehicle(new Bike("HR26BI2002", LocalDateTime.now().minusHours(1).minusMinutes(15), true));
            // Slot 7: Car with VIP pass
            server.parkingLot.parkVehicle(new Car("DL01CA3003", LocalDateTime.now().minusHours(1).minusMinutes(40), true));
            // Slot 8: Regular Car
            server.parkingLot.parkVehicle(new Car("MH02CA4004", LocalDateTime.now().minusHours(2).minusMinutes(25), false));
            // Slot 19: EV with high-speed charging dock
            server.parkingLot.parkVehicle(new EV("DL08EV5005", true, LocalDateTime.now().minusHours(2).minusMinutes(10), false));

            System.out.println("ℹ️  Pre-parked demo vehicles across 24 Commercial Bays (Bike, Car, and EV Charging).");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
