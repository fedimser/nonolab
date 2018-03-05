package io.github.fedimser.nonolab.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WebLoader {
    public static String loadString(String url) {
        try {
            URL website = new URL(url);
            InputStream is = website.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is.transferTo(baos);
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
