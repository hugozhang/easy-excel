package me.about.okhttp;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;


//示例 https://www.mkyong.com/java/okhttp-how-to-send-http-requests/

@Slf4j
public class OkHttp {

    private Request.Builder requestBuilder;

    private FormBody.Builder formBuilder;

    private MultipartBody.Builder multipartBodyBuilder;

    private Response response;

    private Map<String,Object> parameterMap = new HashMap();

    private Map<String,File> fileMap = new HashMap();

    private OkHttp() {
        this.requestBuilder = new Request.Builder();
        this.formBuilder = new FormBody.Builder();
        this.multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    public OkHttp header(String name,String value) {
        this.requestBuilder.addHeader(name,value);
        return this;
    }

    public OkHttp url(String url) {
        this.requestBuilder.url(url);
        return this;
    }

    private void execute() throws IOException {
        this.response = OkHttpHolder.INSTANCE.newCall(requestBuilder.build()).execute();
    }

    public OkHttp get() throws IOException {
        execute();
        return this;
    }

    public OkHttp form(String name,String value) {
        parameterMap.put(name,value);
        return this;
    }

    public OkHttp file(String name, File file) {
        fileMap.put(name,file);
        return this;
    }

    public OkHttp data(Object o) {
        if (o == null) throw new NullPointerException("o == null");
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSON.toJSONString(o)
                );
        this.requestBuilder.post(body);
        return this;
    }

    public OkHttp postForm() throws IOException {
        boolean isUpload = this.fileMap.isEmpty() ? false : true;
        Iterator<Map.Entry<String, Object>> iterator = this.parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (!isUpload) {
                this.formBuilder.add(entry.getKey(),entry.getValue().toString());
            } else {
                this.multipartBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue().toString());
            }
        }
        Iterator<Map.Entry<String, File>> it = this.fileMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, File> entry = it.next();
            this.multipartBodyBuilder.addFormDataPart(entry.getKey(), "file", RequestBody.create(MediaType.parse("application/octet-stream"),entry.getValue()));
        }
        if (!isUpload) {
            this.requestBuilder.post(this.formBuilder.build());
        } else {
            this.requestBuilder.post(this.multipartBodyBuilder.build());
        }
        execute();
        return this;
    }

    public OkHttp postJson() throws IOException {
        execute();
        return this;
    }

    public String asString() throws IOException {
        return this.response.body().string();
    }

    public <T> T asObject(Class<T> class1) throws IOException {
        String string = this.response.body().string();
        return JSON.parseObject(string,class1);
    }

    private static class OkHttpHolder {

        private static OkHttpClient INSTANCE;

        static {
            SSLContext sslContext = null;
            ConnectionSpec spec = null;
            try {
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .allEnabledCipherSuites()
                        .build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };

            List<ConnectionSpec> connectionSpecs = new ArrayList<>();
            connectionSpecs.add(spec);
            connectionSpecs.add(new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build());

            try {
                INSTANCE = new OkHttpClient().newBuilder()
                        .connectionSpecs(connectionSpecs)
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(3, TimeUnit.SECONDS)
                        .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //获取HostnameVerifier
        private static HostnameVerifier getHostnameVerifier() {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            return hostnameVerifier;
        }
    }

    public static OkHttp builder() {
        return new OkHttp();
    }


    public static void main(String[] args) throws IOException {
        System.out.println(OkHttp.builder().url("http://www.baidu.com").form("q","腾讯").file("asda",new File("pom.xml")).postForm().asString());

        System.out.println(OkHttp.builder().url("http://www.baidu.com").data(new Object()).postJson().asString());

    }


}
