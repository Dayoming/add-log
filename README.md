# A.D.D 로그 수집 서버
> 게임 A.D.D의 플레이 기록 데이터를 수집하기 위한 로그 서버 구축

## 응답 예시

### Success Response

**로그 정상 저장**

```jsx
{
	"code": "SUCCESS",
	"data": null,
	"message": "로그 저장 완료",
	"status": 200,
	"timestamp": "2026-07-07T16:43:10.858871"
}
```

**로그가 비어 있는 경우**

플레이어 컴퓨터에 임시 저장된 이벤트가 없는 경우에도 성공 처리한다.

```jsx
{
  "code": "SUCCESS",
  "data": null,
  "message": "로그가 비어 있습니다.",
  "status": 200,
  "timestamp": "2026-07-07T17:31:36.986006"
}
```

### Error Response

**요청 형식 불일치**

```jsx
{
	"code": "Bad Request",
	"data": null,
	"message": "요청에 필수 데이터('logs')가 포함되어 있지 않거나 비어 있습니다.",
	"status": 400,
	"timestamp": "2026-07-07T17:21:23.836588"
}
```

**데이터베이스 오류 발생**

```jsx
{
	"code": "Internal Server Error",
	"data": null,
	"message": "데이터베이스 처리 중 오류가 발생했습니다.",
	"status": 500,
	"timestamp": "2026-07-07T16:52:47.635481"
}
```
