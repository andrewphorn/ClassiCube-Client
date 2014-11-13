package com.mojang.minecraft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.minecraft.gui.FontRenderer;

/**
 * Class used to store data for clicking URLs in the chat screen
 *
 * @author Jon
 */
public class ChatClickData {

    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    public final String message;
    private final ArrayList<LinkData> clickedUrls;
    
    // Regex pattern courtesy of Matthew O'Riordan
    private final String urlPattern = "((([A-Za-z]{2,9}:(?:\\/\\/)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+"
            + "|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)"
            + "((?:\\/[\\+~%\\/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.\\!\\/\\\\\\w]*))?)";
    private final Pattern compiledPattern = Pattern.compile(urlPattern);

    public ChatClickData(FontRenderer fontRenderer, String message) {
        this.message = message;
        clickedUrls = pullLinks(message, fontRenderer);
    }

    public static String stripControlCodes(String string) {
        return patternControlCode.matcher(string).replaceAll("");
    }

    public ArrayList<LinkData> getClickedUrls() {
        return clickedUrls;
    }

    public URI getURI(String message) {
        Matcher urlMatcher = compiledPattern.matcher(message);

        if (urlMatcher.matches()) {
            try {
                String url = urlMatcher.group(0);
                if (!url.startsWith("http://")) {
                    url = "http://" + url;
                }
                return new URI(url);
            } catch (URISyntaxException uriE) {
                // Not sure if we need to do anything here
                // I'm sure no error needs to be recorded
            }
        }
        return null;
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
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(new LinkData(urlStr,
                    fr.getWidth(text.substring(0, m.start())),
                    fr.getWidth(text.substring(0, m.end()))));
        }
        return links;
    }

    public class LinkData {

        public String link;
        public int x0;
        public int x1;

        public LinkData(String textualLink, int x0, int x1) {
            link = textualLink;
            this.x0 = x0;
            this.x1 = x1;
        }
    }
}
