import logging
import json
import argparse
import subprocess

from io import open

from wsgiref.util import setup_testing_defaults
from wsgiref.simple_server import make_server

from backports.tempfile import TemporaryDirectory

logger = logging.getLogger("GF")


class GFError(RuntimeError):
    pass


try:
    import pgf
except ImportError:
    logger.exception("Failed to import module 'pgf'. It's GrammaticalFramework runtime library which needs to be compiled and installed")

def compile_grammar(name, content):
    with TemporaryDirectory() as tmpdir:
        logger.debug("Created temp dir: {}".format(tmpdir))
        files = ["{0}/{1}.gf".format(tmpdir, k)
                 for k in content.keys()
                 if k != name]
        for k, v in content.items():
            with open("{0}/{1}.gf".format(tmpdir, k), "w", encoding="UTF-8") as f:
                f.write(v.decode('utf-8'))
        
        logger.info("Compiling")
        cmd = "gf -i /opt/gf/lang-utils/ -i /opt/gf/concept-net/ --output-dir={path} -make {files} {main}".format(
                path=tmpdir,
                main="{0}/{1}.gf".format(tmpdir, name),
                files=" ".join(files)
        )
        logger.debug("Compile command: {}".format(cmd))
        proc = subprocess.Popen(
            cmd,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        (result, error) = proc.communicate()

        if proc.returncode != 0:
            logger.error(error)
            return None, error
        else:
            logger.debug("Compiled successfuly! Message: {}".format(result))
            grammar = pgf.readPGF("{0}/{1}.pgf".format(tmpdir, name))
            logger.debug("Languages: {}".format(grammar.languages))
            return grammar, None


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

def generate_variants(expressions, concrete_grammar):
    return list([r
                 for (_, e) in expressions
                 for r in concrete_grammar.linearizeAll(e)])


def generate_expressions(abstract_grammar):
    start_cat = abstract_grammar.startCat
    logger.debug("Start category: {}".format(start_cat))
    expressions = list(abstract_grammar.generateAll(start_cat))
    logger.debug("Expressions: {}".format(expressions))
    return expressions


def generate_results(name, content):
    (grammar, error) = compile_grammar(name, content)
    logger.debug("Grammar: {}".format(grammar))
    if grammar:
        logger.info("Generating")
        expressions = generate_expressions(grammar)
        return [(k, generate_variants(expressions, concrete))
                for k, concrete in grammar.languages.items()]
    else:
        raise GFError(error)


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
