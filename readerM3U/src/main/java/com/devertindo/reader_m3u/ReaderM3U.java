package com.devertindo.reader_m3u;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReaderM3U {
    public interface OnReadListener {
        void onRead(List<M3UItems> items);
        void onError(String err);
    }

    public static void readFromInput(String value, OnReadListener listener) {
        try {
            String[] strM3u = value.split("\\r?\\n");
            if (strM3u.length>0) {
                listener.onRead(parseM3U(strM3u));
            } else {
                listener.onError("Invalid input");
            }
        } catch (OutOfMemoryError e) {
            listener.onError(e.getMessage());
        }
    }

    public static void readFromFile(Context context, Uri uri, OnReadListener listener) {
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String allStrings = stringBuilder.toString();
                String[] strM3u = allStrings.split("\\r?\\n");
                listener.onRead(parseM3U(strM3u));
            } catch (IOException | OutOfMemoryError e) {
                listener.onError(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            listener.onError(e.getMessage());
        }
    }

    public static void readFromUrl(String url, String userAgent, OnReadListener listener) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .addHeader("User-Agent", userAgent)
                .url(url)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    listener.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.isSuccessful() && response.body()!=null) {
                            String responseBody = response.body().string();
                            String[] strM3u = responseBody.split("\\r?\\n");
                            listener.onRead(parseM3U(strM3u));
                        } else {
                            listener.onError(response.message());
                        }
                    } catch (OutOfMemoryError e) {
                        listener.onError(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }

    private static List<M3UItems> parseM3U(String[] arrStr) {
        List<M3UItems> items = new ArrayList<>();
        M3UItems item = new M3UItems();
        boolean bContinue = true;

        for (String line : arrStr) {
            if (line.startsWith("#EXTHTTP")) {
                int separatorIndex = line.indexOf(":");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setExt_http(line.substring(separatorIndex + 1).replace(" ",""));
                }
            }
            else if (line.startsWith("#KODIPROP:inputstream.adaptive.manifest_type")) {
                int separatorIndex = line.indexOf("=");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setManifest_type(line.substring(separatorIndex + 1));
                }
            }
            else if (line.startsWith("#EXTVLCOPT:http-user-agent")) {
                int separatorIndex = line.indexOf("=");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setHttp_user_agent(line.substring(separatorIndex + 1).replace("\"",""));
                }
            }
            else if (line.startsWith("#EXTVLCOPT:http-referrer")) {
                int separatorIndex = line.indexOf("=");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setHttp_referrer(line.substring(separatorIndex + 1).replace(" ","").replace("\"",""));
                }
            }
            else if (line.startsWith("#KODIPROP:inputstream.adaptive.license_type")) {
                int separatorIndex = line.indexOf("=");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setLicense_type(line.substring(separatorIndex + 1));
                }
            }
            else if (line.startsWith("#KODIPROP:inputstream.adaptive.license_key")) {
                int separatorIndex = line.indexOf("=");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    item.setLicense_key(line.substring(separatorIndex + 1).replace(" ",""));
                }
            }
            else if (line.startsWith("#EXTINF")) {
                int separatorIndex = line.indexOf(":");
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    String value = line.substring(separatorIndex + 1);

                    String patternTvId = "tvg-id=\"(.*?)\"";
                    Pattern pattern3 = Pattern.compile(patternTvId);
                    Matcher matcherTvId = pattern3.matcher(value);
                    if (matcherTvId.find()) {
                        String tvg_id = matcherTvId.group(1);
                        if (tvg_id != null) {
                            item.setTvg_id(tvg_id);
                        }
                    }

                    String patternLogo = "tvg-logo=\"(.*?)\"";
                    Pattern pattern = Pattern.compile(patternLogo);
                    Matcher matcherLogo = pattern.matcher(value);
                    if (matcherLogo.find()) {
                        String tvg_logo = matcherLogo.group(1);
                        if (tvg_logo != null) {
                            item.setTvg_logo(tvg_logo);
                        }
                    }

                    String patternTitle = "group-title=\"(.*?)\"";
                    Pattern pattern1 = Pattern.compile(patternTitle);
                    Matcher matcherTitle = pattern1.matcher(value);
                    if (matcherTitle.find()) {
                        String group_title = matcherTitle.group(1);
                        if (group_title!=null) {
                            item.setGroup_title(group_title);
                        }
                    }

                    boolean searchChannel = false;
                    int commaIndex = value.indexOf("\",");
                    if (commaIndex != -1 && commaIndex + 2 < value.length()) {
                        searchChannel = true;
                    } else {
                        commaIndex = value.indexOf(",");
                        if (commaIndex != -1 && commaIndex + 2 < value.length()) {
                            searchChannel = true;
                        }
                    }

                    if (searchChannel) {
                        String result = value.substring(commaIndex + 2);
                        if (result.contains(",")) {
                            int commaIndex1 = result.indexOf("\",");
                            String result1 = result.substring(commaIndex1 + 2);
                            item.setTitle(result1.trim());
                        } else {
                            item.setTitle(result.trim());
                        }
                    } else {
                        item.setTitle("Channel not identified");
                    }
                }
            }
            else if (line.startsWith("http") || line.startsWith("#http")) {
                String sLine = line.startsWith("#") ? line.substring(1) : line;
                item.setUrl(sLine.replace(" ",""));
                bContinue = false;
            }

            if (!bContinue) {
                items.add(item);
                item = new M3UItems();
                bContinue = true;
            }
        }

        return items;
    }
}
