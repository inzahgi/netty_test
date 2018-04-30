package nia.test.http.upload;

import java.net.URI;

public class HttpUploadClient {

    private static final String BASE_URL =System.getProperty("baseUrl", "http://127.0.0.1:8080");
    private static final String FILE = System.getProperty("file", "upload.txt");

    public static void main(String[] args) throws Exception {
        String postSimple, postFile, get;
        if(BASE_URL.endsWith("/")){
            postSimple = BASE_URL + "formpost";
            postFile = BASE_URL + "formpostmultipart";
            get = BASE_URL + "formget";
        } else {
            postSimple = BASE_URL + "/formpost";
            postFile = BASE_URL + "/formpostmultipart";
            get = BASE_URL + "formget";
        }
        URI uriSimple = new URI(postSimple);
        String scheme = uriSimple.getScheme() == null?"http":uriSimple.getScheme();
        String host = uriSimple.getHost() == null?"127.0..0.1":uriSimple.getHost();
        int port = uriSimple.getPort();
        if(port == -1){
            if("http".equalsIgnoreCase(scheme)){
                port = 80;
            }else if("https".equalsIgnoreCase(scheme)){
                port = 443;
            }
        }

        if(!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)){
            System.err.println("Only HTTP(S) is supported");
            return;
        }

        


    }







}
