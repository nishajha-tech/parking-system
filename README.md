# 🚗 ParkMaster Pro: Advanced Parking Lot Management System in Java (OOP)

A comprehensive, medium-to-advanced **Console & Real-Time Web Application** built in Java using core **Object-Oriented Programming (OOP) concepts**. Tailored specifically for Computer Science & Engineering (CSE) college projects, technical viva evaluations, and portfolio demonstrations.

---

## 🌟 1. What's New: Advanced Features Added

We extended the original system with high-impact, real-world features that significantly strengthen your OOP presentation:

1. **⚡ Electric Vehicle (EV) Support & Fast-Charging Dock:**
   - Introduced [`EV.java`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/src/EV.java) extending [`Vehicle.java`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/src/Vehicle.java) (₹25/hr base rate).
   - Optional high-speed EV Charging Dock service (+₹50 flat fee).
   - Demonstrates **Polymorphism & Method Overriding** (`calculateFee()` overrides the base calculation).

2. **🅿️ Dedicated Slot Categories ([`SlotType.java`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/src/SlotType.java) Enum):**
   - **Slots 1–3:** Dedicated **Bike Bays** (`SlotType.BIKE`).
   - **Slots 4–8:** Standard **Car Bays** (`SlotType.CAR`).
   - **Slots 9–10:** Premium **EV Charging Bays** (`SlotType.EV_CHARGING`).
   - Smart Slot Allocation logic matches vehicles to their ideal bay, with automatic fallback overflow.
   - Demonstrates **Java Enums & Domain Typing**.

3. **⭐ VIP / Monthly Pass Discount System:**
   - VIP/Monthly Pass holders receive an automatic **20% discount** on the final parking bill.
   - Detailed invoice shows base fee, add-ons, discount breakdown, and net total paid.

4. **💾 CSV File Handling & Transaction Auditing ([`FileStorageManager.java`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/src/FileStorageManager.java)):**
   - Automatically logs every completed exit transaction to `data/parking_history.csv`.
   - View history directly in the console menu (Option 6) or download via the web app (**Export CSV**).
   - Demonstrates **File I/O (`PrintWriter`, `BufferedReader`, `FileReader`)**.

5. **📊 Revenue Analytics by Vehicle Type:**
   - Real-time financial breakdown tracking revenue from **Cars**, **Bikes**, and **EVs** separately.

6. **🌐 Real-Time Interactive Web Dashboard & Public Link:**
   - Modern glassmorphic web dashboard with live capacity progress bar, filter pills, and receipt printer.
   - Powered natively by Java standard library `HttpServer` (zero external frameworks or dependencies).

---

## 🏗️ 2. File Structure

```text
ParkingLotManagementSystem/
├── data/
│   └── parking_history.csv         # Persistent transaction records logged automatically
├── src/
│   ├── Billable.java               # Interface defining fee calculation contract
│   ├── Vehicle.java                # Abstract base class (Encapsulation, Abstraction, VIP flag)
│   ├── Car.java                    # Subclass extending Vehicle (₹20/hr)
│   ├── Bike.java                   # Subclass extending Vehicle (₹10/hr)
│   ├── EV.java                     # Subclass extending Vehicle (₹25/hr + ₹50 charging dock)
│   ├── SlotType.java               # Enum for specialized bays (BIKE, CAR, EV_CHARGING)
│   ├── ParkingSlot.java            # Slot state management & compatibility validation
│   ├── ParkingLot.java             # Core business logic, smart allocation & collections
│   ├── ParkingReceipt.java         # Receipt model with VIP discount & charging breakdown
│   ├── FileStorageManager.java     # CSV file handling and persistent auditing
│   ├── ParkingException.java       # Custom domain exceptions hierarchy
│   ├── ParkingManager.java         # Enhanced Console Menu UI (7 options)
│   ├── Main.java                   # Console application entry point
│   ├── ParkingWebServer.java       # Native Java HTTP REST & Static Server (Port 8095)
│   ├── ParkingLotTest.java         # Automated verification suite (18 unit assertions)
│   └── web/
│       └── index.html              # Responsive, dark-mode real-time web dashboard
├── run-console.bat                 # 1-click batch launcher for Console Application
├── run-web.bat                     # 1-click batch launcher for Web Server & Browser
└── README.md                       # Complete documentation & viva guide
```

---

## 🧠 3. OOP Concepts Mapping Table

| OOP Concept | Class / File | Practical Implementation |
| :--- | :--- | :--- |
| **Encapsulation** | `Vehicle.java`, `ParkingSlot.java` | Private fields (`vehicleNumber`, `entryTime`, `isVip`, `occupied`) protected with public getters and business methods. |
| **Inheritance** | `Car.java`, `Bike.java`, `EV.java` | All three subclasses inherit `vehicleNumber`, `entryTime`, and `isVip` from the abstract `Vehicle` parent. |
| **Abstraction** | `Vehicle.java`, `Billable.java` | `Vehicle` cannot be instantiated directly; defines abstract `getHourlyRate()` implemented differently by each vehicle type. |
| **Polymorphism** | `EV.calculateFee()` vs `Vehicle.calculateFee()` | `EV` overrides `calculateFee()` to add the ₹50 dock fee. Calling `vehicle.getHourlyRate()` dynamically resolves to ₹10 (Bike), ₹20 (Car), or ₹25 (EV). |
| **Enums** | `SlotType.java` | Type-safe representation of slot categories (`BIKE`, `CAR`, `EV_CHARGING`) with display names and default rates. |
| **File I/O** | `FileStorageManager.java` | Demonstrates file persistence by streaming completed exit receipts to `data/parking_history.csv`. |
| **Exception Handling** | `ParkingException.java` | Custom exception hierarchy (`ParkingLotFullException`, `DuplicateVehicleException`, `VehicleNotFoundException`). |
| **Collections** | `ParkingLot.java` | Uses `ArrayList<ParkingSlot>` for dynamic tracking, iteration, and smart allocation. |

---

## 🚀 4. How to Run

### Method 1: Interactive Console Application
```bash
cd C:\Users\Akhil\.gemini\antigravity-ide\scratch\ParkingLotManagementSystem\src
javac *.java
java Main
```
*Or simply double-click [`run-console.bat`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/run-console.bat).*

### Method 2: Web Server Dashboard
```bash
cd C:\Users\Akhil\.gemini\antigravity-ide\scratch\ParkingLotManagementSystem\src
javac *.java
java -cp . ParkingWebServer 8095 web
```
*Or simply double-click [`run-web.bat`](file:///C:/Users/Akhil/.gemini/antigravity-ide/scratch/ParkingLotManagementSystem/run-web.bat).*
Then open: **http://localhost:8095**

### Method 3: Run the 18-Assertion Automated Test Suite
```bash
java ParkingLotTest
```

---

## 🌐 5. Live Public Web Link
The project is hosted live via a secure tunnel:
👉 **[https://filed-asia-occasions-seekers.trycloudflare.com](https://filed-asia-occasions-seekers.trycloudflare.com)**

---

## 🎯 6. Additional Viva Questions for the New Features

#### Q1. How did you implement Java Enums in this project?
**Answer:** We created `SlotType.java` with values `BIKE`, `CAR`, and `EV_CHARGING`. Each enum value holds attributes (`displayName`, `defaultRate`). This provides compile-time type safety instead of using plain strings or arbitrary integers.

#### Q2. How is Method Overriding demonstrated in `EV.java`?
**Answer:** `Vehicle` provides a default `calculateFee(chargeableHours)` returning `chargeableHours * getHourlyRate()`. `EV` overrides this method (`@Override`) to add the flat ₹50 charging dock fee if `isChargingRequested()` is true.

#### Q3. How does Smart Slot Allocation work?
**Answer:** `findBestAvailableSlot(vehicle)` performs two passes: First, it seeks an exact bay matching the vehicle (Bikes in Slots 1–3, Cars in Slots 4–8, EVs in Slots 9–10). If full, it checks if any compatible overflow bay is free.

#### Q4. How did you handle data persistence without an external database?
**Answer:** Using standard Java File I/O in `FileStorageManager.java`. Every exit transaction is appended to `data/parking_history.csv` using `PrintWriter` and `FileWriter`, enabling record auditing across server restarts.
