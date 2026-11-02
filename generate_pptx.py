import sys
import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

def create_deck():
    prs = Presentation()
    # 16:9 Widescreen format
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)

    blank_layout = prs.slide_layouts[6]

    # Color palette
    COLOR_BG = RGBColor(11, 15, 25)
    COLOR_CARD = RGBColor(22, 30, 49)
    COLOR_BORDER = RGBColor(45, 60, 95)
    COLOR_WHITE = RGBColor(248, 250, 252)
    COLOR_MUTED = RGBColor(148, 163, 184)
    COLOR_CYAN = RGBColor(6, 182, 212)
    COLOR_BLUE = RGBColor(59, 130, 246)
    COLOR_EMERALD = RGBColor(16, 185, 129)
    COLOR_ROSE = RGBColor(244, 63, 94)
    COLOR_AMBER = RGBColor(245, 158, 11)

    def apply_background(slide):
        bg = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, Inches(13.333), Inches(7.5))
        bg.fill.solid()
        bg.fill.fore_color.rgb = COLOR_BG
        bg.line.fill.background()
        return bg

    def add_header(slide, tag_text, title_text):
        tx_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.5), Inches(11.7), Inches(1.2))
        tf = tx_box.text_frame
        tf.word_wrap = True
        
        p1 = tf.paragraphs[0]
        p1.text = tag_text.upper()
        p1.font.size = Pt(11)
        p1.font.bold = True
        p1.font.color.rgb = COLOR_CYAN
        p1.space_after = Pt(4)

        p2 = tf.add_paragraph()
        p2.text = title_text
        p2.font.size = Pt(26)
        p2.font.bold = True
        p2.font.color.rgb = COLOR_WHITE

    # ------------------ SLIDE 1: Title Slide ------------------
    s1 = prs.slides.add_slide(blank_layout)
    apply_background(s1)

    # Accent decorative box
    card = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.2), Inches(1.2), Inches(10.9), Inches(5.1))
    card.fill.solid()
    card.fill.fore_color.rgb = COLOR_CARD
    card.line.color.rgb = COLOR_BORDER

    tb = s1.shapes.add_textbox(Inches(1.8), Inches(1.6), Inches(9.7), Inches(4.3))
    tf = tb.text_frame
    tf.word_wrap = True

    p = tf.paragraphs[0]
    p.text = "CSE 2ND YEAR OBJECT-ORIENTED PROGRAMMING PROJECT"
    p.font.size = Pt(12)
    p.font.bold = True
    p.font.color.rgb = COLOR_CYAN
    p.space_after = Pt(12)

    p = tf.add_paragraph()
    p.text = "Smart Parking Lot Management System"
    p.font.size = Pt(36)
    p.font.bold = True
    p.font.color.rgb = COLOR_WHITE
    p.space_after = Pt(10)

    p = tf.add_paragraph()
    p.text = "Commercial 24-Bay Automated Solution Built with Core Java OOP"
    p.font.size = Pt(16)
    p.font.color.rgb = COLOR_MUTED
    p.space_after = Pt(36)

    p = tf.add_paragraph()
    p.text = "Presented By: [Candidate Name]  |  Roll No: [Your Roll No]  |  Branch: CSE"
    p.font.size = Pt(14)
    p.font.bold = True
    p.font.color.rgb = COLOR_BLUE
    p.space_after = Pt(8)

    p = tf.add_paragraph()
    p.text = "Technology: Core Java (JDK 8+), Collections, Native HttpServer, File I/O Persistence"
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_MUTED

    # ------------------ SLIDE 2: Problem Statement ------------------
    s2 = prs.slides.add_slide(blank_layout)
    apply_background(s2)
    add_header(s2, "Motivation & Background", "The Real-World Parking Challenge")

    # Left Box - Problem
    b1 = s2.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(1.8), Inches(5.6), Inches(4.8))
    b1.fill.solid()
    b1.fill.fore_color.rgb = COLOR_CARD
    b1.line.color.rgb = COLOR_ROSE
    tf1 = b1.text_frame
    tf1.word_wrap = True
    p = tf1.paragraphs[0]
    p.text = "❌ Conventional Manual Registers"
    p.font.size = Pt(18)
    p.font.bold = True
    p.font.color.rgb = COLOR_ROSE
    p.space_after = Pt(14)

    problems = [
        "• Manual Registers: Loss of physical slips, illegible logs, and no central tracking.",
        "• Fee Calculation Disputes: Cashier errors when rounding partial hours.",
        "• Zero Slot Visibility: Drivers roam aimlessly causing internal lot traffic.",
        "• Lack of EV & VIP Loyalty: No support for electric charging or discounted memberships."
    ]
    for prob in problems:
        p = tf1.add_paragraph()
        p.text = prob
        p.font.size = Pt(13)
        p.font.color.rgb = COLOR_WHITE
        p.space_after = Pt(10)

    # Right Box - Solution
    b2 = s2.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(6.8), Inches(1.8), Inches(5.7), Inches(4.8))
    b2.fill.solid()
    b2.fill.fore_color.rgb = COLOR_CARD
    b2.line.color.rgb = COLOR_EMERALD
    tf2 = b2.text_frame
    tf2.word_wrap = True
    p = tf2.paragraphs[0]
    p.text = "✅ Our Automated Software Solution"
    p.font.size = Pt(18)
    p.font.bold = True
    p.font.color.rgb = COLOR_EMERALD
    p.space_after = Pt(14)

    solutions = [
        "• Smart 24-Bay Allocation: Automatic assignment of first compatible empty bay.",
        "• Automated Chrono-Billing: Java Duration API calculates exact ceiling hour fee.",
        "• EV Fast Charging & VIP: Specialized bays with charging docks + 20% loyalty discounts.",
        "• Permanent CSV Auditing: All transactions stream to disk for daily revenue audit."
    ]
    for sol in solutions:
        p = tf2.add_paragraph()
        p.text = sol
        p.font.size = Pt(13)
        p.font.color.rgb = COLOR_WHITE
        p.space_after = Pt(10)

    # ------------------ SLIDE 3: 24 Specialized Bays ------------------
    s3 = prs.slides.add_slide(blank_layout)
    apply_background(s3)
    add_header(s3, "Commercial Layout & Capacities", "24 Specialized Parking Bays Structure")

    col_w = Inches(3.6)
    # Bay 1
    c1 = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(1.8), col_w, Inches(3.6))
    c1.fill.solid()
    c1.fill.fore_color.rgb = COLOR_CARD
    c1.line.color.rgb = COLOR_BORDER
    tf = c1.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = "🏍️ Bike Bays (Slots 1–6)"
    p.font.size = Pt(18)
    p.font.bold = True
    p.font.color.rgb = COLOR_CYAN
    p.space_after = Pt(10)
    p = tf.add_paragraph()
    p.text = "Rate: ₹10 / hour"
    p.font.size = Pt(22)
    p.font.bold = True
    p.font.color.rgb = COLOR_WHITE
    p.space_after = Pt(12)
    p = tf.add_paragraph()
    p.text = "Dedicated spaces for two-wheelers. Features intelligent overflow into car bays when full."
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_MUTED

    # Bay 2
    c2 = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(4.8), Inches(1.8), col_w, Inches(3.6))
    c2.fill.solid()
    c2.fill.fore_color.rgb = COLOR_CARD
    c2.line.color.rgb = COLOR_BORDER
    tf = c2.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = "🚗 Car Bays (Slots 7–18)"
    p.font.size = Pt(18)
    p.font.bold = True
    p.font.color.rgb = COLOR_BLUE
    p.space_after = Pt(10)
    p = tf.add_paragraph()
    p.text = "Rate: ₹20 / hour"
    p.font.size = Pt(22)
    p.font.bold = True
    p.font.color.rgb = COLOR_WHITE
    p.space_after = Pt(12)
    p = tf.add_paragraph()
    p.text = "Standard bays for sedans, SUVs, and commercial vehicles with positive partial-hour ceiling rules."
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_MUTED

    # Bay 3
    c3 = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(8.8), Inches(1.8), col_w, Inches(3.6))
    c3.fill.solid()
    c3.fill.fore_color.rgb = COLOR_CARD
    c3.line.color.rgb = COLOR_BORDER
    tf = c3.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = "⚡ EV Charging (19–24)"
    p.font.size = Pt(18)
    p.font.bold = True
    p.font.color.rgb = COLOR_EMERALD
    p.space_after = Pt(10)
    p = tf.add_paragraph()
    p.text = "Rate: ₹25/hr + ₹50"
    p.font.size = Pt(22)
    p.font.bold = True
    p.font.color.rgb = COLOR_WHITE
    p.space_after = Pt(12)
    p = tf.add_paragraph()
    p.text = "Premium charging bays equipped with high-speed EV charging docks for electric vehicles."
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_MUTED

    # Bottom Banner for VIP
    vip_banner = s3.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(5.7), Inches(11.6), Inches(1.0))
    vip_banner.fill.solid()
    vip_banner.fill.fore_color.rgb = COLOR_CARD
    vip_banner.line.color.rgb = COLOR_AMBER
    tf = vip_banner.text_frame
    p = tf.paragraphs[0]
    p.text = "⭐ VIP & Monthly Pass Holders: Enjoy a Flat 20% Discount automatically deducted at checkout!"
    p.font.size = Pt(14)
    p.font.bold = True
    p.font.color.rgb = COLOR_AMBER

    # ------------------ SLIDE 4: OOP Concepts ------------------
    s4 = prs.slides.add_slide(blank_layout)
    apply_background(s4)
    add_header(s4, "Object-Oriented Design", "Core OOP Concepts Demonstrated In Code")

    table_shape = s4.shapes.add_table(6, 3, Inches(0.8), Inches(1.8), Inches(11.7), Inches(4.8))
    table = table_shape.table
    table.columns[0].width = Inches(2.2)
    table.columns[1].width = Inches(2.8)
    table.columns[2].width = Inches(6.7)

    headers = ["OOP Concept", "Class / File", "Implementation in System"]
    for i, h in enumerate(headers):
        cell = table.cell(0, i)
        cell.text = h
        cell.fill.solid()
        cell.fill.fore_color.rgb = RGBColor(30, 41, 59)
        for p in cell.text_frame.paragraphs:
            p.font.bold = True
            p.font.color.rgb = COLOR_CYAN
            p.font.size = Pt(13)

    data = [
        ("Encapsulation", "Vehicle, ParkingSlot", "Private fields (vehicleNumber, entryTime, isOccupied) accessed solely via getters."),
        ("Inheritance", "Car, Bike, EV extends Vehicle", "Subclasses inherit common attributes and constructors from abstract Vehicle parent."),
        ("Abstraction", "Vehicle (Abstract), Billable", "Cannot instantiate raw Vehicle. Billable interface enforces billing contract."),
        ("Polymorphism", "calculateFee(), getHourlyRate()", "Dynamic method dispatch overrides rate (₹10, ₹20, ₹25) and adds EV dock fee at runtime."),
        ("Type-Safe Enums", "SlotType.java", "Enforces compile-time slot categorization (BIKE, CAR, EV_CHARGING) preventing bugs.")
    ]
    for row_idx, row in enumerate(data, start=1):
        for col_idx, text in enumerate(row):
            cell = table.cell(row_idx, col_idx)
            cell.text = text
            cell.fill.solid()
            cell.fill.fore_color.rgb = COLOR_CARD
            for p in cell.text_frame.paragraphs:
                p.font.size = Pt(12)
                p.font.color.rgb = COLOR_WHITE if col_idx == 0 else COLOR_MUTED

    # ------------------ SLIDE 5: Challenges Faced ------------------
    s5 = prs.slides.add_slide(blank_layout)
    apply_background(s5)
    add_header(s5, "Engineering & Problem Solving", "Challenges Faced & Technical Solutions")

    challenges = [
        ("1. Partial-Hour Round-up (Ceiling Rule) Precision",
         "Problem: Floating point division caused rounding bugs with small durations (e.g. 1 hr 2 mins).\nSolution: Implemented modulo arithmetic on Duration.getSeconds(): (remainder > 0) ? fullHours + 1 : fullHours with a strict 1-hour minimum charge."),
        ("2. Smart Bay Allocation & Category Compatibility",
         "Problem: Preventing large cars from taking small bike slots while allowing bikes to overflow if bike bays are full.\nSolution: Designed a Two-Pass Allocation Algorithm: Pass 1 matches preferred bay; Pass 2 permits controlled overflow."),
        ("3. Data Persistence Without External SQL Database",
         "Problem: Requirement mandated no heavy SQL database, but records had to survive server restarts.\nSolution: Built FileStorageManager using Java FileWriter in Append Mode (true) with thread-safe synchronized methods.")
    ]

    top = Inches(1.8)
    for title, desc in challenges:
        box = s5.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), top, Inches(11.7), Inches(1.5))
        box.fill.solid()
        box.fill.fore_color.rgb = COLOR_CARD
        box.line.color.rgb = COLOR_ROSE
        tf = box.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = title
        p.font.size = Pt(15)
        p.font.bold = True
        p.font.color.rgb = COLOR_ROSE
        p.space_after = Pt(4)
        p = tf.add_paragraph()
        p.text = desc
        p.font.size = Pt(12)
        p.font.color.rgb = COLOR_WHITE
        top += Inches(1.7)

    # ------------------ SLIDE 6: Advantages ------------------
    s6 = prs.slides.add_slide(blank_layout)
    apply_background(s6)
    add_header(s6, "Business & System Value", "Key Advantages of the System")

    advs = [
        ("⚡ 100% Computerized Accuracy", "Automated duration and billing calculations completely eliminate manual calculation disputes and human cashier bias."),
        ("📦 Zero External Dependencies", "Runs natively on standard Java JDK (HttpServer) without needing heavy frameworks like Spring Boot or local database installs."),
        ("📈 High Scalability", "Easily scaled from 10 to 24 commercial bays; the proportional algorithm scales cleanly to 100 or 500 bays without rewriting core logic."),
        ("💾 Permanent Audit Log (CSV)", "Every exit transaction is permanently archived. Attendants and owners can export CSV anytime for Microsoft Excel daily audit.")
    ]

    for i, (title, desc) in enumerate(advs):
        x = Inches(0.8 + (i % 2) * 6.0)
        y = Inches(1.8 + (i // 2) * 2.5)
        b = s6.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, x, y, Inches(5.7), Inches(2.2))
        b.fill.solid()
        b.fill.fore_color.rgb = COLOR_CARD
        b.line.color.rgb = COLOR_BLUE
        tf = b.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = title
        p.font.size = Pt(17)
        p.font.bold = True
        p.font.color.rgb = COLOR_CYAN
        p.space_after = Pt(8)
        p = tf.add_paragraph()
        p.text = desc
        p.font.size = Pt(13)
        p.font.color.rgb = COLOR_MUTED

    # ------------------ SLIDE 7: Future Scope ------------------
    s7 = prs.slides.add_slide(blank_layout)
    apply_background(s7)
    add_header(s7, "Innovation Roadmap", "Future Scope & Enhancements")

    futures = [
        ("📷 ANPR Camera Vision", "OpenCV AI module to automatically read registration plates at boom barrier gates."),
        ("📡 IoT Smart Sensors", "Ultrasonic sensors in each bay detecting real-time physical presence and actuating barriers."),
        ("📱 Driver Mobile App", "Cross-platform mobile app enabling drivers to check live availability and reserve parking in advance."),
        ("💳 FASTag & UPI Gateway", "Toll-style automated deduction via FASTag RFID tags or dynamic UPI QR codes on exit screens."),
        ("📈 Surge Pricing AI", "Dynamic pricing algorithms adjusting hourly rates based on weekend mall traffic and peak hours."),
        ("🏢 Multi-Storey Guidance", "Floor-by-floor LED displays guiding drivers to the nearest available bay in large complexes.")
    ]

    for i, (title, desc) in enumerate(futures):
        col = i % 3
        row = i // 3
        x = Inches(0.8 + col * 4.0)
        y = Inches(1.8 + row * 2.5)
        b = s7.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, x, y, Inches(3.7), Inches(2.2))
        b.fill.solid()
        b.fill.fore_color.rgb = COLOR_CARD
        b.line.color.rgb = COLOR_BORDER
        tf = b.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = title
        p.font.size = Pt(15)
        p.font.bold = True
        p.font.color.rgb = COLOR_EMERALD
        p.space_after = Pt(6)
        p = tf.add_paragraph()
        p.text = desc
        p.font.size = Pt(11)
        p.font.color.rgb = COLOR_MUTED

    # ------------------ SLIDE 8: Conclusion ------------------
    s8 = prs.slides.add_slide(blank_layout)
    apply_background(s8)

    card = s8.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.5), Inches(1.2), Inches(10.3), Inches(5.1))
    card.fill.solid()
    card.fill.fore_color.rgb = COLOR_CARD
    card.line.color.rgb = COLOR_BORDER

    tf = card.text_frame
    tf.word_wrap = True

    p = tf.paragraphs[0]
    p.text = "PROJECT SUMMARY & CONCLUSION"
    p.font.size = Pt(12)
    p.font.bold = True
    p.font.color.rgb = COLOR_CYAN
    p.alignment = PP_ALIGN.CENTER
    p.space_after = Pt(14)

    p = tf.add_paragraph()
    p.text = "Thank You For Your Time & Evaluation!"
    p.font.size = Pt(32)
    p.font.bold = True
    p.font.color.rgb = COLOR_WHITE
    p.alignment = PP_ALIGN.CENTER
    p.space_after = Pt(12)

    p = tf.add_paragraph()
    p.text = "The Smart Parking Lot System demonstrates how core Object-Oriented principles solve real-world urban transit and commercial billing challenges."
    p.font.size = Pt(14)
    p.font.color.rgb = COLOR_MUTED
    p.alignment = PP_ALIGN.CENTER
    p.space_after = Pt(28)

    p = tf.add_paragraph()
    p.text = "23 / 23 Automated Unit Tests Passed (100% Success)  |  Live Public Web Deployment Active"
    p.font.size = Pt(13)
    p.font.bold = True
    p.font.color.rgb = COLOR_EMERALD
    p.alignment = PP_ALIGN.CENTER
    p.space_after = Pt(10)

    p = tf.add_paragraph()
    p.text = "Questions & Technical Discussion Are Welcomed"
    p.font.size = Pt(14)
    p.font.bold = True
    p.font.color.rgb = COLOR_BLUE
    p.alignment = PP_ALIGN.CENTER

    output_path = "C:\\Users\\Akhil\\.gemini\\antigravity-ide\\scratch\\ParkingLotManagementSystem\\ParkingLot_Presentation.pptx"
    prs.save(output_path)
    print(f"Presentation saved successfully to: {output_path}")

if __name__ == "__main__":
    create_deck()
