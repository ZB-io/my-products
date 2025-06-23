
package com.bootexample4.api_tests;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
// import com.intuit.karate.http.HttpServer;
// import com.intuit.karate.http.ServerConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdpTest {

	@Test
	void testAll() {
		String apiHostServer = System.getenv().getOrDefault("url1_adp_URL_BASE", "http://127.0.0.1:4010");
		Results results = Runner.path("src/test/java/com/bootexample4/api_tests/Adp")
			.systemProperty("url1_adp_URL_BASE", apiHostServer)
			.reportDir("testReport")
			.parallel(1);
		assertEquals(0, results.getFailCount(), results.getErrorMessages());
	}

}
