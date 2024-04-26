package io.virtue.transport.http.h1;

/**
 * Http response.
 */
public interface HttpResponse extends HttpEnvelope {

    /**
     * Http response status code.
     *
     * @return
     */
    int statusCode();

}
