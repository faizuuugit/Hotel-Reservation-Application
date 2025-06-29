# Hotel Reservation System - Java CLI Application

![Java Version](https://img.shields.io/badge/Java-17%2B-blue)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This is a command-line hotel reservation system I built to manage room bookings, customer accounts, and administrative functions. It was my final project for the Java OOP course where I focused on proper architecture and clean implementation.
---
## What This Does

The application handles:
- ✅ Customer account creation with email validation
- 🛏️ Room availability search by date range
- 📅 Booking system with conflict prevention
- 🔍 Alternative date recommendations when rooms are booked
- 👨‍💼 Admin dashboard for managing rooms and viewing reservations
- 💰 Both paid and free room support

## Technical Highlights

- **OOP Architecture**: Clean separation of UI, services, and models
- **Collections**: `HashMap` for customers, `TreeSet` for rooms ensuring uniqueness
- **Singleton Services**: ReservationService and CustomerService
- **Date Handling**: `LocalDate` for reservation management
- **Input Validation**: Regex for emails, robust date parsing
- **Error Handling**: Graceful recovery from invalid inputs

## Key Design Choices
### Room Conflict Prevention
- Used nested maps with LocalDate ranges to ensure no double-booking

### Recommendation Engine
- If original dates are unavailable, suggests rooms 7 days later

### Email Validation
- Simple regex: ^(.+)@(.+).com$ - focused on basic validation

### Pricing Model
- FreeRoom subclass overriding pricing logic for "Free" display

## Challenges Overcome
### Date Logic: Handled overlapping reservations with isBefore()/isAfter() checks

### Singleton Threading: Made services thread-safe for potential web conversion

### Data Encapsulation: Used final properties with getters only for models

### UI Flow: Managed complex menu states with switch-case and loops

## Limitations & Next Steps
- Data persists only in-memory (would add file storage next)

- Basic email validation (would improve regex for real domains)

- Currently CLI only (could extend to Spring Boot web app)
