package com.mojang.minecraft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.minecraft.gui.FontRenderer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to store data for clicking URLs in the chat screen
 *
 * @author Jon
 */
public class ChatClickData {

    public final String message;

    // Regex pattern courtesy of Matthew O'Riordan
    private final String urlPattern = "\\(?((([A-Za-z]{2,9}:(\\/\\/)?)([\\-;:&=\\+\\$,\\w][\\-;:&=\\+\\$,\\w]+@)?([A-Za-z0-9]([A-Za-z0-9\\-]*[A-Za-z0-9])?)(\\.[A-Za-z0-9]([A-Za-z0-9\\-]*[A-Za-z0-9])?)+|(www\\.|[\\-;:&=\\+\\$,\\w]+@)([A-Za-z0-9]([A-Za-z0-9\\-]*[A-Za-z0-9])?)(\\.[A-Za-z0-9]([A-Za-z0-9\\-]*[A-Za-z0-9])?)+)(:\\d+)?((\\/[\\+~%\\/\\.\\w\\-_\\(\\)]*)?(\\?[\\-\\+=&;%@\\.\\w_]*)?(#\\S*)?))";
    private final Pattern compiledPattern = Pattern.compile(urlPattern);

    public ChatClickData(String message) {
        this.message = FontRenderer.stripColor(message);
    }

    public ArrayList<LinkData> getClickedUrls(FontRenderer fontRenderer) {
        return pullLinks(message, fontRenderer);
    }

    /**
     * Strips any URLs from the the line where the user clicked
     *
     * @param text The text in question
     * @param fr The font renderer instance
     * @return ArrayList of LinkData
     */
    private ArrayList<LinkData> pullLinks(String text, FontRenderer fr) {
        ArrayList<LinkData> links = new ArrayList<>();
        Matcher m = compiledPattern.matcher(text);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String urlStr = m.group();
            if (urlStr.charAt(0) == '(') {
                start++;
                if (urlStr.endsWith(")")) {
                    end--;
                }
                urlStr = text.substring(start, end);
            }

            try {
                links.add(new LinkData(new URI(urlStr),
                        fr.getWidth(text.substring(0, start)),
                        fr.getWidth(text.substring(0, end))));
            } catch (URISyntaxException ex) {
                // Suppress
            }
        }
        return links;
    }

    public class LinkData {

        public URI url;
        public int x0;
        public int x1;

        public LinkData(URI textualLink, int x0, int x1) {
            url = textualLink;
            this.x0 = x0;
            this.x1 = x1;
        }
    }
}
