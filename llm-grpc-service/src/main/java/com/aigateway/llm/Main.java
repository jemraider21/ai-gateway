package com.aigateway.llm;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("LM_STUDIO_URL", "http://127.0.0.1:1234/v1");
        String model = System.getenv().getOrDefault("LM_STUDIO_MODEL", "google/gemma-4-12b-qat");
        LmStudioClient client = new LmStudioClient(baseUrl, model);

        System.out.println("AI Gateway Lab");
        System.out.println("Model: " + model);
        System.out.println("Type 'exit' to quit.");

        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                System.out.print("\nPrompt > ");
                if(!scanner.hasNextLine()) {
                    break;
                }

                String prompt = scanner.nextLine();
                if(prompt.isBlank()) {
                    continue;
                } else if(prompt.equalsIgnoreCase("exit")) {
                    break;
                }

                try {
                    long startTime = System.nanoTime();
                    String response = client.generateResponse(prompt);
                    long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;

                    System.out.println("\nLLM Response:");
                    System.out.println(response);
                    System.out.println("\nResponse time: " + elapsedTime + " ms");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Request interrupted");
                    break;
                } catch (IOException e) {
                    System.err.println("Request failed: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid request: " + e.getMessage());
                }
            }
        }

        System.out.println("Goodbye!");
    }
}
