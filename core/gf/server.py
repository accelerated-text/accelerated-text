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


def compile_concrete_grammar(path, name, instances):
    for inst in instances:
        instance_path = "{path}/{name}{instance}.gf".format(
                path=path,
                name=name,
                instance=inst["key"]
        )
        with open(instance_path, "w") as f:
            f.write(inst["content"])
            yield instance_path



def compile_grammar(name, abstract, instances):
    with TemporaryDirectory() as tmpdir:
        logger.info("Created temp dir: {}".format(tmpdir))
        abstract_path = "{0}/{1}.gf".format(tmpdir, name)
        with open(abstract_path, "w") as f:
            logger.info("Wrote tmp file: {}".format(abstract_path))
            f.write(abstract["content"])

        concrete_grammars = list(compile_concrete_grammar(tmpdir, name, instances))

        logger.info("Compiling")
        proc = subprocess.Popen(
            "gf --output-dir={path} -make {abstract} {other}".format(
                abstract=abstract_path,
                path=tmpdir,
                other=" ".join(concrete_grammars)
            ),
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
            grammar = pgf.readPGF("{0}/{1}.pgf".format(tmpdir, name))
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
    abstract = data["abstract"]
    instances = data["instances"]
    name = data["name"]

    grammar = compile_grammar(name, abstract, instances)
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
