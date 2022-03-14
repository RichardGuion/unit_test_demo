import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

class CapitalLookupServiceTests {

    @ParameterizedTest
    @DisplayName("Positive test cases for findCapitalByPartialName")
    // CSV Source is very useful - can even have this in an external file, to make it easier to modify test data sets
    // I tried to do mixed case, lower case, upper case, leading/trailing blanks for input
    @CsvSource({"Peru,Lima", " Peru,Lima", "Peru ,Lima", "ITALY,Rome", "ukr,Kyiv", "usa,'Washington, D.C.'",
            "can,Ottawa", "arg,'Buenos Aires'", "uk,London", " JP ,Tokyo"})
    // Bugs Found:
    // UK -> returns Pyongyang instead of London; another time it returned Kyiv
    // AR -> doesn't return Buenos Aires for Argentina, seems like a bug
    // Can -> returns Vatican City, I see a bug filed here: https://gitlab.com/amatos/rest-countries/-/issues/108
    void findCapitalByPartialName(String countryName, String expectedResult) {
        TestLogger.getTestLogger().logTestOperation(String.format("findCapitalByName(%s)", countryName));
        try {
            assertEquals(expectedResult, CapitalLookupService.getCapitalLookup().findCapitalByPartialName(countryName));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(String.format("Error while calling %s", CapitalLookupService.getCapitalLookup().getUrlForPartialName(countryName)));
        }
    }

    @ParameterizedTest
    @DisplayName("Negative test cases for findCapitalByPartialName, invalid country name strings")
    // CSV Source is very useful - can even have this in an external file, to make it easier to modify test data sets
    @CsvSource({"england", "scotland", "wakanda", "11", "*usa*", "ASuperLongStringAsLongAsPossible11111111111111111111111",
            "AString/with/bad/formatting\\*?fulltext=true", "wakanda?fulltext=true", "United States?fulltext=false"})
    // Bugs Found:
    // The last case fails, where I set fullText = false, which just appends this onto the short code API
    // I tried this manually: https://restcountries.com/v3.1/name/United States?fulltext=false
    // It still returns the Json data set; not a terrible problem but I would think fullText=false should revert to
    // the short country code.
    // The API does have a lot of inconsistencies.
    void findCapitalPartialNameHttpExceptions(String countryName) {
       TestLogger.getTestLogger().logTestOperation(String.format("findCapitalPartialNameHttpExceptions(%s)", countryName));
       assertThrows(HttpClientErrorException.class, () -> {
           CapitalLookupService.getCapitalLookup().findCapitalByPartialName(countryName);
       });
    }

    @ParameterizedTest
    @DisplayName("Positive test cases for findCapitalByFullName")
    // CSV Source is very useful - can even have this in an external file, to make it easier to modify test data sets
    @CsvSource({"Peru,Lima", "united States,'Washington, D.C.'", "UNITED Kingdom,London", "South Korea,Seoul",
            "New Zealand,Wellington", "South Africa,Pretoria", "Bolivia,La Paz"})
    // Bugs Found:
    // South Africa -> returns Pretoria but a few articles mentioned that it actually has three capitals, could be a bug
    // Bolivia -> has two capitals, the seat of the government is in La Paz, this returns Sucre, could be a bug
    void findCapitalByFullName(String countryName, String expectedResult) {
        TestLogger.getTestLogger().logTestOperation(String.format("findCapitalByFullName(%s)", countryName));
        try {
            assertEquals(expectedResult, CapitalLookupService.getCapitalLookup().findCapitalByFullName(countryName));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(String.format("Error while calling %s", CapitalLookupService.getCapitalLookup().getUrlForFullName(countryName)));
        }
    }

    @ParameterizedTest
    @DisplayName("Negative test cases for findCapitalByFullName, invalid country names")
    // CSV Source is very useful - can even have this in an external file, to make it easier to modify test data sets
    @CsvSource({"middle earth", "hogwarts", "arrakis", "Shangri-La", "1111", "*", "El Dorado", "Gotham City"})
    void findCapitalFullNameHttpExceptions(String countryName) {
        TestLogger.getTestLogger().logTestOperation(String.format("findCapitalFullNameHttpExceptions(%s)", countryName));
        assertThrows(HttpClientErrorException.class, () -> {
            CapitalLookupService.getCapitalLookup().findCapitalByFullName(countryName);
        });
    }
}