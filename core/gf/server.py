import logging
import argparse

from wsgiref.simple_server import make_server
from wsgiref.util import setup_testing_defaults

from utils import (response_404, json_request, json_response, route, routes)
from gf import generate_results, parse_text, GFError


logger = logging.getLogger("server")



@route("/", "POST")
@json_request
@json_response
def generate(environ, start_response, data):
    content = data["content"]
    name = data["module"]
    try:
        results = generate_results(name, content)
        return {"results": results}
    except GFError as error:
        return {"error": error.message}
    except Exception as ex:
        logger.exception(ex)
        return {"error": str(ex).strip()}



@route("/parse", "POST")
@json_request
@json_response
def parse(environ, start_response, data):
    content = data["content"]
    name = data["module"]
    text = data["text"]
    try:
        results = parse_text(name, content, text)
        return {"results": results}
    except GFError as error:
        return {"error": error.message}
    except Exception as ex:
        logger.exception(ex)
        return {"error": str(ex).strip()}



@route("/health", "GET")
@json_response
def ping(*args):
    return {"status": "OK"}



def application(environ, start_response):
    setup_testing_defaults(environ)
    for (m, p), fn in routes.items():
        if environ["REQUEST_METHOD"] == m and environ["PATH_INFO"] == p:
            return fn(environ, start_response)
    return response_404(environ, start_response)


def main(args):
    httpd = make_server("", args.port, application)
    if args.debug:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)
    logger.info("Serving on port: {}".format(args.port))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logger.info("Stopping server")
        httpd.server_close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--debug", action="store_true")
    parser.add_argument("--port", default=8000, type=int, help="Server port")
    main(parser.parse_args())
