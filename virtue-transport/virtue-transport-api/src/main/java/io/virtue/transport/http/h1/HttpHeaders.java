package io.virtue.transport.http.h1;

import java.util.Map;

/**
 * Http headers.
 */
public interface HttpHeaders extends Iterable<Map.Entry<CharSequence, CharSequence>> {

    /**
     * Get header by name.
     *
     * @param name
     * @return
     */
    CharSequence get(CharSequence name);

    /**
     * Add header.
     *
     * @param name
     * @param value
     */
    void add(CharSequence name, CharSequence value);

    /**
     * Add headers.
     *
     * @param headers
     */
    void add(Map<CharSequence, CharSequence> headers);

    /**
     * Add headers.
     *
     * @param headers
     */
    void add(HttpHeaders headers);

}
