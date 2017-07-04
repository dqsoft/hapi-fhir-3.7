package ca.uhn.fhir.rest.client.interceptor;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.*;

/**
 * HTTP interceptor to be used for adding Cookie to requests.
 * <p>
 * This interceptor adds a header resembling the following:<br>
 * &nbsp;&nbsp;&nbsp;<code>Cookie: [key]=[value]</code><br>
 * </p>
 */

public class CookieInterceptor implements IClientInterceptor {
	private final String sessionCookie;

	public CookieInterceptor(String sessionCookie) {
		this.sessionCookie = sessionCookie;
	}

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		theRequest.addHeader(Constants.HEADER_COOKIE, sessionCookie); //$NON-NLS-1$
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) {
		// nothing
	}
}
