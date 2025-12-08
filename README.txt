
                    GARDENMATE: COMMUNITY GARDEN RESERVATION SYSTEM
                              HOW TO RUN THE PROGRAM

NEEDED REQUIREMENTS
- Java Development Kit (JDK) 11 or higher installed
- Command-line terminal (Terminal on macOS, Command Prompt on Windows)

QUICK START (3 Steps)
1. Open a terminal
2. Navigate to the project folder:
   cd /path/to/OOP-project

3. Compile and run:
   javac *.java && java Main


DETAILED INSTRUCTIONS

STEP 1: NAVIGATE TO PROJECT DIRECTORY
Open your terminal and change to the project directory:

On macOS/Linux:
   cd /Users/yourusername/Desktop/OOP-project

On Windows:
   cd C:\Users\yourusername\Desktop\OOP-project


STEP 2: COMPILE THE JAVA FILES
Compile all Java files in the project:

   javac *.java

This will compile these files:
   - Crop.java
   - DateRange.java
   - Gardener.java
   - GardenPlot.java
   - GardenSystem.java
   - Reservation.java
   - ReservationStatus.java
   - Main.java

If successful, you'll see .class files created for each Java file.


STEP 3: RUN THE PROGRAM
Execute the main program:

   java Main



Once the program starts, you'll see the Welcome Menu:

┌───────────────────────────────────────────────────────┐
│                    WELCOME MENU                       │
├───────────────────────────────────────────────────────┤
│  1. Register as New Gardener                          │
│  2. Login (Existing Gardener)                         │
│  3. View All Plots                                    │
│  4. Exit                                              │
└───────────────────────────────────────────────────────┘

FOR NEW USERS:
1. Select option "1" to register
2. Enter your name (required)
3. Enter email (optional - press Enter to skip)
4. Enter phone (optional - press Enter to skip)
5. You'll be automatically logged in

FOR RETURNING USERS:
1. Select option "2" to login
2. View the list of registered gardeners
3. Enter your Gardener ID (e.g., G001)


MAIN MENU OPTIONS (after login)
1. VIEW AVAILABLE PLOTS
   - Check which plots are free for specific dates
   - Enter start and end dates in YYYY-MM-DD format

2. MAKE A RESERVATION
   - Select dates, choose a plot, optionally select crops
   - Maximum 3 active reservations per gardener

3. VIEW MY RESERVATIONS
   - See all your active and past reservations

4. CANCEL A RESERVATION
   - Cancel any of your active reservations

5. VIEW AVAILABLE CROPS
   - See all crops with growing information

6. VIEW ALL PLOTS
   - See details of all garden plots

7. VIEW MY PROFILE
   - See your account information

8. UPDATE MY PROFILE
   - Change your name, email, or phone number

9. LOGOUT
   - Return to the Welcome Menu

0. EXIT
   - Close the program


DATE FORMAT
All dates must be entered in YYYY-MM-DD format.
Examples:
   - 2025-06-01 (June 1, 2025)
   - 2025-12-25 (December 25, 2025)


AVAILABLE GARDEN PLOTS
The system comes with 5 pre-configured plots:

   P001 - Sunny Corner    (25 m²)  - North Section
   P002 - Shady Grove     (30 m²)  - East Section
   P003 - Herb Garden     (15 m²)  - South Section  *Herbs only*
   P004 - Vegetable Patch (40 m²)  - West Section
   P005 - Flower Bed      (20 m²)  - Central Area

Note: The Herb Garden (P003) only allows: basil, mint, rosemary,thyme, oregano, and parsley.


AVAILABLE CROPS
   - Tomatoes   (90 days min)  - Spring, Summer
   - Lettuce    (45 days min)  - Spring, Fall
   - Basil      (60 days min)  - Summer
   - Carrots    (70 days min)  - Spring, Fall
   - Peppers    (80 days min)  - Summer
   - Mint       (50 days min)  - Spring, Summer
   - Rosemary   (90 days min)  - Spring, Summer, Fall
   - Cucumbers  (60 days min)  - Summer
   - Spinach    (40 days min)  - Spring, Fall
   - Zucchini   (50 days min)  - Summer


TROUBLESHOOTING
Problem: "javac is not recognized"
Solution: Ensure Java JDK is installed and added to your PATH

Problem: "Error: Could not find or load main class Main"
Solution: Make sure you're in the correct directory and files are compiled

Problem: Compilation errors
Solution: Run "rm *.class" (macOS/Linux) or "del *.class" (Windows) 
          then recompile with "javac *.java"

Problem: Date parsing error
Solution: Use the format YYYY-MM-DD exactly (e.g., 2025-06-15)


CLEAN UP
To remove compiled class files:

On macOS/Linux:
   rm *.class

On Windows:
   del *.class
   
Thank you for using GardenMate!

