import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.HttpClientErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    // main entry point
    public static void main(String[] args) {
        loopAndReadInputForManualTests();
    }

    // loops and reads input for a country code
    private static void loopAndReadInputForManualTests() {
        String countryName = "";

        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Begin manual testing loop for findCapitalByPartialName");

        do {
            System.out.print("Enter an abbreviated country name to lookup (ENTER to quit): ");

            try {
                countryName = buffer.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!countryName.isEmpty()) {
                try {
                    String countryCapital = CapitalLookupService.getCapitalLookup().findCapitalByPartialName(countryName);
                    System.out.printf("The capital of %s is %s%n", countryName, countryCapital);
                } catch (JsonProcessingException | HttpClientErrorException ex) {
                    System.out.printf("No capital found for country: %s%n", countryName);
                }
            }
        } while (!countryName.isEmpty());

        System.out.println("Finished with manual testing loop.");
    }
}
