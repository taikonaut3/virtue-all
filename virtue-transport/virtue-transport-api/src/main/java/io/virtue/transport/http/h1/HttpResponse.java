package io.virtue.transport.http.h1;

/**
 * Http Response.
 */
public interface HttpResponse extends HttpEnvelope {

    /**
     * http status code.
     *
     * @return
     */
    int statusCode();

}
