import json

routes = {}


def response_404(environ, start_response):
    status = "404 NOT FOUND"
    response_headers = []
    start_response(status, response_headers)
    return ""




def route(path, method):
    def inject(fn):
        routes[(method, path)] = fn
        def wrapper(environ, *args, **kwargs):
            return fn(*args, **kwargs)
            # if environ["REQUEST_METHOD"] == "POST":
            #     return fn(environ, *args, **kwargs)
            # else:
            #     return response_404(environ, *args, **kwargs)

        return wrapper
    return inject


def json_request(fn):
    def wrapper(environ, start_response):
        try:
            request_body_size = int(environ.get("CONTENT_LENGTH", 0))
        except ValueError:
            request_body_size = 0

        request_body = environ["wsgi.input"].read(request_body_size)
        return fn(environ, start_response, json.loads(request_body))

    return wrapper


def json_response(fn):
    def wrapper(environ, start_response, *args):
        status = "200 OK"
        try:
            response = fn(environ, start_response, *args)
        except Exception as ex:
            response = {"error": True, "message": str(ex)}

        output = json.dumps(response).encode("UTF-8")
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(output)))
        ]
        start_response(status, response_headers)

        return [output]
    return wrapper
