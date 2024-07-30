package org.example;

import com.google.gson.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
        do {
            GetLatLonByDistrict getLocation = new GetLatLonByDistrict();
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String formattedLat = decimalFormat.format(Double.parseDouble(getLocation.getLat())).replace(",",".");
            String formattedLon = decimalFormat.format(Double.parseDouble(getLocation.getLon())).replace(",",".");
            double lat = Double.parseDouble(formattedLat);
            double lon = Double.parseDouble(formattedLon);
            String baseUrl = "https://www.meteosource.com/api/v1/free/point";
            String sections = "daily";
            String timezone = "UTC";
            String language = "en";
            String units = "metric";
            String key = "apikey";

            // URL'ye parametre ekleyin
            String urlWithParams = String.format("%s?lat=%s&lon=%s&sections=%s&timezone=%s&language=%s&units=%s&key=%s",
                    baseUrl,
                    URLEncoder.encode(Double.toString(lat),"UTF-8"),
                    URLEncoder.encode(Double.toString(lon),"UTF-8"),
                    URLEncoder.encode(sections, "UTF-8"),
                    URLEncoder.encode(timezone, "UTF-8"),
                    URLEncoder.encode(language, "UTF-8"),
                    URLEncoder.encode(units, "UTF-8"),
                    URLEncoder.encode(key, "UTF-8"));
            URL url = new URL(urlWithParams);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.valueOf(url)))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response;
            Gson gson = new Gson();

            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("Request url = "+urlWithParams);
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    String jsonString = gson.toJson(jsonResponse);
                    try (FileWriter myWriter = new FileWriter("forecast.json")) {
                        myWriter.write(jsonString);
                        System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred while writing to the file.");
                        e.printStackTrace();
                    }
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonObject dailyObject = jsonObject.getAsJsonObject("daily");
                    JsonArray dataArray = dailyObject.getAsJsonArray("data");
                    System.out.println("------------------------------------------------------------------------------------------------------");
                    for (JsonElement dataElement : dataArray) {
                        JsonObject dataObject = dataElement.getAsJsonObject();
                        String day = dataObject.getAsJsonPrimitive("day").getAsString();
                        String weather = dataObject.getAsJsonPrimitive("weather").getAsString();
                        String summary = dataObject.getAsJsonPrimitive("summary").getAsString();
                        JsonObject alldayObject = dataObject.getAsJsonObject("all_day");
                        String averageTemp = alldayObject.getAsJsonPrimitive("temperature").getAsString();
                        System.out.println("Day                 => "+ day);
                        System.out.println("Weather             => "+ weather);
                        System.out.println("Summary             => "+ summary);
                        System.out.println("Average Temperature => "+ averageTemp);
                        System.out.println("------------------------------------------------------------------------------------------------------");
                    }
                } else {
                    System.out.println("urlWithParams = "+urlWithParams);
                    System.out.println("HTTP Request failed with status code: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }while (1<2);
    }
}
