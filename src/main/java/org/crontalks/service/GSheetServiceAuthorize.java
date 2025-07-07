package org.crontalks.service;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.google.api.client.json.gson.GsonFactory.getDefaultInstance;

@Slf4j
@Component
public class GSheetServiceAuthorize {

    @Value("${GSHEETS_JSON_PATH:classpath:cred.json}")
    private String credentialsResource;

    private final ResourceLoader resourceLoader;

    public GSheetServiceAuthorize(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Sheets authorize() throws IOException {
        final List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        NetHttpTransport transport = new NetHttpTransport.Builder().build();
        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(GoogleCredentials.fromStream(getFromResource()).createScoped(scopes));

        return new Sheets.Builder(transport, getDefaultInstance(), httpRequestInitializer)
            .setApplicationName("talks-reminders")
            .build();
    }

    private InputStream getFromResource() throws IOException {
        try {
            new JSONObject(credentialsResource);
            return IOUtils.toInputStream(credentialsResource, StandardCharsets.UTF_8);
        } catch (JSONException e) {
            return credentialsResource.contains("classpath:")
                ? resourceLoader.getResource(credentialsResource).getInputStream()
                : new FileInputStream(credentialsResource);
        }

    }

}
