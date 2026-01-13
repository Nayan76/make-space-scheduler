# Make Space Scheduler

A Java Swing application for managing meeting room bookings at Make Space Ltd.

## Features
- Book meeting rooms based on capacity requirements
- Check room availability
- Three meeting rooms with different capacities:
  - C-Cave (3 people)
  - D-Tower (7 people) 
  - G-Mansion (20 people)
- Buffer time enforcement
- User-friendly GUI interface

## Prerequisites
- Java 8 or higher
- Git (for cloning)

## How to Run

### Method 1: Using Terminal
```bash
# Clone the repository
git clone https://github.com/your-username/make-space-scheduler.git
cd make-space-scheduler

# Compile the project
javac -d . src/com/makespace/*.java

# Run the application
java com.makespace.MakeSpaceGUI