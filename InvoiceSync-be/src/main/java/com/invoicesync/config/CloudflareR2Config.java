package com.invoicesync.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Configures an {@link S3Client} pointed at Cloudflare R2 instead of AWS.
 * R2 speaks the S3 API, so the regular AWS SDK works against it once the
 * endpoint, region and a couple of R2-specific quirks below are set.
 */
@Configuration
public class CloudflareR2Config {

  @Value("${r2.access-key-id}")
  private String accessKey;

  @Value("${r2.secret-access-key}")
  private String secretKey;

  @Value("${r2.endpoint}")
  private String endpointUrl;

  @Bean
  public S3Client r2Client() {
    AwsBasicCredentials credentialsProvider = AwsBasicCredentials.create(
        accessKey,
        secretKey
    );

    // R2 requires path-style (endpoint/bucket/key), not AWS's default virtual-hosted-style
    // (bucket.endpoint/key). Chunked encoding must be disabled, otherwise R2 rejects the
    // request signature (403) - this is an R2 quirk, not something AWS S3 needs.
    S3Configuration serviceConfiguration = S3Configuration.builder()
        .pathStyleAccessEnabled(true)
        .chunkedEncodingEnabled(false)
        .build();

    return S3Client.builder()
        .endpointOverride(URI.create(endpointUrl))
        .credentialsProvider(StaticCredentialsProvider.create(credentialsProvider))
        .region(Region.of("auto")) // required by the SDK but not used by R2
        // SDK 2.30+ adds extra data-integrity checksums by default on every request, which
        // R2 doesn't support the same way AWS S3 does and rejects. WHEN_REQUIRED restores
        // the old behaviour of only adding them where an operation truly requires one.
        .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
        .build();
  }

  public String getAccessKey() {
    return accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public String getEndpointUrl() {
    return this.endpointUrl;
  }

}
