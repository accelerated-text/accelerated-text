Originally, AcceleratedText had no proper way to differentiate users, and in most cases, there's not need for that.

However, a partial solution is being slowly introduced. 
We do not wish to bring the burden of full fledged authorization system here, but it is possible to integrate with existing one.


## Integrating

`AUTH_URL` environment variable is used to specify concrete url. (If parameter is not set - whole auth system is disabled).
- API expects to get all requests with Header `Auth-Token: <actual auth token>`
- Then service does `GET` request with Header: `Authorization: Token <actual auth token>`
- Expected minimum output is JSON: 

```json
{"username": "Some User Name", "group": {"id":  42}}
```

We don't really care about `user-id`, only `group-id` is used internally.
This allows to share same data between several users, and if data sharing is unwanted - simply create unique group for each user.