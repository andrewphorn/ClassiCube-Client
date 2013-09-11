package com.mojang.minecraft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.minecraft.gui.FontRenderer;

public class ChatClickData {
    public static final Pattern pattern = Pattern
	    .compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
    public final String message;
    private final ArrayList<LinkData> clickedUrls;

    public ChatClickData(FontRenderer fontRenderer, ChatLine chatLine,
	    int x, int y) {
	this.message = chatLine.message;
	this.clickedUrls = this.pullLinks(message, fontRenderer);
    }

    public ArrayList<LinkData> getClickedUrls() {
	return this.clickedUrls;
    }

    public URI getURI(String message) {
	Matcher urlMatcher = pattern.matcher(message);

	if (urlMatcher.matches()) {
	    try {
		String url = urlMatcher.group(0);

		if (urlMatcher.group(1) == null) {
		    url = "http://" + url;
		}

		return new URI(url);
	    } catch (URISyntaxException uriE) {

	    }
	}
	return null;
    }

    private ArrayList<LinkData> pullLinks(String text, FontRenderer fr) {
	ArrayList<LinkData> links = new ArrayList<LinkData>();
	String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
	Pattern p = Pattern.compile(regex);
	Matcher m = p.matcher(text);
	while (m.find()) {
	    String urlStr = m.group();
	    if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
		urlStr = urlStr.substring(1, urlStr.length() - 1);
	    }
	    links.add(new LinkData(urlStr, fr.getWidth(text.substring(0,
		    m.start())), fr.getWidth(text.substring(0, m.end()))));
	}
	return links;
    }

    private static final Pattern patternControlCode = Pattern
	    .compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String stripControlCodes(String string) {
	return patternControlCode.matcher(string).replaceAll("");
    }

    public class LinkData {
	public String link;
	public int x0;
	public int x1;

	public LinkData(String s, int a, int b) {
	    link = s;
	    x0 = a;
	    x1 = b;
	}
    }
}
