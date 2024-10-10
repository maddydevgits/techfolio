package com.makeskilled.Techfolio.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${zeptomail.api.url}")
    private String postUrl;

    @Value("${zeptomail.api.key}")
    private String apiKey;

    @Value("${zeptomail.from.email}")
    private String fromEmail;

    public String sendEmail(String recipientEmail, String recipientName, String subject, String body) {
        StringBuffer response = new StringBuffer();
        BufferedReader br = null;
        HttpURLConnection conn = null;

        System.out.println("Post URL: " + postUrl);
        System.out.println("API Key: " + apiKey);
        System.out.println("From Email: " + fromEmail);
        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Zoho-enczapikey " + apiKey);  // Replace with your API Key

            JSONObject object = new JSONObject();
            JSONObject from = new JSONObject();
            from.put("address", fromEmail);

            JSONObject toEmailObj = new JSONObject();
            toEmailObj.put("address", recipientEmail);
            toEmailObj.put("name", recipientName);

            JSONObject to = new JSONObject();
            to.put("email_address", toEmailObj);

            object.put("from", from);
            object.put("to", new JSONObject[]{to});
            object.put("subject", subject);
            object.put("htmlbody", body);

            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();

            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response.toString();
    }
} 