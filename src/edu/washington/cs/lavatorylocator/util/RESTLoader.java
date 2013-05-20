package edu.washington.cs.lavatorylocator.util;

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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;



/**
 * The RESTLoader class builds a query from the parameters passed to it from
 * its parent Activity to send to the server. It then sends the response back
 * to the Activity to be processed.
 *
 *
 * @author Wil Sunseri
 *
 */
public class RESTLoader extends AsyncTaskLoader<RESTLoader.RESTResponse> {
    // Our app handles two types or requests, GET and POST

    public enum requestType {
        GET,
        POST
    }

    // A wrapper that contains the server's response as well as its status code
    public static class RESTResponse implements Parcelable {
        private HttpEntity responseData;
        private int responseCode;

        /**
         * Creates an "empty" RESTResponse
         *
         * The empty RESTResponse is used to indicate unsuccessful querying.
         *
         */
        public RESTResponse() {
        }

        /**
         * Creates a RESTResponse with data and the status code from the server.
         *
         * @param data
         * @param code
         */

        public RESTResponse(HttpEntity data, int code) {
            responseData = data;
            responseCode = code;
        }

        /**
         * Gets this RESTResponse's data.
         *
         * @return this RESTResponse's data
         */
        public HttpEntity getData() {
            return responseData;
        }

        /**
         * Gets this RESTResponse's status code.
         *
         * @return this RESTResponse's status code
         */
        public int getCode() {
            return responseCode;
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(responseData);
            dest.writeInt(responseCode);
        }
    }

    private final requestType rType;
    private final Bundle params;
    private Uri location;

    /**
     * Constructs a new RESTLoader.
     *
     * @param context the context of the Activity creating this RESTLoader
     * @param address the server address to send this RESTLoader to
     * @param type the type of request this RESTLoader is to use (GET/POST)
     * @param args the arguments to add to this RESTLoader's query
     */
    public RESTLoader(Context context, Uri address, requestType type,
            Bundle args) {
        super(context);

        location = address;
        rType = type;
        params = args;
    }

    /**
     * Queries the server and returns a RESTResponse containing the status code
     * of the request, the response data if there is any, or nothing if there
     * was an exception thrown on the way to the server.
     *
     * @return a RESTResponse containing any data relevant to the query
     */
    @Override
    public RESTResponse loadInBackground() {
        Log.i("tagged", "loading");
        try {
            if (location == null) {
                return new RESTResponse();
            } else {
                HttpRequestBase request = null;

                switch (rType) {
                // Because this is a GET request, we need to add the parameters
                // to the URL

                case GET: {
                    request = new HttpGet();
                    if (params == null) {
                        request.setURI(new URI(location.toString()));
                    } else {
                        final Uri.Builder uriBuilder = location.buildUpon();
                        for (BasicNameValuePair param : makeParamList(params)) {
                            uriBuilder.appendQueryParameter(param.getName(),
                                    param.getValue());
                        }
                        location = uriBuilder.build();
                        request.setURI(new URI(location.toString()));
                    }
                }
                    break;

                // Because this is a POST request, we need to encode the
                // parameters within the request
                case POST: {
                    request = new HttpPost();
                    request.setURI(new URI(location.toString()));
                    final HttpPost postRequest = (HttpPost) request;
                    if (params != null) {
                        final UrlEncodedFormEntity entity =
                                new UrlEncodedFormEntity(makeParamList(params));

                        postRequest.setEntity(entity);
                    }
                }
                    break;
                default:
                    break;
                }

                if (request != null) {
                    // The request was created successfully

                    final HttpParams httpParameters = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParameters,
                            5000);
                    HttpConnectionParams.setSoTimeout(httpParameters, 5000);

                    final HttpClient client = 
                            new DefaultHttpClient(httpParameters);

                    Log.i("tagged", "about to load");
                    final HttpResponse response = client.execute(request);

                    Log.i("tagged", "loading executed");

                    final HttpEntity responseEntity = response.getEntity();
                    final StatusLine responseStatus = response.getStatusLine();
                    int statusCode;
                    if (responseStatus != null) {
                        // We executed the request just fine
                        statusCode = responseStatus.getStatusCode();
                    } else {
                        // Something went wrong with the request's execution
                        statusCode = 0;
                    }
                    Log.i("tagged", Integer.toString(statusCode));
                    RESTResponse restResponse;
                    if (responseEntity != null) {
                        // There's some amount of response data to return
                        restResponse = new RESTResponse(responseEntity,
                                statusCode);
                    } else {
                        // There's absolutely no response data to return
                        restResponse = new RESTResponse(null, statusCode);
                    }
                    return restResponse;
                }
                return new RESTResponse();
            }
        } catch (UnsupportedEncodingException e) {
            Log.i("tagged", "got nothing from loading");
            return new RESTResponse();
        } catch (ClientProtocolException e) {
            Log.i("tagged", "got nothing from loading");
            return new RESTResponse();
        } catch (ParseException e) {
            Log.i("tagged", "got nothing from loading");
            return new RESTResponse();
        } catch (URISyntaxException e) {
            Log.i("tagged", "got nothing from loading");
            return new RESTResponse();
        } catch (IOException e) {
            Log.i("tagged", "got nothing from loading");
            return new RESTResponse();
        }
    }

    /**
     * Sends the result of this query to the Activity that created this Loader.
     * Note that we don't need to call this ourselves, as it's handled
     * automatically.
     *
     * @param data the data from the query
     */
    @Override
    public void deliverResult(RESTResponse data) {
        super.deliverResult(data);
    }

    // Tells the Loader to query the server and load data
    // Note: called automatically
    @Override
    public void onStartLoading() {
        forceLoad();
    }

    // Cancels this Loader's loading process
    // Note: called automatically
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    // Stop this Loader and resets its stored data to its default state

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    // private helper method that converts a Bundle of parameters into a List
    // of pairs that can be used more easily
    private List<BasicNameValuePair> makeParamList(Bundle params) {
        final List<BasicNameValuePair> paramList =
                new LinkedList<BasicNameValuePair>();
        for (String key : params.keySet()) {
            paramList.add(new BasicNameValuePair(key, params.getString(key)));
        }
        return paramList;
    }
}
