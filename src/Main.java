package src;

import java.io.File;

/**
 * Main is the primary entry point for the Password Strength Checker application.
 * By default, starts the zero-dependency embedded Java Web Server.
 * If '--console' or '-c' argument is passed, launches the interactive ConsoleApp.
 */
public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        if (args.length > 0 && ("--console".equalsIgnoreCase(args[0]) || "-c".equalsIgnoreCase(args[0]))) {
            ConsoleApp.main(args);
            return;
        }

        // Determine web folder location
        String webDir = "web";
        File dirCheck = new File(webDir);
        if (!dirCheck.exists()) {
            // Check parent or relative directory
            if (new File("../web").exists()) {
                webDir = "../web";
            }
        }

        int port = DEFAULT_PORT;
        WebServer server = null;

        // Try ports starting from 8080 up to 8090
        for (int p = DEFAULT_PORT; p <= DEFAULT_PORT + 10; p++) {
            try {
                server = new WebServer(p, webDir);
                server.start();
                port = p;
                break;
            } catch (Exception e) {
                System.out.println("[*] Port " + p + " is occupied, trying next port...");
            }
        }

        if (server == null) {
            System.err.println("[!] Failed to bind any port between 8080 and 8090. Exiting.");
            System.exit(1);
        }

        System.out.println("\n>>> Application successfully running at: http://localhost:" + port + "/ <<<");
        System.out.println(">>> Open this URL in any browser to use the Cybersecurity Web UI. <<<\n");
        System.out.println("Press Ctrl+C in terminal to stop server.\n");

        // Keep main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Shutting down server...");
            server.stop();
        }
    }
}
