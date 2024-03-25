package com.example.omega.service.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;

public final class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

    private PaginationUtil() {
    }

    /**
     * Generate pagination headers for a Spring Data {@link org.springframework.data.domain.Page} object.
     *
     * @param uriBuilder The URI builder to construct pagination links.
     * @param page       The Page object containing paginated data.
     * @param <T>        The type of object contained in the Page.
     * @return HttpHeaders containing pagination information.
     */
    public static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));

        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        StringBuilder link = new StringBuilder();

        // Append "next" link if there are more pages available
        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next")).append(",");
        }

        // Append "prev" link if current page is not the first page
        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev")).append(",");
        }

        // Append "last" and "first" links
        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
                .append(",")
                .append(prepareLink(uriBuilder, 0, pageSize, "first"));

        headers.add(HttpHeaders.LINK, link.toString());
        return headers;
    }

    /**
     * Prepare a pagination link with the specified page number and page size.
     *
     * @param uriBuilder The URI builder to construct the link.
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param relType    The relationship type of the link (e.g., "next", "prev").
     * @return The formatted pagination link.
     */
    private static String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format(HEADER_LINK_FORMAT, preparePageUri(uriBuilder, pageNumber, pageSize), relType);
    }

    /**
     * Prepare the URI for the specified page number and page size.
     *
     * @param uriBuilder The URI builder to construct the URI.
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @return The prepared URI as a string.
     */
    private static String preparePageUri(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize) {
        return uriBuilder.replaceQueryParam("page", Integer.toString(pageNumber))
                .replaceQueryParam("size", Integer.toString(pageSize))
                .toUriString()
                .replace(",", "%2C")
                .replace(";", "%3B");
    }
}