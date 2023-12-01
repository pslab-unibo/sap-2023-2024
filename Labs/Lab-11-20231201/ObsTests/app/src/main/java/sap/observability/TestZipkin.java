package sap.observability;

import brave.Tracing;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class TestZipkin {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Configure a reporter, which controls how often spans are sent
		
		// (this dependency is io.zipkin.reporter2:zipkin-sender-okhttp3)
		var sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
		
		// (this dependency is io.zipkin.reporter2:zipkin-reporter-brave)
		var zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);

		// Create a tracing component with the service name you want to see in Zipkin.
		var tracing = Tracing.newBuilder().localServiceName("my-service").addSpanHandler(zipkinSpanHandler).build();

		// Tracing exposes objects you might need, most importantly the tracer
		var tracer = tracing.tracer();
		
		// Start a new trace or a span within an existing trace representing an operation
		var span = tracer.startScopedSpan("my-span-name");
		
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {
		  span.error(ex); // Unless you handle exceptions, you might not know the operation failed!
		} finally {
		  span.finish(); // always finish the span
		}

		// Failing to close resources can result in dropped spans! When tracing is no
		// longer needed, close the components you made in reverse order. This might be
		// a shutdown hook for some users.
		tracing.close();
		zipkinSpanHandler.close();
		sender.close();

	}

}
