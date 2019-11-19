import logging
import json
import argparse
import subprocess

from wsgiref.util import setup_testing_defaults
from wsgiref.simple_server import make_server

from backports.tempfile import TemporaryDirectory

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

try:
    import pgf
except ImportError:
    logger.exception("Failed to import module 'pgf'. It's GrammaticalFramework runtime library which needs to be compiled and installed")


def compile_grammar(raw):
    with TemporaryDirectory() as tmpdir:
        logger.info("Created temp dir: {}".format(tmpdir))
        grammar_path = "{}/grammar.cf".format(tmpdir)
        with open(grammar_path, "w") as f:
            logger.info("Wrote tmp file: {}".format(grammar_path))
            f.write(raw)

        logger.info("Compiling")
        proc = subprocess.Popen(
            "gf --output-dir={1} -make {0}".format(grammar_path, tmpdir),
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        (result, error) = proc.communicate()

        if proc.returncode != 0:
            logger.error(error)
            return None
        else:
            logger.debug("Compiled successfuly! Message: {}".format(result))
            grammar = pgf.readPGF("{0}/grammarAbs.pgf".format(tmpdir))
            logger.debug("Languages: {}".format(grammar.languages))
            return grammar


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
def application(environ, start_response, data):
    grammar = compile_grammar(data["content"])
    if grammar:
        logger.info("Generating")
        results = []
        try:
            logger.debug("Start category: {}".format(grammar.startCat))
            expressions = list(grammar.generateAll(grammar.startCat))
            lang = grammar.languages["grammar"]
            logger.debug("Expressions: {}".format(expressions))
            results = list([r
                            for (_, e) in expressions
                            for r in lang.linearizeAll(e)])
        except Exception as ex:
            logger.exception(ex)

        logger.debug("Results: {}".format(results))
        return {"results": results}
    else:
        return {"results": []}

def main(args):
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
    main(parser.parse_args())
