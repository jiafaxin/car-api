package com.autohome.car.api.common.http;


import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 封装HttpClient
 */
public class HttpClientWrapper {
    private final static int SOCKET_TIMEOUT = 30000;
    private final static int CONNECT_TIMEOUT = 30000;
    private final static int CONNECTION_REQUEST_TIMEOUT = 30000;
    private final static int RETRY_COUNT = 0;
    private final static String ENCODING = "utf-8";


    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientWrapper.class);

    private enum HTTPMETHOD {
        GET, POST
    }

    private int socketTimeout = 50;
    private int connectTimeout = 50;
    private int connectionRequestTimeout = 50;

    private CloseableHttpClient client;
    private RequestConfig requestConfig;
    private static PoolingHttpClientConnectionManager connManager = null;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(400);
            connManager.setDefaultMaxPerRoute(30);
        } catch (KeyManagementException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public HttpClientWrapper() {
        this(CONNECTION_REQUEST_TIMEOUT, CONNECT_TIMEOUT, SOCKET_TIMEOUT, RETRY_COUNT);
    }

    public HttpClientWrapper(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
        this(connectionRequestTimeout, connectTimeout, socketTimeout, RETRY_COUNT);
    }

    public HttpClientWrapper(int connectionRequestTimeout, int connectTimeout, int socketTimeout, int retryCount) {
        super();
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
        //client                      = HttpClientBuilder.create().build();//不使用连接池
        if (retryCount > 0) {
            client = HttpClients.custom().setConnectionManager(connManager).setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, false)).build();
        } else {
            client = HttpClients.custom().setConnectionManager(connManager).build();
        }
        this.requestConfig = RequestConfig.custom().setConnectionRequestTimeout(this.connectionRequestTimeout)
                .setConnectTimeout(this.connectTimeout).setSocketTimeout(this.socketTimeout).build();
    }


    /**
     * get 请求
     *
     * @param url
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent getResponse(String url)
            throws IOException, HttpException {
        return this.getResponse(url, ENCODING);
    }

    /**
     * @param url
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent getResponse(String url, String encoding)
            throws IOException, HttpException {
        return this.getResponse(url, null, encoding);
    }

    /**
     * @param url
     * @param params
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent getResponse(String url, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return this.getResponse(url, null, params, encoding);
    }

    /**
     * @param url
     * @param header
     * @param params
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent getResponse(String url, Map<String, String> header, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return this.request(url, header, params, HTTPMETHOD.GET, encoding);
    }

    /**
     * @param url
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent postResponse(String url)
            throws IOException, HttpException {
        return this.postResponse(url, ENCODING);
    }

    /**
     * @param url
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent postResponse(String url, String encoding)
            throws IOException, HttpException {
        return this.postResponse(url, null, encoding);
    }

    /**
     * @param url
     * @param params
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent postResponse(String url, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return this.postResponse(url, null, params, encoding);
    }

    /**
     * @param url
     * @param header
     * @param params
     * @param encoding
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public ResponseContent postResponse(String url, Map<String, String> header, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return this.request(url, header, params, HTTPMETHOD.POST, encoding);
    }


    /**
     * 根据url编码，请求方式，请求URL
     *
     * @param url
     * @param header
     * @param params
     * @param method
     * @param encoding
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public ResponseContent request(String url, Map<String, String> header, Map<String, String> params, HTTPMETHOD method, String encoding)
            throws HttpException, IOException {
        if (StringUtils.isBlank(url))
            return null;

        encoding = StringUtils.isNotBlank(encoding) ? encoding : ENCODING;


        long startTimestamp = System.currentTimeMillis();

        HttpRequestBase request = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            if (method == HTTPMETHOD.GET) {
                if (params != null && params.size() > 0) {
                    String queryString = "";
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        queryString += entry.getKey() + "=" + entry.getValue() + "&";
                    }
                    queryString = queryString.substring(0, queryString.length() - 1);
                    url += (url.indexOf("?") > 0 ? "&" : "?") + queryString;
                }

                url = this.encodeURL(url.trim(), encoding);
                request = new HttpGet(url);
            } else if (method == HTTPMETHOD.POST) {
                url = this.encodeURL(url.trim(), encoding);
                request = new HttpPost(url);

                if (params != null && params.size() > 0) {
                    List<NameValuePair> body = new ArrayList<>();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        body.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(body, encoding));
                }

                header = (header == null || header.size() == 0) ? new HashMap<String, String>() : header;
                if (!header.containsKey(HttpHeaders.CONTENT_TYPE)) {
                    String contentType = "application/x-www-form-urlencoded";
                    header.put(HttpHeaders.CONTENT_TYPE, contentType);
                }
            }

            if (header != null && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
//            request.addHeader("X-API-RequestID", RequestID.getRequestID());

            request.setConfig(requestConfig);

            response = client.execute(request);
            entity = response.getEntity(); // 获取响应实体
            StatusLine statusLine = response.getStatusLine();

            ResponseContent rc = new ResponseContent(this.getResponseEncoding(entity, encoding));
            rc.setContentType(this.getResponseContentType(entity, null));
            rc.setStatusCode(statusLine.getStatusCode());
            rc.setContentBytes(EntityUtils.toByteArray(entity));

//            LOGGER.info("{\"HttpExecuteTime\":\"{}\",\"Method\":\"{}\",\"Url\":\"{}\",\"response\":{}}", System.currentTimeMillis() - startTimestamp, method, url, JSON.toJSONString(rc.getContent()));
            return rc;

        } catch (Exception ex) {
            LOGGER.error("{\"Exception\":\"{}\",\"HttpExecuteTime\":\"{}\",\"Method\":\"{}\",\"Url\":\"{}\"}", JSON.toJSONString(ex), System.currentTimeMillis() - startTimestamp, method, url);
            return null;
        } finally {
            if (request != null)
                request.releaseConnection();
            if (entity != null)
                entity.getContent().close();
            if (response != null)
                response.close();
        }
    }

    /**
     * 消息实体获取content-type
     *
     * @param entity
     * @param def
     * @return
     */
    private String getResponseContentType(HttpEntity entity, String def) {
        String contentType = def;
        Header header = entity.getContentType();
        if (header != null) {
            try {
                HeaderElement[] hes = header.getElements();
                if (hes != null && hes.length > 0) {
                    contentType = hes[0].getName();
                }
            } catch (Exception e) {
            }
        }
        return contentType;
    }


    /**
     * 消息实体获取encoding
     *
     * @param entity
     * @param def
     * @return
     */
    private String getResponseEncoding(HttpEntity entity, String def) {
        String encoding = def;
        Header header = entity.getContentEncoding();
        if (header != null) {
            encoding = header.getValue().toLowerCase();
        }
        return encoding;
    }

    static Set<Character> BEING_ESCAPED_CHARS = new HashSet();

    static {
        char[] signArray = {' ', '\\', '‘', ']', '!', '^', '#', '`', '$', '{', '%', '|', '}', '(', '+', ')', '<', '>',
                ';', '['};
        for (int i = 0; i < signArray.length; i++) {
            BEING_ESCAPED_CHARS.add(signArray[i]);
        }
    }

    /**
     * url 编码
     *
     * @param url
     * @param encoding
     * @return
     */
    private String encodeURL(String url, String encoding) {
        if (url == null)
            return null;
        if (encoding == null)
            return url;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c == 10) {
                continue;
            } else if (BEING_ESCAPED_CHARS.contains(c) || c == 13 || c > 126) {
                try {
                    sb.append(URLEncoder.encode(String.valueOf(c), encoding));
                } catch (Exception e) {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString().replaceAll("\\+", "%20");
    }

    /**
     * 上传用方法
     *
     * @param url
     * @param header
     * @param params
     * @param
     * @param encoding
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public ResponseContent upload(String url, Map<String, String> header, Map<String, byte[]> params, String encoding)
            throws HttpException, IOException {
        if (org.springframework.util.StringUtils.isEmpty(url)) {
            return null;
        }

        encoding = !org.springframework.util.StringUtils.isEmpty(encoding) ? encoding : ENCODING;


        long startTimestamp = System.currentTimeMillis();

        HttpRequestBase request = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {

            url = this.encodeURL(url.trim(), encoding);
            request = new HttpPost(url);
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
            if (params != null && params.size() > 0) {
                List<NameValuePair> body = new ArrayList<>();
                for (Map.Entry<String, byte[]> entry : params.entrySet()) {
                    mEntityBuilder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.MULTIPART_FORM_DATA, "");
                }
                ((HttpPost) request).setEntity(mEntityBuilder.build());
            }

            header = (header == null || header.size() == 0) ? new HashMap<String, String>() : header;

            if (header != null && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
            request.addHeader("X-API-RequestID", RequestID.getRequestID());

            request.setConfig(requestConfig);

            response = client.execute(request);
            entity = response.getEntity(); // 获取响应实体
            StatusLine statusLine = response.getStatusLine();

            ResponseContent rc = new ResponseContent(this.getResponseEncoding(entity, encoding));
            rc.setContentType(this.getResponseContentType(entity, null));
            rc.setStatusCode(statusLine.getStatusCode());
            rc.setContentBytes(EntityUtils.toByteArray(entity));

            return rc;

        } catch (Exception ex) {
            LOGGER.error("发送失败", ex);
            return null;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * post json
     *
     * @param url
     * @param header
     * @param encoding
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public ResponseContent postJson(String url, Map<String, String> header, String json, String encoding)
            throws HttpException, IOException {
        if (org.springframework.util.StringUtils.isEmpty(url)) {
            return null;
        }

        encoding = !org.springframework.util.StringUtils.isEmpty(encoding) ? encoding : ENCODING;


        long startTimestamp = System.currentTimeMillis();

        HttpRequestBase request = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {

            url = this.encodeURL(url.trim(), encoding);
            request = new HttpPost(url);
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();

            request.addHeader("Content-Type", "application/json");
            ((HttpPost) request).setEntity(new StringEntity(json, "utf-8"));


            header = (header == null || header.size() == 0) ? new HashMap<String, String>() : header;

            if (header != null && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
            request.addHeader("X-API-RequestID", RequestID.getRequestID());

            request.setConfig(requestConfig);

            response = client.execute(request);
            entity = response.getEntity(); // 获取响应实体
            StatusLine statusLine = response.getStatusLine();

            ResponseContent rc = new ResponseContent(this.getResponseEncoding(entity, encoding));
            rc.setContentType(this.getResponseContentType(entity, null));
            rc.setStatusCode(statusLine.getStatusCode());
            rc.setContentBytes(EntityUtils.toByteArray(entity));

            return rc;

        } catch (Exception ex) {
            LOGGER.error("发送失败", ex);
            return null;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        }
    }
}
