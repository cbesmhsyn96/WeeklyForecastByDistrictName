package org.example;

import com.google.gson.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.*;
import java.net.http.HttpResponse;

public class GetLatLonByDistrict {private String baseUrl = "https://geocode.maps.co/search";
    private String q;
    private String api_key = "apikey";
    private Scanner scanner = new Scanner(System.in);
    private HttpResponse<String> response;
    private Gson gson = new Gson();
    private JsonArray jsonArray;

    // Constructor
    public GetLatLonByDistrict() throws UnsupportedEncodingException, MalformedURLException {
        System.out.print("Haftalık hava durumu tahminini görmek istediğiniz ilçe : ");
        q = scanner.nextLine();

        // URL ve HttpRequest oluştur
        String urlWithParamsGetLatLon = String.format("%s?q=%s&api_key=%s",
                baseUrl,
                URLEncoder.encode(q, "UTF-8"),
                URLEncoder.encode(api_key, "UTF-8"));

        URL urlLocation = new URL(urlWithParamsGetLatLon);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.valueOf(urlLocation)))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        // HttpClient ile istek yap
        HttpClient client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // JSON işleme kodları
        processResponse();
    }

    private void processResponse() {
        if (response != null && response.statusCode() == 200) {
            JsonElement jsonResponse = JsonParser.parseString(response.body());
            if (jsonResponse.isJsonArray()) {
                jsonArray = jsonResponse.getAsJsonArray();
            } else {
                System.out.println("Beklenen JSON dizisi değil.");
                return;
            }
        } else {
            System.out.println("Yanıt alınamadı veya hata kodu: " + (response != null ? response.statusCode() : "null"));
        }
    }

    public String getLat(){
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            String lat = jsonObject.get("lat").getAsString();
            return lat;
        }
        return "";
    }

    public String getLon(){
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            String lon = jsonObject.get("lon").getAsString();
            return lon;
        }
        return "";
    }

    public void printLatLon() {
        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String lat = jsonObject.get("lat").getAsString();
                String lon = jsonObject.get("lon").getAsString();
                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);
                System.out.println("------");
            }
        } else {
            System.out.println("JSON dizisi boş veya mevcut değil.");
        }
    }

}
