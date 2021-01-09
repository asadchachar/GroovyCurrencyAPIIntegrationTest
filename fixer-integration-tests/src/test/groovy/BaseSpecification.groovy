import config.Config
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class BaseSpecification extends Specification {

	@Shared def client	
	@Shared def apiKey
	@Shared def baseUrl

	def setupSpec() {

		apiKey = Config.getApiKey('fixer');
		println("apiKeyss: "+ apiKey)
		baseUrl = Config.getBaseUrl('fixer') + "/convert"
		System.out.println("Using baseUrl=" + baseUrl)

		client = new RESTClient(baseUrl);
		client.handler.failure = { resp, data -> resp.data = data; return resp }
		client.headers['api-key'] = apiKey
		
	}	

}
