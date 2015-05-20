package com.oyasunadev.mcraft.client.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run Minecraft Classic standalone version.
 */
public class ClassiCubeStandalone {
    // Direct URL parsing
    private static final String directUrlPattern = "^mc://" // scheme 
            + "(localhost|(\\d{1,3}\\.){3}\\d{1,3}|([a-zA-Z0-9\\-]+\\.)+([a-zA-Z0-9\\-]+))" // host/IP
            + "(:(\\d{1,5}))?/" // port
            + "([^/]+)" // username
            + "(/(.*))?$"; // mppass
    private static final Pattern directUrlRegex = Pattern.compile(directUrlPattern);

    private static final String BLANK_MPPASS = "00000000000000000000000000000000";
    
    public static void main(String[] args) {
        String player = null;
        String server = null;
        int port = 0;
        String mppass = null;
        String skinServer = null;
        boolean startFullScreen = false;
        try {
            if (args != null && args.length> 0 && args.length <= 3) {
                // direct-connect URL
                ServerJoinInfo details = getDetailsFromDirectUrl(args[0]);
                if (details != null) {
                    server = details.address.getHostAddress();
                    port = details.port;
                    player = details.playerName;
                    mppass = details.pass;
                }
                if (args.length > 1) {
                    skinServer = args[1];
                }
                if (args.length > 2) {
                    startFullScreen = Boolean.parseBoolean(args[2]);
                }
            } else if (args != null && args.length > 3) {
                server = args[0];
                port = Integer.parseInt(args[1]);
                player = args[2];
                mppass = args[3];
                if (args.length > 4) {
                    skinServer = args[4];
                }
                if (args.length > 5) {
                    startFullScreen = Boolean.parseBoolean(args[5]);
                }
            }
            ClassiCubeStandalone classicubeStandalone = new ClassiCubeStandalone();
            if (player == null || server == null || mppass == null || port <= 0) {
                classicubeStandalone.startMinecraft(null, null, null, 0, skinServer, startFullScreen);
            } else {
                classicubeStandalone.startMinecraft(player, server, mppass, port, skinServer, startFullScreen);
            }
        } catch (Exception e) {
            System.err.println("ClassiCube client: Cannot parse parameters: " + e.getMessage());
        }
    }

    public void startMinecraft(String player, String server, String mppass, int port,
            String skinServer, boolean fullscreen) {
        MinecraftFrame minecraftFrame = new MinecraftFrame();

        minecraftFrame.startMinecraft(player, server, mppass, port, skinServer, fullscreen);
    }

    private static ServerJoinInfo getDetailsFromDirectUrl(final String url) {
        if (url == null) {
            throw new NullPointerException("url");
        }
        ServerJoinInfo result = new ServerJoinInfo();
        final Matcher directUrlMatch = directUrlRegex.matcher(url);
        if (directUrlMatch.matches()) {
            try {
                result.address = InetAddress.getByName(directUrlMatch.group(1));
            } catch (final UnknownHostException ex) {
                return null;
            }
            final String portNum = directUrlMatch.group(6);
            if (portNum != null && portNum.length() > 0) {
                try {
                    result.port = Integer.parseInt(portNum);
                } catch (final NumberFormatException ex) {
                    return null;
                }
            } else {
                result.port = 25565;
            }
            result.playerName = directUrlMatch.group(7);
            final String mppass = directUrlMatch.group(9);
            if (mppass != null && mppass.length() > 0) {
                result.pass = mppass;
            } else {
                result.pass = BLANK_MPPASS;
            }
            return result;
        }
        return null;
    }

    static class ServerJoinInfo {

        public String playerName;
        public InetAddress address;
        public int port;
        public String pass;
    }
}
