package minow.pwr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.*;


public class CombineApiData {

    private static final String API_DATA_TEMPLATE = "https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-data-section?id-zmienna=303&id-przekroj=740&id-rok=%d&id-okres=247&ile-na-stronie=5000&numer-strony=0&lang=pl";
    private static final String API_NAMES_TEMPLATE = "https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-section-position?id-zmienna=303&id-przekroj=740&id-rok=%d&id-okres=247&ile-na-stronie=0&lang=pl";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer, String> positionNames;

    public List<CenyTowarow> fetchDataForYear(int year) throws Exception {
        String api1Url = String.format(API_DATA_TEMPLATE, year);
        String api2Url = String.format(API_NAMES_TEMPLATE, year);

        // Fetch JSON data
        String api1Json = fetchJson(api1Url);
        String api2Json = fetchJson(api2Url);

        // Parse data
        List<ApiData> apiDataList = parseApi1Data(api1Json);
        List<ApiNames> apiNamesList = parseApi2Data(api2Json);

        // Build mappings
        positionNames = new HashMap<>();
        for (ApiNames apiNames : apiNamesList) {
            positionNames.put(apiNames.getIdPozycja(), apiNames.getNazwaPozycja());
        }

        // Combine data
        List<CenyTowarow> combinedData = new ArrayList<>();
        for (ApiData apiData : apiDataList) {
            CenyTowarow item = new CenyTowarow();
            item.setYear(apiData.getIdDaty());
            item.setValue(apiData.getWartosc());
            item.setPosition1Name(positionNames.getOrDefault(apiData.getIdPozycja1(), "Unknown"));
            item.setPosition2Name(positionNames.getOrDefault(apiData.getIdPozycja2(), "Unknown"));
            combinedData.add(item);
        }
        return combinedData;
    }

    public Set<String> getUniqueProducts() {
        if (positionNames == null) return Collections.emptySet();
        return new TreeSet<>(positionNames.values());
    }

    private String fetchJson(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new Exception("Failed to fetch JSON. Code: " + response.code());
            }
        }
    }

    private List<ApiData> parseApi1Data(String json) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode dataNode = rootNode.get("data");
        if (dataNode == null || !dataNode.isArray()) {
            throw new Exception("API 1: 'data' field is missing or not an array.");
        }
        return objectMapper.readValue(dataNode.toString(), new TypeReference<List<ApiData>>() {});
    }

    private List<ApiNames> parseApi2Data(String json) throws Exception {
        return objectMapper.readValue(json, new TypeReference<List<ApiNames>>() {});
    }
}

//public class CombineApiData {
//    public static void main(String[] args) {
//        String apiDataUrl = "https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-data-section?id-zmienna=303&id-przekroj=740&id-rok=2023&id-okres=247&ile-na-stronie=5000&numer-strony=0&lang=pl";
//        String apiNamesUrl = "https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-section-position?id-zmienna=303&id-przekroj=740&id-rok=2023&id-okres=247&ile-na-stronie=0&lang=pl";
//
//        OkHttpClient client = new OkHttpClient();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            String api1Json = fetchJson(client, apiDataUrl);
//            JsonNode rootNode1 = objectMapper.readTree(api1Json);
//            JsonNode dataNode = rootNode1.get("data");
//            if (dataNode == null || !dataNode.isArray()) {
//                throw new Exception("ApiData: 'data' field is missing or not an array.");
//            }
//            List<ApiData> apiDataList = objectMapper.readValue(dataNode.toString(), new TypeReference<List<ApiData>>() {});
//
//
//            String api2Json = fetchJson(client, apiNamesUrl);
//            List<ApiNames> apiNamesList = objectMapper.readValue(api2Json, new TypeReference<List<ApiNames>>() {});
//
//
//            Map<Integer, String> dimensionNames = new HashMap<>();
//            Map<Integer, String> positionNames = new HashMap<>();
//            for (ApiNames apiNames : apiNamesList) {
//                dimensionNames.put(apiNames.getIdWymiar(), apiNames.getNazwaWymiar());
//                positionNames.put(apiNames.getIdPozycja(), apiNames.getNazwaPozycja());
//            }
//
//
//            for (ApiData apiData : apiDataList) {
//                CenyTowarow combined = new CenyTowarow();
//                combined.setYear(apiData.getIdDaty());
//                combined.setValue(apiData.getWartosc());
//                combined.setPosition1Name(positionNames.get(apiData.getIdPozycja1()));
//                combined.setPosition2Name(positionNames.get(apiData.getIdPozycja2()));
//
//                System.out.println(combined.toString());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String fetchJson(OkHttpClient client, String url) throws Exception {
//        Request request = new Request.Builder().url(url).build();
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                return response.body().string();
//            } else {
//                throw new Exception("Failed to fetch JSON. Code: " + response.code());
//            }
//        }
//    }
//}
