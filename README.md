# OB-BE

# Campick Server

이 프로젝트에는 Swagger(OpenAPI) 문서화가 springdoc-openapi를 통해 포함되어 있습니다.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

설정
- Spring Boot: 3.5.5
- Java: 17
- 라이브러리: org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0

개발 시 보안 설정을 사용하는 경우, Swagger 리소스 경로(`/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`)에 대한 접근을 허용해야 합니다.

배포완료