import logging
import argparse

from wsgiref.util import setup_testing_defaults
from wsgiref.simple_server import make_server

from utils import (response_404, post_request, json_request, json_response)
from gf import generate_results


logger = logging.getLogger("server")


@post_request
@json_request
@json_response
def application(environ, start_response, data):
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
