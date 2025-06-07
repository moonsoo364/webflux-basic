# webflux-basic

스프링 부트 웹플럭스 기본 개념 및 동작을 설명하기 위한 프로젝트

# 리액트 프로그래밍과 웹플럭스

## 리액티브 프로그래밍 이란?

리액티브 프로그래밍은 비동기 비동기 데이터 흐름(Asynchronous data stream)으로 구성된 프로그래밍 패러다임입니다. 이벤트가 발생하면 그에 반응하는 방식으로 동작합니다.

리액티브 프로그래밍의 특징

1. 데이터 흐름(Streams) : 값들이 시간에 따라 연속적으로 발생하는 흐름을 처리합니다.
2. 비동기 (Asynchronous) : 작업이 완료될 때까지 기다리지 않고 다음 작업을 계속 수행합니다.
3. 논블로킹(Non-blocking) : 스레드를 점유하지 않으며, 자원을 효율적으로 사용합니다.
4. 백프래셔(Backpressure) : 컨슈머가 데이터를 감당하지 못할 경우 프로듀서가 이를 조절할 수 있습니다.

## 웹플럭스(WebFlux)란

웹플럭스는 Spring 5에 도입된 웹 프레임워크입니다. 전통적은 Spring MVC가 블로킹 I/O 기반이라면, WebFlux는 논블로킹, 리액티브 프로그래밍을 기반으로 만들어져있습니다.

웹플럭스의 특징

1. 논블로킹 I/O : 성능과 확장성 향상
2. 리액터(Reactor) 라이브러리 기반 : `Mono`, `Flux`를 핵심 타입으로 사용
3. 함수형 스타일 코드
4. Netty, Undertow 등 논블로킹 서버 엔진 지원

# 웹플럭스 HTTP 요청 흐름

웹플럭스에서 HTTP 통신의 요청을 처리하는 방법을 알아보겠습니다.

## HTTP 요청 흐름

클라이언트에서 API 요청 시 아래와 같은 흐름으로 동작합니다.

1. 클라이언트에서 요청이 오면 서버 엔진인 Netty에서 이를 수신합니다.
2. `ReactorHttpHandlerAdapter` 가 Netty와 WebFlux를 연결합니다.
3.  `HttpWebHandlerAdapter` 는 HTTP 요청을 `Mono`, `Flux`로 처리합니다.
4. `DispatcherHandler` 에서 클라이언트의 요청 URL을 컨트롤러로 라우팅
5. `@Controller` 에서 요청을 처리하고 `Mono` , `Flux` 로 반환합니다.
6. 웹플럭스가 Netty로 응답 결과를 전달하고 Netty에서 클라이언트로 응답을 전달합니다.

```
[클라이언트 요청: HTTP GET/POST 등]
           │
           ▼
╔═════════════════════════════════════╗
║     1.Reactor Netty (서버 수신부)     ║ ← 논블로킹 소켓 이벤트
╚═════════════════════════════════════╝
           │
           ▼
╔═════════════════════════════════════╗
║ 2.ReactorHttpHandlerAdapter          ║ ← WebFlux와 Netty 연결
╚═════════════════════════════════════╝
           │
           ▼
╔═════════════════════════════════════╗
║ 3.HttpWebHandlerAdapter              ║ ← Mono/Flux로 HTTP 처리
╚═════════════════════════════════════╝
           │
           ▼
╔═════════════════════════════════════╗
║ 4.DispatcherHandler (Spring 핵심 라우팅)  ║
╚═════════════════════════════════════╝
           │
           ▼
╔═════════════════════════════════════╗
║ 5.@Controller / @RestController       ║ ← 요청 처리 후 Mono/Flux 반환
╚═════════════════════════════════════╝
           │
           ▼
╔═════════════════════════════════════╗
║ 6.반환값 subscribe() → 응답 전송     ║
╚═════════════════════════════════════╝
           │
           ▼
[클라이언트 HTTP 응답 수신]

```

## 각 컴포넌트 별 동작 설명

주요 시퀀스들을 아래에서 상세히 알아 보겠습니다.

1. `ReactorHttpHandlerAdapter`
2. `HttpWebHandlerAdapter`
3. `DispatcherHandler`
4.  `subscribe()`

### 1.`ReactorHttpHandlerAdapter`

- 클래스 위치 : `org.springframework.http.server.reactive.ReactorHttpHandlerAdapter`
- 역할 : Netty와 Webflux의 연결 지점
- 동작 :
  - Netty가 수신한 요청을 `ServerHttpRequest` , `ServerHttpResponse` 로 변환합니다.
  - `WebFlux` 의 `HttpHandler` 인터페이스를 호출합니다.

아래에서 `httpHandler` 는 내부적으로는 `HttpWebHandlerAdaper` 입니다.

```java
public Mono<Void> apply(HttpServerRequest req, HttpServerResponse res){
	return this.httpHandler.handle(req, res);
}
```

### 2. `HttpWebHandlerAdapter`

- 클래스 위치 : `org.springframework.http.server.reactive.HttpWebHandlerAdapter`
- 역할 : WebFlux의 핸들러 실행
- 동작:
  - 요청이 들어오면 “요청을 처리할 전체 작업 흐름”을 담은 `Mono<Void>` 를 반환
  - 내부적으로 비동기 필터 체인을 실행
  - 리턴된 `Mono` 를 `subscribe()` 해서 실행 시작

아래에서 webHandler는 일반적으로 `DispatherHandler` 입니다.

```java
handle(ServerHttpRequest request, ServerHttpResponse response) {
    return this.webHandler.handle(exchange);
}
```

### 3. `DispatcherHandler`

- 클래스 위치 : `org.springframework.web.reactive.DispatcherHandler`
- 역할 : WebFlux의 클라이언트 가 요청한 URL에 맞는 라우터(Controller) 찾기
- 동작 :
  - `HandlerMapping` 으로 라우터를 찾습니다. (예 : `@GetMapping("/users")`)
  - `HandlerAdapter` 로 핸들러를 실행합니다.(매핑된 `@Cotroller` 의 메서드를 실행)
  - 반환된 `Mono/Flux` 를 처리합니다.

```java
handle(ServerWebExchange exchange) {
    return getHandler(exchange)
        .flatMap(handler -> getHandlerAdapter(handler)
            .flatMap(adapter -> adapter.handle(exchange, handler)));
}
```

### 4. `subscribe()` 호출과 응답 전송

WebFlux는 결과 `Mono<Void>` 를 Reactor Netty에 넘기고 Netty는 `Mono.subscribe()` 를 호출하여 요청 처리를 시작합니다.

- `subscribe()` 가 호출되어야 실제 리액티브 체인이 실행됩니다.
- 결과가 `onNext` , `onComplete` 되면 `ServerHttpResponse` 에 논블로킹으로 HTTP 응답을 작성합니다.

# R2DBC란?

R2DBC는 Reactive Relational Datasase Connectity의 약자 입니다.

리액티브 방식으로 관계형 데이터베이스를 다루기 위한 비동기 논블로킹 API입니다.

쉽게 생각하면 웹플럭스에서 Spring MVC의 JDBC와 대응되는 라이브러리 입니다.

아래는 간략하게 두 라이브러리를 비교한 표 입니다.

| 항목 | JDBC | R2DBC |
| --- | --- | --- |
| 동기/비동기 | 동기 (블로킹 I/O) | 비동기 (논블로킹 I/O) |
| 스레드 | 요청마다 스레드 점유 | 스레드 효율적 (Event Loop 기반) |
| 리액티브 스트림 | 미지원 | 지원 (`Mono`, `Flux`) |
| 예외 처리 | `try-catch` 기반 | `onError`, `doOnError` 등 리액티브 방식 |
| 트랜잭션 처리 | 명령형 (`conn.setAutoCommit(false)`) | 리액티브 트랜잭션 API 필요 |

## R2DBC 구성 요소

### 1. R2DBC SPI (Service Provider Interface)

- 인터페이스만 구현하고 실제 구현은 DB 벤더를 이용합니다.
- DB 벤더는 이 규격에 맞춰 구현체를 생성합니다.

### 2. R2DBC Driver

- 관계형 데이터베이스 별 논블로킹 드라이버 입니다.
- 예
  - PostgreSQL: `io.r2dbc:r2dbc-postgresql`
  - MySQL : `dev.miku:r2dbc-mysql` 혹은 `r2dbc-mariadb`

### 3. Spring Data R2DBC

- Spring이 만든 R2DBC용 추상화 입니다.
- JPA 처럼 `@Repository` , `@Query` , `R2dbcEntityTemplate` 등을 제공합니다.

아래는 Spring Data R2DBC와 JPA를 비교한 내용 입니다.

| 항목 | Spring Data JPA | Spring Data R2DBC |
| --- | --- | --- |
| 처리 방식 | 동기 (블로킹 I/O) | 비동기 (논블로킹 I/O) |
| 주요 API | JPA, Hibernate | R2DBC, Reactor (`Mono`, `Flux`) |
| 영속성 컨텍스트 | 지원 (EntityManager) | 미지원 |
| Lazy Loading | 지원 | 미지원 (명시적 쿼리 필요) |
| Dirty Checking | 지원 | 미지원 |
| 트랜잭션 처리 방식 | 명령형 (TransactionManager) | 리액티브 트랜잭션 (reactor context 기반) |
| 사용 대상 | 전통적인 MVC 기반 시스템 | WebFlux 등 리액티브 애플리케이션 |
| 스레드 모델 | 요청마다 스레드 점유 | 이벤트 루프 기반, 스레드 효율적 사용 |
| 데이터 접근 방식 | Entity 중심 | SQL 중심 (`DatabaseClient`, `Template`) |

## R2DBC Connection Pool

R2DBC에서 커넥션 풀은 데이터베이스와의 연결을 효율적으로 관리하기 위한 기능입니다. Spring MVC의 Hikari CP와 대응되는 라이브러리 입니다.

### Connection Pool 이란

- DB 연결을 미리 생성해두고 재사용할 수 있도록 관리하는 기술입니다.
- 각 요청마다 새로운 커넥션을 만드는 대신 풀에서 떠내서 사용하고 반환합니다.
- R2DBC는 비동기/논블로킹 I/O 기반이기 때문에 전통적인 JDBC 커넥션 풀과는 구조와 사용 방식이 다릅니다.

### Connection Pool 동작 방식

- 풀에서 비어 있는 연결을 꺼냅니다.(`getConnection`)
- 비동기 작업을 처리합니다.
- 사용 후 자원을 반환합니다.(`release`)
- 커넥션 풀에서 DB연결을 재사용합니다.

```
┌─────────────────────────────┐
│      R2DBC ConnectionPool   │
└────────────┬───────────────┘
             │
    ┌────────▼─────────┐
    │ Available Pool   │  ←  미리 생성된 연결
    └────────┬─────────┘
             │
    ┌────────▼─────────┐
    │  사용 중 Pool    │  ←  현재 구독 중인 연결
    └──────────────────┘

```

### R2DBC Connection Pool 설정

```yaml
  r2dbc:
    pool:
      name: r2dbc-pool
      enabled: true 
      initial-size: 6 
      max-size: 12 
      max-acquire-time: 10s 
      max-idle-time: 1m 
      max-create-connection-time: 1m 
      max-life-time: 2m 

```

| 설정 항목                        | 설명                                                                         |
|------------------------------| -------------------------------------------------------------------------- |
| `name`                       | 커넥션 풀의 이름입니다. 모니터링이나 로깅 시 구분용으로 사용됩니다.                                     |
| `enabled`                    | 커넥션 풀 사용 여부입니다. 기본값은 `true`입니다.                                            |
| `initial-size`               | 초기 커넥션 수입니다. 풀 사이즈가 0일 때 첫 DB 커넥션을 맺으면 이 값만큼 커넥션이 초기화됩니다.                  |
| `max-size`                   | 커넥션 풀의 최대 커넥션 수입니다. 이 수를 초과하는 요청은 대기(pending) 상태로 들어갑니다.                   |
| `max-acquire-time`           | 커넥션을 풀에서 획득하기 위해 기다릴 수 있는 최대 시간입니다. 이 시간이 지나면 예외가 발생합니다.                   |
| `max-idle-time`              | 커넥션이 유휴 상태로 유지될 수 있는 최대 시간입니다. 초과 시 커넥션은 종료됩니다. `max-life-time`보다 작아야 합니다. |
| `max-create-connection-time` | 새 커넥션을 생성하는 데 허용되는 최대 시간입니다. 이 시간을 초과하면 커넥션 생성이 실패합니다.                     |
| `max-life-time`              | 커넥션의 최대 생존 시간입니다. 이 시간이 지나면 사용 중이더라도 커넥션은 제거되고 새 커넥션으로 교체됩니다.              |
### Connection Pool 디버깅하기

애플리케이션이 운영중인 상황에서 현재 커넥션 풀의 상태를 알고 싶을 때가 있습니다. R2DBC에서는 `ConnectionPool` 객체를 이용하여 현재 사용 중인 Pool 의 상태를 알 수 있습니다.

아래와 같이 커넥션 풀의 상태를 조회할 수 있는 API를 만들 수 있습니다.

```java
@RestController
@RequestMapping("/auth/db")
@RequiredArgsConstructor
public class ConnectionPoolController {
    private final ConnectionPool connectionPool;

    @GetMapping("/pool")
    public Mono<Map<String, Object>> getPoolStatus() {
        PoolMetrics metrics = connectionPool.getMetrics()
                .orElseThrow(() -> new IllegalStateException("ConnectionPoolMetrics is not available"));

        return Mono.just(Map.of(
                "MaxAllocatedSize", metrics.getMaxAllocatedSize(),
                "idleSize", metrics.idleSize(),
                "pendingAcquireSize", metrics.pendingAcquireSize(),
                "acquiredSize", metrics.acquiredSize(),
                "allocatedSize", metrics.allocatedSize()
        ));
    }

}
```

여기서 눈여겨 봐야할 값은 `acquiredSize` 와 `pendingAcquireSize` 값입니다.  `acquiredSize` 은 현재 데이터베이스와 연결되고 트랜잭션 작업을 하고 있는 커넥션 풀이고 `pendingAcquireSize` 대기 중인 작업 큐 개수를 의미 합니다.

충분한 커넥션 풀을 확보하더라도 실제 작업하고 있는 풀이 적고 쌓여있는 작업이 많다면 어디에서 병목 현상이 있을 수 있습니다.

이를 확인하기 위해선 visualVM을 활용해 CPU, Thread, Heap 정보를 같이 확인하는 것이 좋습니다.

아래는 위 API를 호출했을 때의 응답 예시 입니다. 최대 커넥션 풀까지 도달 했지만 대기 중인 큐는 44개나 있고 활동 중인 풀은 1개이며 대기중인 풀은 11개 입니다. 어디선가 문제가 있다는 점을 알 수 있습니다.

```json
{
    "MaxAllocatedSize": 12,
    "acquiredSize": 1,
    "idleSize": 11,
    "allocatedSize": 12,
    "pendingAcquireSize": 44
}
```
## 기타 유용한 설정

웹플럭스에서 작업 쓰레드 개수가 부족하면 BoundedElastic 쓰레드가 생성됩니다. 이 쓰레드의 최대 값은 가용 코어 수 * 10 입니다. 너무 많은 쓰레드를 할당하고 싶지 않다면 vm option에 명시적으로 개수를 제한할 수 있습니다. 또한 BoundedElastic 쓰레드가 사용할 대기 중인 큐의 개수도 제한할 수 있습니다. 아래는 vm 설정 값 입니다.

```
-Dreactor.schedulers.defaultBoundedElasticSize=50
-Dreactor.schedulers.defaultBoundedElasticQueueSize=50000

```