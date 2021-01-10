import spock.lang.Shared;
import spock.lang.Stepwise;
import wslite.soap.*
import groovyx.net.http.HttpResponseDecorator;
import static groovyx.net.http.ContentType.JSON;
import groovy.util.CharsetToolkit;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.apache.http.HttpStatus.SC_OK as OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED as Unauthorized;
import spock.lang.Unroll;
import config.Config;
import groovyx.net.http.URIBuilder

@Stepwise
class FixerTest extends BaseSpecification {

	@Unroll
    def "Currency Conversion from #from to #to with amount #amount should give #expectedStatus (POST request)"() {

        when:
        def resp = client.get(
			path: baseUrl,
			query: [
                'from': "$from",
				'to': "$to",
				'amount': "$amount"
			]
			) as HttpResponseDecorator
		println "data" + resp.data;

        then:
        resp.status == expectedStatus
        if (expectedStatus == OK) {
            assert resp.data.success == true
            assert resp.data.rate != null
			assert resp.data.rate > 0
			assert resp.data.convertResult != null
			assert resp.data.convertResult > 0
        }

        where:
        from     |  to   | amount    | expectedStatus
        'USD'    | 'NOK' | 198       | OK
        'DKK'    | 'SEK' | 21        | OK
        'NOK'    | 'PKR' | 9         | OK
    }

	@Unroll
	def "Currency Conversion between #from and #to on amount #amount (GET request)"() {
		when:
		def request = """
		{
			"from": "$from",
			"to": "$to",
			"amount": "$amount"
		}
		"""
		
		def resp = client.post(
			path: baseUrl + "/currency", 
			requestContentType: JSON, 
			contentType: JSON, 
			body: request ) as HttpResponseDecorator
		println "Request " + request;
		println "Response" + resp.data;

		then:
		assert resp.status == OK
		assert resp.data.success == true
		assert resp.data.from == "$from"
		assert resp.data.amount != null

		where:
		from | to | amount | expectedStatus
		"SEK" | "USD" | 217.1 | OK
		"INR" | "PKR" | 3000 | OK
	
	}

	@Unroll
	def "Currency Conversion between #from and #to on amount #amount without API key "() {
		when:
		client.headers['api-key'] = "INVALID_API_KEY"

		def request = """
		{
			"from": "$from",
			"to": "$to",
			"amount": "$amount"
		}
		"""
		
		def resp = client.post(
			path: baseUrl + "/currency", 
			requestContentType: JSON, 
			contentType: JSON, 
			body: request ) as HttpResponseDecorator
		println "Request " + request;
		println "Response" + resp.data;
		println "Response" + resp.status;

		then:
		assert resp.data.success == null
		assert resp.status.toString() == "$expectedStatus"
	
		where:
		from | to | amount | expectedStatus
		"SEK" | "USD" | 217.1 | Unauthorized
		"INR" | "PKR" | 3000 | Unauthorized
	
	}

}