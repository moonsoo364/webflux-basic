# redis 도커 이미지
windows 에서 docker desktop을 이용해 redis를 설치하기 위해 관련 파일을 담은 디렉터리


## docker-compose.yml
### docker compose 실행법
- docker-compose.yml 파일과 같은 경로에 이름이 redis인 디렉터리 생성 -
- redis.conf를 해당 디렉터리에 복사
- redis.conf에 %{REDS_PW}를 원하는 비밀번호로 변경
- docker-compose.yml 이 있는 위치에서 아래 명령어 실행
```shell
docoker compose up -d
```
- 레디스 이미지가 정상적으로 올라왔다면 ./redis/logs , ./redis/data 디렉터리가 생성됩니다.
