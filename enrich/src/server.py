import logging
import json
import argparse

from wsgiref.util import setup_testing_defaults
from wsgiref.simple_server import make_server

from src.enrich import Enricher
from src.utils import format_result

logger = logging.getLogger("Enrich")


def inject_enricher(fn):
    enricher = Enricher()
    def wrapper(*args, **kwargs):
        return fn(*args, enricher=enricher, **kwargs)
    return wrapper


def response_404(environ, start_response):
    status = "404 NOT FOUND"
    response_headers = []
    start_response(status, response_headers)
    return ""

def post_request(fn):
    def wrapper(environ, *args, **kwargs):
        setup_testing_defaults(environ)
        if environ["REQUEST_METHOD"] == "POST":
            return fn(environ, *args, **kwargs)
        else:
            return response_404(environ, *args, **kwargs)

    return wrapper


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
            logger.exception(ex)
            response = {"error": True, "message": str(ex)}

        output = json.dumps(response).encode("UTF-8")
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(output)))
        ]
        start_response(status, response_headers)

        return [output]
    return wrapper


@post_request
@json_request
@json_response
@inject_enricher
def application(environ, start_response, data, enricher=None):
    text = data["text"].strip(".")
    context = data["context"]
    filtered_context = dict([(k, v) for k, v in context.items()
                             if k is not None and k.strip() != ""])
    logger.debug("Filtered context: {}".format(filtered_context))
    result = enricher.connect_sentences(text)
    # result = enricher.enrich(text, filtered_context, max_iters=50)
    return {"result": format_result(result)}


def main(args):
    if args.debug:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)
    httpd = make_server("", args.port, application)
    logger.info("Serving on port: {}".format(args.port))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logger.info("Stopping server")
        httpd.server_close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", default=8000, type=int, help="Server port")
    parser.add_argument("--debug", action="store_true", help="Debug output")
    main(parser.parse_args())
