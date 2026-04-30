package edu.arizona.videoshare.gatling;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class VideoShareSimulation extends Simulation {

        private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");

        private final HttpProtocolBuilder httpProtocol = http
                        .baseUrl(baseUrl)
                        .acceptHeader("application/json, text/html, */*")
                        .userAgentHeader("Gatling VideoShare Test");

        private final ScenarioBuilder homePageLoadTest = scenario("Home page loading test")
                        .exec(
                                        http("Load home page")
                                                        .get("/")
                                                        .check(status().is(200)));

        private final ScenarioBuilder publicVideosApiLoadTest = scenario("Public videos API loading test")
                        .exec(
                                        http("Fetch public videos")
                                                        .get("/api/videos")
                                                        .check(status().is(200)));

        {
                setUp(
                                homePageLoadTest.injectOpen(
                                                constantUsersPerSec(2).during(Duration.ofSeconds(10))),
                                publicVideosApiLoadTest.injectOpen(
                                                constantUsersPerSec(2).during(Duration.ofSeconds(10))))
                                .protocols(httpProtocol)
                                .assertions(
                                                global().failedRequests().percent().lt(5.0),
                                                global().responseTime().max().lt(5000));
        }
}