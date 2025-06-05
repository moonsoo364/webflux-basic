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
spring:
  r2dbc:
    pool:
      initial-size: 5
      max-size: 20
      max-idle-time: 30s
```

| 설정 항목 | 설명 |
| --- | --- |
| `initial-size` | 애플리케이션 시작 시 생성할 커넥션 수 |
| `max-size` | 커넥션 풀의 최대 커넥션 수 |
| `max-idle-time` | 유휴 커넥션의 최대 유지 시간 |