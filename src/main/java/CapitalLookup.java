import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// Class for calling the RestCountries web services to locate a capital
public class CapitalLookup extends RestCountriesWebService {

    private CapitalLookup() {
    }

    // Singleton pattern
    private static CapitalLookup instance = new CapitalLookup();
    public static CapitalLookup getCapitalLookup() {
        return instance;
    }

    public String getUrlForPartialName(String partialCountryName) {
        return String.format("%s%s", nameWebServiceUrl, partialCountryName);
    }

    public String getUrlForFullName(String fullCountryName) {
        return String.format("%s%s?fullText=True", nameWebServiceUrl, fullCountryName);
    }

    // Calls the name service API for the country and returns the string containing the capital name
    private String getCapitalRestResponse(String urlForRestCountries) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(urlForRestCountries, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode capitalNode = root.findValue("capital");
        return capitalNode.get(0).asText();
    }

    // Finds the Capital for the partial name of the country
    // Example: MX returns Mexico City
    public String findCapitalByPartialName(String countryName) throws JsonProcessingException {
        String urlForPartialName = getUrlForPartialName(countryName);
        System.out.println(String.format("findCapitalByPartialName(%s), calling: %s", countryName, urlForPartialName));

        return getCapitalRestResponse(urlForPartialName);
    }

    // Finds the Capital for the full name of the country
    // Example: "United States" returns Washington, D.C.
    public String findCapitalByFullName(String countryName) throws JsonProcessingException {
        String urlForFullName = getUrlForFullName(countryName);
        System.out.println(String.format("findCapitalByFullName(%s), calling: %s", countryName, urlForFullName));

        return getCapitalRestResponse(urlForFullName);
    }
}
