OS-Integrated File Manager
Course: CS3502 - Operating Systems

Developer: [Giovanni Chatman]

Date: April 2026

1. Project Overview
This application is a JavaFX-based file management tool designed to demonstrate the interaction between high-level user applications and the Windows Operating System kernel. It focuses on the bridge between Java's NIO.2 API and low-level system calls, specifically highlighting atomic operations and kernel-level error handling.

2. Dependencies & Environment
To build and run this application, ensure the following are configured:

JDK: Version 21 or higher.

JavaFX SDK: Version 21 or higher.

OS: Optimized for Windows 11 (NTFS File System).

IDE: Compatible with Eclipse, IntelliJ IDEA, or VS Code.

3. Build and Run Instructions
Import Project: Import the source folder as a Java Project into your preferred IDE.

Configure JavaFX: * Add the JavaFX JARs to the Project Build Path.

Add the following VM Arguments (adjust path as necessary):
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml

Execution: Run Main.java.

Initial Path: The application is configured to scan a folder named TestFolder on the Desktop. Please ensure a folder exists at that location or modify the rootPath variable in Main.java.

4. Technical Features Demonstrated
System Call Mapping: Leverages java.nio.file to trigger kernel-mode operations (CreateFile, DeleteFile, MoveFileEx).

Atomic Integrity: Uses StandardCopyOption.ATOMIC_MOVE to ensure rename operations are indivisible transactions.

Metadata Processing: Retrieves file attributes (size, permissions, timestamps) directly from the OS Master File Table (MFT).

Exception Handling: Robustly catches and reports OS-level rejections, such as AccessDeniedException.

