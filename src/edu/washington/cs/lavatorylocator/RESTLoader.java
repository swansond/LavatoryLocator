package edu.washington.cs.lavatorylocator;


//TODO: COMMENT
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class RESTLoader extends AsyncTaskLoader<RESTLoader.RESTResponse> {

    public enum requestType {
        GET,
        POST
    }

    public static class RESTResponse {
        private HttpEntity responseData;
        private int responseCode;


        public RESTResponse() {
        }

        public RESTResponse(HttpEntity data, int code) {
            responseData = data;
            responseCode = code;
        }
        
        public HttpEntity getData() {
            return responseData;
        }
        
        public int getCode() {
            return responseCode;
        }
    }
    
    private requestType rType;
    private Bundle params;
    private Uri location;
    private RESTResponse response;
    
    public RESTLoader(Context context, Uri address, requestType type) {
        super(context);
        
        location = address;
        rType = type;
    }
    
    public RESTLoader(Context context, Uri address, requestType type,
            Bundle args) {
        super(context);
        
        location = address;
        rType = type;
        params = args;
    }
    
    @Override
    public RESTResponse loadInBackground() {
        try {
            if (location == null) {
                return new RESTResponse();
            } else {
                HttpRequestBase request = null;

                switch (rType) {
                case GET: {
                    request = new HttpGet();
                    if (params == null) {
                        request.setURI(new URI(location.toString()));
                    } else {
                        Uri.Builder uriBuilder = location.buildUpon();
                        for (BasicNameValuePair param : makeParamList(params)) {
                            uriBuilder.appendQueryParameter(param.getName(),
                                    param.getValue());
                        }
                        location = uriBuilder.build();
                        request.setURI(new URI(location.toString()));
                    }
                }
                break;

                case POST: {
                    request = new HttpPost();
                    request.setURI(new URI(location.toString()));
                    HttpPost postRequest = (HttpPost) request;
                    if (params != null) {
                        UrlEncodedFormEntity entity = 
                                new UrlEncodedFormEntity(makeParamList(params)); 
                        postRequest.setEntity(entity);
                    }
                }
                break;
                }

                if (request != null) {
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(request);
                    
                    Log.i("tag", response.getStatusLine().toString());
                    
                    HttpEntity responseEntity = response.getEntity();
                    StatusLine responseStatus = response.getStatusLine();
                    int statusCode;
                    if (responseStatus != null) {
                        statusCode = responseStatus.getStatusCode();
                    } else {
                        statusCode = 0;
                    }

                    RESTResponse restResponse;
                    if (responseEntity != null) {
                        restResponse = new RESTResponse(responseEntity, 
                                statusCode);
                    } else {
                        restResponse = new RESTResponse();
                    }
                    return restResponse;
                }
                return new RESTResponse();
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return new RESTResponse();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            return new RESTResponse();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return new RESTResponse();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            return new RESTResponse();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return new RESTResponse();
        }
    }
    
    @Override
    public void deliverResult(RESTResponse data) {
        response = data;
        super.deliverResult(data);
    }
    
    @Override
    public void onStartLoading() {
        forceLoad();
    }
    
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        response = null;
    }
    
    private List<BasicNameValuePair> makeParamList(Bundle params) {
        List<BasicNameValuePair> paramList = 
                new LinkedList<BasicNameValuePair>();
        for (String key : params.keySet()) {
            paramList.add(new BasicNameValuePair(key, params.getString(key)));
        }
        return paramList;
    }
}
