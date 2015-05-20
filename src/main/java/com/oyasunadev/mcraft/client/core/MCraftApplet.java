/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oyasunadev.mcraft.client.core;

import com.mojang.minecraft.MinecraftApplet;
import com.mojang.util.LogUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Override the MinecraftApplet class because we need to fake the Document Base
 * and Code Base.
 */
public class MCraftApplet extends MinecraftApplet {

    private static final String CODE_BASE_URL = "http://minecraft.net:80/",
            DOCUMENT_BASE_URL = "http://minecraft.net:80/play.jsp";

    private final Map<String, String> parameters = new HashMap<>();

    @Override
    public URL getCodeBase() {
        try {
            return new URL(CODE_BASE_URL);
        } catch (MalformedURLException ex) {
            LogUtil.logError("Error getting applet code base.", ex);
            return null;
        }
    }

    @Override
    public URL getDocumentBase() {
        try {
            return new URL(DOCUMENT_BASE_URL);
        } catch (MalformedURLException ex) {
            LogUtil.logError("Error getting applet document base.", ex);
            return null;
        }
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

}
