package org.example;

import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import jakarta.servlet.http.*;

public class FunctionA extends HttpServlet {
    private static final String TARGET_URL = "https://my-function-b-369653134773.us-central1.run.app";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        if (!(credentials instanceof IdTokenProvider)) {
            throw new IOException("Credentials not valid for ID token generation.");
        }

        IdTokenCredentials tokenCredential = IdTokenCredentials.newBuilder()
                .setIdTokenProvider((IdTokenProvider) credentials)
                .setTargetAudience(TARGET_URL)
                .build();

        tokenCredential.refresh();

        URL url = new URL(TARGET_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + tokenCredential.getAccessToken().getTokenValue());

        Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        resp.setContentType("text/plain");
        resp.getWriter().println("Response from Function-B:");
        resp.getWriter().println(response);
    }
}
