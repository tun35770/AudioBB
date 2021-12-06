package edu.temple.audiobb

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import javax.xml.transform.ErrorListener

class InputStreamVolleyRequest(method: Int, url: String?, listener: Response.Listener<ByteArray>, errorListener: Response.ErrorListener) : Request<ByteArray>(method, url, errorListener) {

    private var mListener: Response.Listener<ByteArray>
    //private lateinit var mParams: Map<String, String>
    private lateinit var responseHeaders: Map<String, String>

    init{
        //super(method, url, errorListener)
        setShouldCache(false)
        mListener = listener
        //mParams = params
    }

    /*override fun getParams() : Map<String, String> {
        return mParams
    }*/

    override fun getHeaders(): MutableMap<String, String> {
        var params = HashMap<String, String>()
        params.put("Content-Type", "application/json")
        return params
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
        responseHeaders = response?.headers as Map<String, String>
        return Response.success(response?.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray?) {
        mListener.onResponse(response)
    }

}