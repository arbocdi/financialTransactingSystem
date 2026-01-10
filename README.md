## High-Performance Financial Transaction System

You are a Java Developer at a leading financial institution tasked with building
a high-performance, scalable money transfer system that allows clients to view
their account lists and transfer funds to other clients. The system must handle high
concurrency, ensure data consistency, and integrate with multiple external
services (e.g., fraud detection, notification services, ledger updates).

## Key Requirements

#### 1. Account Management

- Fetch and display a list of accounts for a given client. [IN PROGRESS]
- Account data must be cached for performance. [IN PROGRESS]

#### 2. Money Transfer

- Support high-frequency transactions with low latency. [DONE]
- Ensure atomicity (transactions succeed or fail completely). [DONE]
- Handle retries in case of temporary failures (network issues, external
  service unavailability). [DONE]

#### 3. External Service Integration

- Fraud detection service (sync call, must be fast). [DONE]
- Notification service (async, eventual consistency). [IN PROGRESS]
- Ledger service (must be eventually consistent). [IN PROGRESS]

#### 4. Performance & Scalability

- Optimize for high throughput (thousands of transactions per second). [DONE]
- Use Kafka for async processing where possible. [DONE]
- Implement caching (Redis) to reduce database load. [IN PROGRESS]

#### 5. Resilience

- Retry policies for transient failures (exponential backoff). [DONE]
- Circuit breakers to avoid cascading failures. [IN PROGRESS]
- Idempotency to prevent duplicate transactions. [DONE]

#### Evaluation Criteria

- Correctness – No double-spending, data consistency. [DONE]
- Performance – Handles 1,000+ TPS with low latency. [DONE]
- Resilience – Retries, circuit breakers, idempotency. [RETRIES,IDEMPOTENCY]
- Scalability – Microservices, Kafka, caching. [CACHING IN PROGRESS]
- Code Quality – Clean architecture, tests, observability. [IN PROGRESS]
- Bonus Challenges (Optional)
    - How would you handle a partial failure where money is debited but not
      credited? [SAGA PATTERN]
  - How would you scale this globally (multi-region deployments)? [IN PROGRESS]
  - How would you introduce rate limiting to prevent abuse? [rolling window in redis, based on client identification]

[Scaling: kafka topic partitioning,db sharding]

[Miro board](https://miro.com/app/board/uXjVGahmmy0=/?share_link_id=875391363523)

[Only core functionality is implemented.] 
