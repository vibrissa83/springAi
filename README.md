# SpringAI를 이용한 간단한 챗봇
## 소개
SpringAI와 AWS DynamoDB를 활용하여 고객 상담 데이터를 저장하고, AI와 상호작용할 수 있는 간단한 챗봇 프로젝트입니다.
## DynamoDB 테이블 생성
챗봇은 고객 상담 요청과 히스토리를 관리하기 위해 아래와 같이 두 개의 DynamoDB 테이블을 사용합니다.
### CUST_CHAT 테이블 생성
``` powershell
aws dynamodb create-table `
    --table-name CUST_CHAT `
    --attribute-definitions `
        AttributeName=ID,AttributeType=S `
        AttributeName=CUSTOMER_KEY,AttributeType=S `
    --key-schema `
        AttributeName=ID,KeyType=HASH `
    --global-secondary-indexes file://C:/Eddysworkspace/springAi/global-secondary-indexes.json `
    --billing-mode PAY_PER_REQUEST `
    --region ap-northeast-2
```
### CUST_CHAT_HISTORY 테이블 생성
``` powershell
aws dynamodb create-table `
    --table-name CUST_CHAT_HISTORY `
    --attribute-definitions `
        AttributeName=ID,AttributeType=S `
        AttributeName=CUSTOMER_KEY,AttributeType=S `
    --key-schema `
        AttributeName=ID,KeyType=HASH `
    --global-secondary-indexes file://C:/Eddysworkspace/springAi/global-secondary-indexes.json `
    --billing-mode PAY_PER_REQUEST `
    --region ap-northeast-2
```
## 실행 방법
1. **필요한 구성값 설정** `application.yml` 파일에 다음과 같은 AWS 및 OpenAI 관련 키 정보를 채워야 합니다.
``` yaml
   aws:
     dynamodb:
       region: ap-northeast-2
       access-key: <YOUR_AWS_ACCESS_KEY>
       secret-key: <YOUR_AWS_SECRET_KEY>
   spring:
     ai:
       openai:
         api-key: <YOUR_OPENAI_API_KEY>
```
1. **프로젝트 실행** Maven으로 스프링 애플리케이션을 실행합니다:
``` bash
   ./mvnw spring-boot:run
```
1. **웹 브라우저에서 접속** 기본 URL: `http://localhost:8080/chat`

## 참고
이 프로젝트는 학습 및 확장용으로 제작되었습니다. 자유롭게 수정, 재배포가 가능합니다.
